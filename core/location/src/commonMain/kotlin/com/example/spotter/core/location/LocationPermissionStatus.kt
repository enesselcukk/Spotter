package com.example.spotter.core.location

enum class LocationPermissionStatus {
    Granted,
    Denied,
    Restricted,
    NotDetermined,
}

fun LocationPermissionStatus.isGranted(): Boolean = this == LocationPermissionStatus.Granted
