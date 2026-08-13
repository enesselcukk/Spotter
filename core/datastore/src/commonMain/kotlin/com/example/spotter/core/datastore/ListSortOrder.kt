package com.example.spotter.core.datastore

enum class ListSortOrder {
    DISTANCE,
    NAME_ASC,
    NAME_DESC,
}

fun listSortOrderFromStorage(value: String?): ListSortOrder =
    when (value) {
        "NAME_ASC" -> ListSortOrder.NAME_ASC
        "NAME_DESC" -> ListSortOrder.NAME_DESC
        else -> ListSortOrder.DISTANCE
    }
