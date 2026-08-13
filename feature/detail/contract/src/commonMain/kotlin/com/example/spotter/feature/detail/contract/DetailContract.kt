package com.example.spotter.feature.detail.contract

import com.example.spotter.core.model.Article

object DetailContract {

    data class UiState(
        val article: Article? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    )

    sealed interface Action {
        data object BackClicked : Action
    }
}
