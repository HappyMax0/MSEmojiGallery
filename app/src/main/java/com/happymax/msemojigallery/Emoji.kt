package com.happymax.msemojigallery

import android.graphics.Bitmap

data class Emoji(val name:String, val category: EmojiCategory, val image3D:Bitmap?, val hasMultiSkin:Boolean, var collected:Boolean)
