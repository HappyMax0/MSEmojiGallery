package com.happymax.msemojigallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchResultViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Emoji>>()
    val items: LiveData<List<Emoji>> = _items

    fun updateItems(newItems: List<Emoji>) {
        _items.value = newItems
    }
}
