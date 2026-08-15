package com.example.spotter.feature.map.domain.repository

import com.example.spotter.feature.home.domain.model.SpotDto

interface MapSpotsHandoff {
    fun publish(spots: List<SpotDto>)

    fun peek(): List<SpotDto>
}
