package com.happymax.msemojigallery

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

data class Emoji(val name:String, val category: EmojiCategory, val image3D:Bitmap?, val hasMultiSkin:Boolean)
