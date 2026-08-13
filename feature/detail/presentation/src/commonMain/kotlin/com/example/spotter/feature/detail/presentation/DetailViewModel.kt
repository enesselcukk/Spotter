package com.example.spotter.feature.detail.presentation

import androidx.lifecycle.ViewModel
import com.example.spotter.feature.detail.contract.DetailContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailViewModel(
    private val articleId: String,
) : ViewModel() {
}
