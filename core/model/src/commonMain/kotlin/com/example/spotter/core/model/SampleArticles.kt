package com.example.spotter.core.model

val sampleArticles = listOf(
    Article(
        id = "1",
        title = "Kotlin Multiplatform reaches new milestone",
        description = "Compose Multiplatform brings shared UI to Android, iOS, and desktop.",
        content = "Kotlin Multiplatform enables sharing business logic and UI across platforms.",
        url = "https://kotlinlang.org",
        imageUrl = null,
        publishedAt = "2026-08-08T09:00:00Z",
        source = Source(id = "kotlin", name = "Kotlin Blog"),
        author = "JetBrains",
    ),
    Article(
        id = "2",
        title = "Navigation in Compose Multiplatform",
        description = "Type-safe routes work across Android, iOS, and desktop targets.",
        content = "Navigation Compose supports shared navigation graphs in commonMain.",
        url = "https://developer.android.com",
        imageUrl = null,
        publishedAt = "2026-08-08T10:00:00Z",
        source = Source(id = "android", name = "Android Developers"),
        author = "Google",
    ),
    Article(
        id = "3",
        title = "Room 3 for Kotlin Multiplatform",
        description = "Local persistence with Room and bundled SQLite on all targets.",
        content = "Room 3 generates Kotlin code and supports iOS and JVM with KSP.",
        url = "https://developer.android.com/jetpack/androidx/releases/room3",
        imageUrl = null,
        publishedAt = "2026-08-08T11:00:00Z",
        source = Source(id = "androidx", name = "AndroidX"),
        author = null,
    ),
)
