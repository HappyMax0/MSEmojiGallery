package com.happymax.msemojigallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.caverock.androidsvg.SVG
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DetailFragment : Fragment() {

    var name: String? = null
    var hasMultiSkin:Boolean = false
    var collected:Boolean = false
    private var image3D: Bitmap? = null
    private var imageColor: Drawable? = null
    private var imageFlat: Drawable? = null
    private var imageHighContrast: Drawable? = null
    private var tabPosition: Int? = 0
    private var skin:Skin = Skin.Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")  // 从 arguments 中获取数据
            hasMultiSkin = it.getBoolean("hasMultiSkin")
            collected = it.getBoolean("collected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView:View = inflater.inflate(R.layout.fragment_detail, container, false)

        val context = context
        if(name != null && context != null){
            //val mainActivity = activity as MainActivity
            val rootPath = if(hasMultiSkin) "$name/Default" else "$name"
            val files: Array<out String>? = context.assets.list(rootPath)
            if (files != null) {
                for (file in files) {
                    when(file){
                        "3D" -> image3D = Helper.getBitmapImage(context, "$rootPath/$file")
                        "Color" -> imageColor = Helper.getDrawableImage(context, "$rootPath/$file")
                        "Flat" -> imageFlat = Helper.getDrawableImage(context, "$rootPath/$file")
                        "High Contrast" -> imageHighContrast = Helper.getDrawableImage(context, "$rootPath/$file")
                    }
                }
            }
        }

        val imageView = rootView.findViewById<ImageView>(R.id.imageView)
        imageView?.setImageBitmap(image3D)

        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        imageView?.setImageBitmap(image3D)
                    }
                    1 -> {
                        imageView?.setImageDrawable(imageColor)
                        }
                    2 -> {
                        imageView?.setImageDrawable(imageFlat)
                    }
                    3 -> {
                        imageView?.setImageDrawable(imageHighContrast)
                    }
                }

                tabPosition = tab?.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        }
        )

        rootView.findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener{
            val image = imageToBitmap()

            val file = context?.let { it1 -> image?.let { it2 ->
                name?.let { it3 ->
                    saveBitmapToCache(it1,
                        it2, it3
                    )
                }
            } }
            file?.let {
                val uri = getUriFromFile(context, it)
                uri?.let {
                    shareBitmap(context, it)
                }
            }
        }

        val colorRadioGroup:RadioGroup = rootView.findViewById(R.id.colorRadioGroup)
        if(!hasMultiSkin){
            colorRadioGroup.visibility = View.GONE
        }
        else{
            colorRadioGroup.visibility = View.VISIBLE
            val radio_default:RadioButton = rootView.findViewById(R.id.radio_default)
            radio_default.isChecked = true
            radio_default.setOnCheckedChangeListener  { _, isChecked ->
                if(isChecked){
                    skin = Skin.Default
                    updateSkin(imageView, "Default")
                }
            }
            val radioLight:RadioButton = rootView.findViewById(R.id.radio_light)
            radioLight.setOnCheckedChangeListener  { _, isChecked ->
                if(isChecked){
                    skin = Skin.Light
                    updateSkin(imageView, "Light")
                }
            }
            val radioDark:RadioButton = rootView.findViewById(R.id.radio_dark)
            radioDark.setOnCheckedChangeListener  { _, isChecked ->
                if(isChecked){
                    skin = Skin.Dark
                    updateSkin(imageView, "Dark")
                }
            }
            val radioMedium:RadioButton = rootView.findViewById(R.id.radio_medium)
            radioMedium.setOnCheckedChangeListener  { _, isChecked ->
                if(isChecked){
                    skin = Skin.Medium
                    updateSkin(imageView, "Medium")
                }
            }
            val radioMediumLight:RadioButton = rootView.findViewById(R.id.radio_mediumLight)
            radioMediumLight.setOnCheckedChangeListener  { _, isChecked ->
                if(isChecked){
                    skin = Skin.MediumLight
                    updateSkin(imageView, "Medium-Light")
                }
            }
            val radioMediumDark:RadioButton = rootView.findViewById(R.id.radio_mediumDark)
            radioMediumDark.setOnCheckedChangeListener  { _, isChecked ->
                if(isChecked){
                    skin = Skin.MediumDark
                    updateSkin(imageView, "Medium-Dark")
                }
            }
        }

       /* val bottomAppBar: BottomAppBar = rootView.findViewById(R.id.bottomAppBar)

        bottomAppBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_fav -> {

                    return@setOnMenuItemClickListener true
                }

                R.id.action_save -> {

                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }*/

        val saveBtn: ImageButton = rootView.findViewById(R.id.saveBtn)
        saveBtn.setOnClickListener {
            saveImage()
        }

        val favouriteCheckBox:CheckBox = rootView.findViewById(R.id.favouriteCheckBox)
        favouriteCheckBox.isChecked = collected
        favouriteCheckBox.setOnCheckedChangeListener{_, isChecked ->
            val sharedPreferences = context!!.getSharedPreferences("main", Context.MODE_PRIVATE)
            val jsonString = sharedPreferences.getString("collection", "[]")
            val jsonArray = JSONArray(jsonString)
            val collectedEmojiNameArray:MutableList<String> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                collectedEmojiNameArray.add(jsonArray.getString(i))
            }

            if(isChecked){
                name?.let {
                    if(!collectedEmojiNameArray.contains(it))
                        collectedEmojiNameArray.add(it)
                }
            }
            else{
                name?.let {
                    if(collectedEmojiNameArray.contains(it))
                        collectedEmojiNameArray.remove(it)
                }
            }

            val newJSONArray = JSONArray("[]")
            val editor = sharedPreferences.edit()
            for (value in collectedEmojiNameArray) {
                newJSONArray.put(value)
            }
            editor.putString("collection", newJSONArray.toString())
            editor.apply()

            val result = Bundle()
            result.putString("name", name)
            result.putBoolean("collected", isChecked)
            parentFragmentManager.setFragmentResult("favouriteChanged", result)

            //Tab View
            if(activity is MainActivity){
                val mainActivity = activity as MainActivity
                val mainViewModel = ViewModelProvider(mainActivity).get(MainViewModel::class.java)

                val emoji = mainViewModel.emojis.value.first() { it.name == name }
                emoji.collected  = isChecked

                if(mainActivity.currentCategory == EmojiCategory.FAVOURITE){
                    val bundle = bundleOf("category" to mainActivity.currentCategory.name)
                    mainActivity.navController.navigate(R.id.nav_favourites, bundle)
                }
            }
            else if(activity is DetailActivity){
                val resultIntent = Intent()
                resultIntent.putExtra("name", name)
                resultIntent.putExtra("collected", isChecked)
                (activity as DetailActivity).setResult(Activity.RESULT_OK, resultIntent)
            }
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun saveImage(){
        val image = imageToBitmap()
        if(image != null){
            var path = "$name/3D"
            if(!hasMultiSkin){
                path = when(tabPosition){
                    0 -> "$name/3D"
                    1 -> "$name/Color"
                    2 -> "$name/Flat"
                    3 -> "$name/High Contrast"
                    else -> "$name/3D"
                }
            }
            else{
                val dictionary = when(skin) {
                    Skin.Default -> "Default"
                    Skin.Light -> "Light"
                    Skin.Dark -> "Dark"
                    Skin.Medium -> "Medium"
                    Skin.MediumLight -> "Medium-Light"
                    Skin.MediumDark -> "Medium-Dark"
                }
                path = when(tabPosition){
                    0 -> "$name/$dictionary/3D"
                    1 -> "$name/$dictionary/Color"
                    2 -> "$name/$dictionary/Flat"
                    3 -> "$name/Default/High Contrast"
                    else -> "$name/3D"
                }
            }
            saveBitmapToExternal(context, image, path)
        }
    }

    private fun imageToBitmap(): Bitmap?{
        var image = image3D
        if(!hasMultiSkin){
            when(tabPosition){
                0 -> image = image3D
                1 -> image = assetToBitmap(context?.assets, "$name/Color")
                2 -> image = assetToBitmap(context?.assets, "$name/Flat")
                3 -> image = assetToBitmap(context?.assets, "$name/High Contrast")
            }
        }
        else{
            val dictionary = when(skin) {
                Skin.Default -> "Default"
                Skin.Light -> "Light"
                Skin.Dark -> "Dark"
                Skin.Medium -> "Medium"
                Skin.MediumLight -> "Medium-Light"
                Skin.MediumDark -> "Medium-Dark"
            }

            when(tabPosition){
                0 -> image = image3D
                1 -> image = assetToBitmap(context?.assets, "$name/$dictionary/Color")
                2 -> image = assetToBitmap(context?.assets, "$name/$dictionary/Flat")
                3 -> image = assetToBitmap(context?.assets, "$name/Default/High Contrast")
            }
        }
        return image
    }

    private fun updateSkin(imageView:ImageView, skin:String){
        image3D = context?.let { Helper.getBitmapImage(it, "$name/$skin/3D") }
        imageColor = context?.let { Helper.getDrawableImage(it, "$name/$skin/Color") }
        imageFlat = context?.let { Helper.getDrawableImage(it, "$name/$skin/Flat") }
        when(tabPosition){
            0 -> imageView.setImageBitmap(image3D)
            1 -> imageView.setImageDrawable(imageColor)
            2 -> imageView.setImageDrawable(imageFlat)
        }
    }

    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if(drawable == null)
            return null

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, 500, 500)
        drawable.draw(canvas)
        return bitmap
    }

    private fun assetToBitmap(assetManager: AssetManager?, directoryName: String) : Bitmap? {
        if (assetManager == null) return null

        val files: Array<out String>? = assetManager?.list(directoryName)
        if (files != null) {
            val file = files.firstOrNull()
            if (file != null) {
                val inputStream = assetManager.open("$directoryName/$file")
                val svg = SVG.getFromInputStream(inputStream)
                val bitmap = Bitmap.createBitmap(
                    svg.documentWidth.toInt() * 100,
                    svg.documentHeight.toInt() * 100, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                svg.documentWidth *= 100
                svg.documentHeight *= 100
                svg.renderToCanvas(canvas)
                inputStream.close()
                return bitmap
            }
        }
        return null
    }

    private fun getBitmapFromAssets(context: Context?, fileName: String): Bitmap? {
        if (context == null) return null
        val assetManager = context.assets
        try {
            assetManager.open(fileName).use { `is` ->
                return BitmapFactory.decodeStream(`is`)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null // 发生异常时返回 null
        }
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): File? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // 创建目录
            val file = File(cachePath, fileName)
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmapToExternal(context: Context?, bitmap: Bitmap, path: String): File? {
        if(context == null) return null
        val imageFiles: Array<String> = context.assets?.list(path)!!
        if (imageFiles.isNotEmpty()) {
            val imageFile = if (imageFiles.first().lowercase().endsWith(".svg")) {
                imageFiles.first().dropLast(4) + ".png"
            } else {
                imageFiles.first()
            }

            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val msemojiDir = File(picturesDir, "MSEmoji")

            val outFile = File(msemojiDir, imageFile)
            // 确保 MSEmoji 目录存在
            if (!msemojiDir.exists()) {
                msemojiDir.mkdirs()
            }
            val fileOutputStream = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            // 通知图库更新
            MediaScannerConnection.scanFile(
                context,
                arrayOf(outFile.absolutePath),
                null,
                null
            )

            Toast.makeText(context, "Save succeed!", Toast.LENGTH_SHORT).show()

            return  outFile
        }
        return  null
    }

    private fun getUriFromFile(context: Context, file: File): Uri? {
        return try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    private fun shareBitmap(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png" // 设置 MIME 类型
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 授予读取 URI 权限
        }
        context.startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun copyAssetToExternalStorage(context: Context?, path: String) {
        if(context == null) return

        val assetManager = context.assets
        val imageFiles: Array<String> = context.assets?.list(path)!!
        if(imageFiles.isNotEmpty()) {
            val imageFile = imageFiles.first()
            val filePath = "$path/$imageFile"
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val msemojiDir = File(picturesDir, "MSEmoji")

            // 确保 MSEmoji 目录存在
            if (!msemojiDir.exists()) {
                val dirCreated = msemojiDir.mkdirs()
                Log.d("CopyAsset", "MSEmoji directory created: $dirCreated")
            }
            
            val outFile = File(msemojiDir, imageFile)

            try {
                assetManager.open(filePath).use { `in` ->
                    FileOutputStream(outFile).use { out ->
                        val buffer = ByteArray(1024)
                        var read: Int
                        while ((`in`.read(buffer).also { read = it }) != -1) {
                            out.write(buffer, 0, read)
                        }
                        out.flush()
                        // 通知图库更新
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(outFile.absolutePath),
                            null,
                            null
                        )
                        Log.d("CopyAsset", "File copied to: " + outFile.absolutePath)
                        Toast.makeText(context, "Save succeed!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}