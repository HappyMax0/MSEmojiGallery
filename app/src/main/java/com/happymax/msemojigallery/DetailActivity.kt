package com.happymax.msemojigallery

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.ui.AppBarConfiguration
import com.happymax.msemojigallery.databinding.ActivityDetailBinding
import com.happymax.msemojigallery.databinding.ActivityMainBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val name:String? = intent.getStringExtra("name")
        val hasMultiSkin:Boolean = intent.getBooleanExtra("hasMultiSkin", false)
        val collected:Boolean = intent.getBooleanExtra("collected", false)
        //setContentView(R.layout.activity_detail)

        // 设置 Toolbar
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 隐藏 ActionBar
        supportActionBar?.hide()

        // 设置 Toolbar 或者其他自定义的标题栏（如果有的话）
        binding.toolbar.title = name
        setSupportActionBar(binding.toolbar)
        //sets(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

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