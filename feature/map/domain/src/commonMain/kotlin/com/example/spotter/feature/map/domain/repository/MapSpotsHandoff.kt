package com.example.spotter.feature.map.domain.repository

import com.example.spotter.feature.home.domain.model.SpotDto

/**
 * Hands the already loaded spot list from the list screen to the map screen so the map
 * does not have to hit the network again for data the user is already looking at.
 */
interface MapSpotsHandoff {
    fun publish(spots: List<SpotDto>)

    fun peek(): List<SpotDto>
}
