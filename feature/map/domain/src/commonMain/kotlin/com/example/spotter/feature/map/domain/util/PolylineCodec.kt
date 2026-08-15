package com.example.spotter.feature.map.domain.util

import com.example.spotter.feature.map.domain.model.RoutePoint
import kotlin.math.pow

/**
 * Decoder for the Google/OSRM encoded polyline format.
 */
object PolylineCodec {

    fun decode(encoded: String, precision: Int = 5): List<RoutePoint> {
        if (encoded.isEmpty()) return emptyList()

        val factor = 10.0.pow(precision)
        val points = ArrayList<RoutePoint>(encoded.length / 4)
        var index = 0
        var latitude = 0
        var longitude = 0

        while (index < encoded.length) {
            var shift = 0
            var chunk: Int
            var value = 0

            do {
                chunk = encoded[index++].code - CHAR_OFFSET
                value = value or ((chunk and CONTINUATION_MASK.inv()) shl shift)
                shift += 5
            } while (chunk >= CONTINUATION_MASK && index < encoded.length)
            latitude += if (value and 1 != 0) (value shr 1).inv() else value shr 1

            shift = 0
            value = 0
            do {
                chunk = encoded[index++].code - CHAR_OFFSET
                value = value or ((chunk and CONTINUATION_MASK.inv()) shl shift)
                shift += 5
            } while (chunk >= CONTINUATION_MASK && index < encoded.length)
            longitude += if (value and 1 != 0) (value shr 1).inv() else value shr 1

            points.add(RoutePoint(latitude / factor, longitude / factor))
        }

        return points
    }

    private const val CHAR_OFFSET = 63
    private const val CONTINUATION_MASK = 0x20
}
