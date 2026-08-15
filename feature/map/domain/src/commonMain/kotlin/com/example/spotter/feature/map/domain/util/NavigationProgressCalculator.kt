package com.example.spotter.feature.map.domain.util

import com.example.spotter.feature.home.domain.util.GeoDistance
import com.example.spotter.feature.map.domain.model.NavigationProgress
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.RouteStep

object NavigationProgressCalculator {
    private const val ArrivalThresholdMeters = 28.0
    private const val OnRouteThresholdMeters = 400.0

    fun isOnRoute(geometry: List<RoutePoint>, user: RoutePoint): Boolean {
        if (geometry.size < 2) return false
        return closestPointOnRoute(geometry, user).distanceMeters <= OnRouteThresholdMeters
    }

    fun calculate(plan: RoutePlan, user: RoutePoint): NavigationProgress {
        val geometry = plan.geometry
        val steps = plan.steps
        if (geometry.size < 2 || steps.isEmpty()) {
            return NavigationProgress(
                currentStepIndex = 0,
                currentStep = steps.firstOrNull() ?: fallbackArriveStep(),
                distanceToManeuverMeters = 0.0,
                remainingDistanceMeters = 0.0,
                remainingDurationSeconds = 0.0,
                isArrived = true,
            )
        }

        val closest = closestPointOnRoute(geometry, user)
        val remainingDistance = remainingDistanceFrom(geometry, closest)
        val remainingDuration = if (plan.distanceMeters <= 0.0) {
            0.0
        } else {
            remainingDistance / plan.distanceMeters * plan.durationSeconds
        }

        val stepVertices = stepVertexIndices(plan)
        val nextStepIndex = nextManeuverIndex(steps, stepVertices, closest.vertexIndex)
        val currentStep = steps[nextStepIndex]
        val distanceToManeuver = remainingDistanceFrom(
            geometry = geometry,
            from = closest,
            untilVertex = stepVertices[nextStepIndex],
        )
        val destination = geometry.last()
        val isArrived = GeoDistance.metersBetween(
            user.latitude,
            user.longitude,
            destination.latitude,
            destination.longitude,
        ) <= ArrivalThresholdMeters

        return NavigationProgress(
            currentStepIndex = nextStepIndex,
            currentStep = currentStep,
            distanceToManeuverMeters = if (isArrived) 0.0 else distanceToManeuver,
            remainingDistanceMeters = remainingDistance,
            remainingDurationSeconds = remainingDuration,
            isArrived = isArrived,
        )
    }

    private fun nextManeuverIndex(
        steps: List<RouteStep>,
        stepVertices: List<Int>,
        userVertex: Int,
    ): Int {
        val ahead = stepVertices.indices.firstOrNull { index ->
            val step = steps[index]
            if (step.maneuver == "depart") return@firstOrNull false
            stepVertices[index] > userVertex
        }
        return ahead ?: steps.lastIndex
    }

    private fun stepVertexIndices(plan: RoutePlan): List<Int> {
        var travelled = 0.0
        return plan.steps.map { step ->
            val vertex = step.location
                ?.let { nearestVertexIndex(plan.geometry, it) }
                ?: vertexAtDistance(plan.geometry, travelled)
            travelled += step.distanceMeters
            vertex
        }
    }

    private data class ClosestPoint(
        val vertexIndex: Int,
        val latitude: Double,
        val longitude: Double,
        val distanceMeters: Double,
    )

    private fun closestPointOnRoute(
        geometry: List<RoutePoint>,
        user: RoutePoint,
    ): ClosestPoint {
        var best = ClosestPoint(
            vertexIndex = 0,
            latitude = geometry.first().latitude,
            longitude = geometry.first().longitude,
            distanceMeters = Double.MAX_VALUE,
        )

        for (index in 0 until geometry.lastIndex) {
            val start = geometry[index]
            val end = geometry[index + 1]
            val projected = projectOnSegment(user, start, end)
            val distance = GeoDistance.metersBetween(
                user.latitude,
                user.longitude,
                projected.latitude,
                projected.longitude,
            )
            if (distance < best.distanceMeters) {
                best = ClosestPoint(index, projected.latitude, projected.longitude, distance)
            }
        }
        return best
    }

    private fun projectOnSegment(
        point: RoutePoint,
        start: RoutePoint,
        end: RoutePoint,
    ): RoutePoint {
        val abLat = end.latitude - start.latitude
        val abLon = end.longitude - start.longitude
        val abSquared = abLat * abLat + abLon * abLon
        if (abSquared == 0.0) return start

        val t = (
            (point.latitude - start.latitude) * abLat +
                (point.longitude - start.longitude) * abLon
            ) / abSquared
        val clamped = t.coerceIn(0.0, 1.0)
        return RoutePoint(
            latitude = start.latitude + abLat * clamped,
            longitude = start.longitude + abLon * clamped,
        )
    }

    private fun remainingDistanceFrom(
        geometry: List<RoutePoint>,
        from: ClosestPoint,
        untilVertex: Int = geometry.lastIndex,
    ): Double {
        val endVertex = untilVertex.coerceIn(from.vertexIndex, geometry.lastIndex)
        if (endVertex == from.vertexIndex) {
            val target = geometry[endVertex]
            return GeoDistance.metersBetween(
                from.latitude,
                from.longitude,
                target.latitude,
                target.longitude,
            )
        }

        var total = GeoDistance.metersBetween(
            from.latitude,
            from.longitude,
            geometry[from.vertexIndex + 1].latitude,
            geometry[from.vertexIndex + 1].longitude,
        )
        var index = from.vertexIndex + 1
        while (index < endVertex) {
            val current = geometry[index]
            val next = geometry[index + 1]
            total += GeoDistance.metersBetween(
                current.latitude,
                current.longitude,
                next.latitude,
                next.longitude,
            )
            index += 1
        }
        return total
    }

    private fun nearestVertexIndex(geometry: List<RoutePoint>, point: RoutePoint): Int {
        var bestIndex = 0
        var bestDistance = Double.MAX_VALUE
        geometry.forEachIndexed { index, candidate ->
            val distance = GeoDistance.metersBetween(
                point.latitude,
                point.longitude,
                candidate.latitude,
                candidate.longitude,
            )
            if (distance < bestDistance) {
                bestDistance = distance
                bestIndex = index
            }
        }
        return bestIndex
    }

    private fun vertexAtDistance(geometry: List<RoutePoint>, meters: Double): Int {
        if (meters <= 0.0) return 0
        var travelled = 0.0
        for (index in 0 until geometry.lastIndex) {
            val current = geometry[index]
            val next = geometry[index + 1]
            travelled += GeoDistance.metersBetween(
                current.latitude,
                current.longitude,
                next.latitude,
                next.longitude,
            )
            if (travelled >= meters) return index + 1
        }
        return geometry.lastIndex
    }

    private fun fallbackArriveStep() = RouteStep(
        maneuver = "arrive",
        modifier = null,
        roadName = "",
        distanceMeters = 0.0,
        durationSeconds = 0.0,
        location = null,
    )
}
