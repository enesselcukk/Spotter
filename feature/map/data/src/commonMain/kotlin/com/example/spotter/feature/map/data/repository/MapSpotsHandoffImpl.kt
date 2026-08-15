package com.example.spotter.feature.map.data.repository

import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff

class MapSpotsHandoffImpl : MapSpotsHandoff {

    private var spots: List<SpotDto> = emptyList()

    override fun publish(spots: List<SpotDto>) {
        this.spots = spots
    }

    override fun peek(): List<SpotDto> = spots
}
