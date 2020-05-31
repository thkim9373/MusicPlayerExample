package com.hoony.musicplayerexample.view_model

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import java.util.*

class MainViewModel(private val state: SavedStateHandle, application: Application) :
    AndroidViewModel(application) {

    private val _fragmentsStackMutableLiveData = MutableLiveData<Stack<Fragment>>()

    val fragmentsStackLiveData: LiveData<Stack<Fragment>>
        get() = _fragmentsStackMutableLiveData
}