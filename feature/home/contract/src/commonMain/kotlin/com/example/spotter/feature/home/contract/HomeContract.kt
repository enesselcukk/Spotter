package com.example.spotter.feature.home.contract

import com.example.spotter.core.model.Article

object HomeContract {

    data class UiState(
        val articles: List<Article> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    )

    sealed interface Action {
        data class ArticleClicked(val articleId: String) : Action
    }
}
