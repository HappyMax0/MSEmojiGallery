package com.happymax.msemojigallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.view.View
import com.caverock.androidsvg.SVG
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


object Helper {
    fun GetEmojis(context: Context, emojiCategory: EmojiCategory):MutableList<Emoji>{
        val emojis:MutableList<Emoji> = ArrayList()
        try {
            val sharedPreferences = context!!.getSharedPreferences("main", Context.MODE_PRIVATE)
            val jsonString = sharedPreferences.getString("collection", "[]")
            val jsonArray = JSONArray(jsonString)
            val collectedEmojiNameArray:MutableList<String> = java.util.ArrayList()
            for (i in 0 until jsonArray.length()) {
                collectedEmojiNameArray.add(jsonArray.getString(i))
            }

            val files: Array<out String>? = context.assets.list("") // "" 表示根目录
            if (files != null) {
                for (file in files) {
                    // 检查是否为文件夹
                    val subFiles: Array<String> = context.assets.list(file)!!
                    if (subFiles != null) {
                        var emoji:Emoji
                        var image3D: Bitmap? = null
                        var category:EmojiCategory = EmojiCategory.ALL
                        var hasMultiSkin = false
                        // 遍历子文件夹
                        for (subFile in subFiles) {
                            if (subFile == "3D") {
                                // 找到名为 "3D" 的文件夹
                                image3D = getBitmapImage(context, "$file/$subFile")
                            }
                            else if(subFile == "metadata.json"){
                                val jsonObject = readJsonFromAssets(context, "$file/$subFile")
                                if (jsonObject != null) {
                                    // 处理 JSON 数据
                                    val group = jsonObject.getString("group")
                                    category = if (group == "Activities")
                                        EmojiCategory.ACTIVITIES
                                    else if(group == "Animals & Nature")
                                        EmojiCategory.ANIMALS_AND_NATURE
                                    else if(group == "Smileys & Emotion")
                                        EmojiCategory.SMILEYS_AND_EMOTION
                                    else if(group == "People & Body")
                                        EmojiCategory.PEOPLE_AND_BODY
                                    else if(group == "Food & Drink")
                                        EmojiCategory.FOOD_AND_DRINK
                                    else if(group == "Travel & Places")
                                        EmojiCategory.TRAVEL_AND_PLACES
                                    else if(group == "Objects")
                                        EmojiCategory.OBJECTS
                                    else if(group == "Symbols")
                                        EmojiCategory.SYMBOLS
                                    else if(group == "Flags")
                                        EmojiCategory.FLAGS
                                    else EmojiCategory.ALL
                                    // ...
                                    try {
                                        if(jsonObject.has("unicodeSkintones")){
                                            val unicodeSkintones = jsonObject.getJSONArray("unicodeSkintones")
                                            if(unicodeSkintones != null && unicodeSkintones.length() > 0){
                                                hasMultiSkin = true
                                            }
                                        }
                                    }catch (ex:Exception){
                                        hasMultiSkin = false
                                    }
                                }
                            }
                            else if(subFile == "Default"){
                                val imageFiles: Array<String> = context.assets?.list("$file/$subFile/3D")!!
                                image3D = getBitmapImage(context, "$file/$subFile/3D")
                            }
                        }

                        if(category == emojiCategory || emojiCategory == EmojiCategory.ALL){
                            emoji = Emoji(file, category, image3D, hasMultiSkin, collectedEmojiNameArray.contains(file))
                            emojis.add(emoji)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return emojis
    }

    fun getBitmapImage(context: Context, path:String): Bitmap? {
        var image: Bitmap? = null

        val imageFiles: Array<String> = context.assets?.list(path)!!
        if(imageFiles.isNotEmpty()){
            val imageFile = imageFiles.first()
            // 检查是否为图片文件（根据文件扩展名判断）
            val imagePath = "$path/$imageFile"
            if (imageFile.endsWith(".jpg") || imageFile.endsWith(".png")) {
                // 加载图片
                val inputStream: InputStream = context.assets.open(imagePath)
                image = BitmapFactory.decodeStream(inputStream)
                // 使用 Bitmap 显示图片或进行其他操作
                // ...
                inputStream.close()
            }
        }
        return image
    }

    fun getDrawableImage(context: Context, path:String): Drawable? {
        var image: Drawable? = null

        val imageFiles: Array<String> = context.assets?.list(path)!!

        if(imageFiles.isNotEmpty()){
            val imageFile = imageFiles.first()
            // 检查是否为图片文件（根据文件扩展名判断）
            val imagePath = "$path/$imageFile"
            if(imageFile.endsWith(".svg"))
            {
                try {
                    // 读取SVG文件
                    val inputStream: InputStream = context.assets.open(imagePath)
                    val svg = SVG.getFromInputStream(inputStream)
                    // 将SVG渲染成Drawable
                    val pictureDrawable = PictureDrawable(svg.renderToPicture())
                    image = pictureDrawable
                    inputStream.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        return image
    }


    private fun readJsonFromAssets(context: Context, fileName: String): JSONObject? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun CalculateSpanCount(view:View): Int {
        val displayMetrics = view.resources.displayMetrics
        val screenWidthDp = view.width / displayMetrics.density
        val columnWidthDp = 90 // 每列的宽度（dp）

        return (screenWidthDp / columnWidthDp).toInt().coerceAtLeast(1)
    }
}