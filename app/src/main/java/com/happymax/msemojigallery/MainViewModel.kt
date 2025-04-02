package com.happymax.msemojigallery

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayList

class MainViewModel : ViewModel() {
    //lateinit var emojis:MutableList<Emoji>
//    private val emojis = MutableLiveData<MutableList<Emoji>>()
    private val _emojis = MutableStateFlow<MutableList<Emoji>>(ArrayList())
    val emojis: StateFlow<MutableList<Emoji>> = _emojis.asStateFlow()

    var dataSetted = false

    fun setSharedData(data: MutableList<Emoji>) {
        if(!dataSetted){
            emojis.value.addAll(data)
            dataSetted = true
        }
    }
}