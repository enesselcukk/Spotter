package com.example.spotter.core.datastore

enum class AppLanguage(val tag: String) {
    ENGLISH("en"),
    TURKISH("tr"),
    ;

    companion object {
        fun fromTag(tag: String): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: ENGLISH

        fun normalizeDeviceTag(tag: String): String =
            when (tag.lowercase().substringBefore('-')) {
                TURKISH.tag -> TURKISH.tag
                else -> ENGLISH.tag
            }
    }
}
