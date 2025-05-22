package com.happymax.msemojigallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.happymax.msemojigallery.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import java.util.Locale


class MainActivity : AppCompatActivity() {
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var currentCategory: EmojiCategory = EmojiCategory.FAVOURITE
    lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeValue = sharedPreferences.getString("theme", "system")
        when (themeValue) {
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        if(!mainViewModel.dataSetted)
            mainViewModel.setSharedData(Helper.GetEmojis(this, EmojiCategory.ALL))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 隐藏 ActionBar
        supportActionBar?.hide()

        // 设置 Toolbar 或者其他自定义的标题栏（如果有的话）
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        setSupportActionBar(binding.appBarMain.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. This solution sets
            // only the bottom, left, and right dimensions, but you can apply whichever
            // insets are appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_favourites,
                R.id.nav_smileys_and_emotion, R.id.nav_people_and_body,
                R.id.nav_animals_and_nature, R.id.nav_food_and_drink,
                R.id.nav_travel_and_places, R.id.nav_activities,
                R.id.nav_objects, R.id.nav_symbols, R.id.nav_flags, R.id.nav_search
            ), drawerLayout
        )

        navView.setNavigationItemSelectedListener { item ->
            val category = when (item.itemId) {
                R.id.nav_smileys_and_emotion -> {
                    EmojiCategory.SMILEYS_AND_EMOTION
                }
                R.id.nav_people_and_body -> {
                    EmojiCategory.PEOPLE_AND_BODY
                }
                R.id.nav_animals_and_nature -> {
                    EmojiCategory.ANIMALS_AND_NATURE
                }
                R.id.nav_food_and_drink -> {
                    EmojiCategory.FOOD_AND_DRINK
                }
                R.id.nav_travel_and_places -> {
                    EmojiCategory.TRAVEL_AND_PLACES
                }
                R.id.nav_activities -> {
                    EmojiCategory.ACTIVITIES
                }
                R.id.nav_objects -> {
                   EmojiCategory.OBJECTS
                }
                R.id.nav_symbols -> {
                   EmojiCategory.SYMBOLS
                }
                R.id.nav_flags -> {
                    EmojiCategory.FLAGS
                }
                R.id.nav_favourites ->{
                    EmojiCategory.FAVOURITE
                }
                else -> {
                    EmojiCategory.ALL
                }
            }

            currentCategory = category

            val bundle = bundleOf("category" to category.name)
            navController.navigate(item.itemId, bundle)

            drawerLayout.closeDrawers()

            true
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)

        navView.setCheckedItem(R.id.nav_favourites)

        launcher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val name = data.getStringExtra("name")
                    val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
                    val emoji = mainViewModel.emojis.value.first() { it.name == name }
                    if(emoji != null){
                        val collected = data.getBooleanExtra("collected", emoji.collected)
                        emoji.collected = collected

                        if(currentCategory == EmojiCategory.FAVOURITE){
                            val bundle = bundleOf("category" to currentCategory.name)
                            navController.navigate(R.id.nav_favourites, bundle)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        // Configure the search info and add any event listeners.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // 提交搜索时的处理
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // 实时监听输入变化（可用于自动提示）
                performSearch(newText)
                return false
            }
        })

        // Define the listener.
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when the action item collapses.
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.popBackStack()  // 返回上一个 destination（即之前 NavigationView 选中的页面）
                return true // Return true to collapse the action view.
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when it expands.
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_search)

                return true // Return true to expand the action view.
            }
        }

        // Assign the listener to that action item.
        searchItem?.setOnActionExpandListener(expandListener)

        // For anything else you have to do when creating the options menu,
        // do the following:

        return super.onCreateOptionsMenu(menu)
    }

    private fun performSearch(query: String) {
        // 例如过滤 RecyclerView 列表内容
        Log.d("Search", "搜索关键词: $query")

        if(query.isBlank()) return

        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val emojis = mainViewModel.emojis.value.filter{ it.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))}

        Log.d("Search", "结果数量: ${emojis.count()}")

        val viewModel = ViewModelProvider(this).get(SearchResultViewModel::class.java)
        viewModel.updateItems(emojis)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.action_settings -> {
                navigateToSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToSettings(){
       val detailFragment = this.findViewById<View>(R.id.detail_fragment)  //supportFragmentManager.findFragmentByTag("detailFragment")
        if(detailFragment != null){
            // 在平板模式下，更新 CategoryFragment 显示
            val manager = this.supportFragmentManager
            //val detailFragment = manager.findFragmentById(R.id.detailFragment) as? DetailFragment
            val settingsFragment = SettingsActivity.SettingsFragment()
            manager.beginTransaction()
                .replace(R.id.detail_fragment, settingsFragment)
                .commit()
        }
        else{
            val intent = Intent(this, SettingsActivity::class.java)
            this.launcher?.launch(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}