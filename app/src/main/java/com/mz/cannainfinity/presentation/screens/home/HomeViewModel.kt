package com.mz.cannainfinity.presentation.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mz.cannainfinity.data.Entries
import com.mz.cannainfinity.data.MongoDB
import com.mz.cannainfinity.model.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel @Inject constructor(

) : ViewModel() {

    var entries: MutableState<Entries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllEntries()
    }

    private fun observeAllEntries() {
        viewModelScope.launch {
            MongoDB.getAllEntries().collect() { result ->
                entries.value = result
            }
        }
    }
}