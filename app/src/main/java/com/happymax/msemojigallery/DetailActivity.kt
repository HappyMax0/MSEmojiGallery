package com.happymax.msemojigallery

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceManager
import com.happymax.msemojigallery.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var category:String

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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

        //setContentView(R.layout.activity_detail)
        val cate = intent.getStringExtra("category")
        category = cate ?: getString(R.string.app_name)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // 隐藏 ActionBar
        supportActionBar?.hide()
        // 设置 Toolbar 或者其他自定义的标题栏（如果有的话）
        toolbar.title = getString(R.string.app_name)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

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

        UpdateEmoji(intent);
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // 更新 Intent

        UpdateEmoji(intent)
    }

    fun UpdateEmoji(intent: Intent){
        val name:String? = intent.getStringExtra("name")
        val cate:String? = intent.getStringExtra("category")
        val hasMultiSkin:Boolean = intent.getBooleanExtra("hasMultiSkin", false)
        val collected:Boolean = intent.getBooleanExtra("collected", false)
        category = cate ?: getString(R.string.app_name)
        val manager = supportFragmentManager
        val detailFragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putString("name", name)  // 传递参数
                putBoolean("hasMultiSkin", hasMultiSkin)
                putBoolean("collected", collected)
            }
        }
        manager.beginTransaction()
            .replace(R.id.detail_fragment, detailFragment)
            .commit()
    }
}