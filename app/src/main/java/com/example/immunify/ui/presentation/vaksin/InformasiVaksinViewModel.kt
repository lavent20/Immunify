package com.example.immunify.ui.presentation.vaksin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.repository.VaksinRepository
import com.example.immunify.model.Vaksin
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InformasiVaksinViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository
) : ViewModel() {

    private val _vaksin = mutableStateOf(Vaksin())
    val vaksin: State<Vaksin> = _vaksin

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isShowDialog = mutableStateOf(false)
    val isShowDialog: State<Boolean> = _isShowDialog

    fun getVaksinDetail(id: String) = viewModelScope.launch {
        vaksinRepository.getVaksinById(id).collect { result ->
            when (result) {
                is Resource.Loading -> _isLoading.value = true
                is Resource.Success -> {
                    // Karena VaksinModelResponse membungkus item Vaksin
                    _vaksin.value = result.data?.item ?: Vaksin()
                    _isLoading.value = false
                }
                is Resource.Error -> {
                    _isLoading.value = false
                }
            }
        }
    }

    fun setDialog(state: Boolean) {
        _isShowDialog.value = state
    }
}