package com.example.copernisea.map

import android.os.Parcel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.copernisea.map.data.FileListDataSource
import com.example.copernisea.map.data.provideFileListDataSource
import com.example.copernisea.map.utils.ApiResult
import com.example.copernisea.map.utils.DateUtils
import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val fileListDataSource: FileListDataSource =
        provideFileListDataSource() //Could be injected by DI

    private lateinit var availableDates: Map<String, Layer>

    private val _state = MutableLiveData<MapState>(MapState.Loading)
    val state: LiveData<MapState> = _state

    fun init(folderName: String) {
        viewModelScope.launch {
            val response = fileListDataSource.getFileLists(folderName)
            if (response is ApiResult.Error) {
                _state.value = MapState.Error(response.throwable.message ?: "")
            } else {
                availableDates = (response as ApiResult.Success).result.availableDates.associate {
                    it.date to Layer(
                        response.result.baseUrl + it.layerFileName,
                        response.result.baseUrl + it.scaleFileName
                    )
                }
                _state.value = MapState.FinishedLoading
            }
        }
    }

    fun setDate(millis: Long) {
        val newDate = DateUtils.formatDate(millis)
        _state.value = MapState.ShowLayer(
            availableDates[newDate]!!.url,
            availableDates[newDate]!!.scaleUrl,
            newDate
        )
    }

    fun getCalendarConstraints(): CalendarConstraints {
        val dateValidator = object : CalendarConstraints.DateValidator {
            override fun isValid(millis: Long): Boolean {
                return availableDates.containsKey(DateUtils.formatDate(millis))
            }

            override fun writeToParcel(dest: Parcel?, flags: Int) {
            }

            override fun describeContents(): Int {
                return 0
            }

        }
        return CalendarConstraints.Builder().setValidator(dateValidator).build()
    }
}

data class Layer(
    val url: String,
    val scaleUrl: String
)