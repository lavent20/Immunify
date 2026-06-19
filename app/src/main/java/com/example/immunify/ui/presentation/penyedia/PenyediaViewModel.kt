package com.example.immunify.ui.presentation.penyedia

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PenyediaViewModel @Inject constructor(
    private val penyediaRepository: PenyediaRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _penyediaList = MutableStateFlow<List<PenyediaVaksin>>(emptyList())
    val penyediaList: StateFlow<List<PenyediaVaksin>> = _penyediaList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    @SuppressLint("MissingPermission")
    fun getAllPenyediaWithLocation() = viewModelScope.launch {
        _isLoading.value = true
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    fetchRealtimeClinics(location)
                } else {
                    _isLoading.value = false
                }
            }
            .addOnFailureListener { _isLoading.value = false }
    }

    private fun fetchRealtimeClinics(location: Location) = viewModelScope.launch {
        penyediaRepository.getRealtimeNearbyClinics(location).collect { result ->
            when (result) {
                is Resource.Loading -> _isLoading.value = true
                is Resource.Success -> {
                    _penyediaList.value = result.data ?: emptyList()
                    _isLoading.value = false
                }
                is Resource.Error -> _isLoading.value = false
            }
        }
    }
}
