package com.example.spotter.feature.map.presentation.ui

import androidx.compose.runtime.Composable
import com.example.spotter.feature.map.domain.model.RouteStep
import com.example.spotter.feature.map.presentation.generated.resources.Res
import com.example.spotter.feature.map.presentation.generated.resources.map_step_arrive
import com.example.spotter.feature.map.presentation.generated.resources.map_step_depart
import com.example.spotter.feature.map.presentation.generated.resources.map_step_keep_left
import com.example.spotter.feature.map.presentation.generated.resources.map_step_keep_right
import com.example.spotter.feature.map.presentation.generated.resources.map_step_merge
import com.example.spotter.feature.map.presentation.generated.resources.map_step_ramp
import com.example.spotter.feature.map.presentation.generated.resources.map_step_roundabout
import com.example.spotter.feature.map.presentation.generated.resources.map_step_sharp_left
import com.example.spotter.feature.map.presentation.generated.resources.map_step_sharp_right
import com.example.spotter.feature.map.presentation.generated.resources.map_step_slight_left
import com.example.spotter.feature.map.presentation.generated.resources.map_step_slight_right
import com.example.spotter.feature.map.presentation.generated.resources.map_step_straight
import com.example.spotter.feature.map.presentation.generated.resources.map_step_turn_left
import com.example.spotter.feature.map.presentation.generated.resources.map_step_turn_right
import com.example.spotter.feature.map.presentation.generated.resources.map_step_uturn
import com.example.spotter.feature.map.presentation.generated.resources.map_unnamed_road
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun routeStepInstruction(step: RouteStep): String {
    val roadName = step.roadName.takeIf { it.isNotBlank() }
        ?: stringResource(Res.string.map_unnamed_road)

    return when (step.maneuver) {
        "depart" -> stringResource(Res.string.map_step_depart, roadName)
        "arrive" -> stringResource(Res.string.map_step_arrive)
        "merge" -> stringResource(Res.string.map_step_merge, roadName)
        "on ramp", "off ramp" -> stringResource(Res.string.map_step_ramp, roadName)
        "roundabout", "rotary", "roundabout turn" ->
            stringResource(Res.string.map_step_roundabout, roadName)

        "fork" -> when (step.modifier) {
            "left", "slight left", "sharp left" -> stringResource(Res.string.map_step_keep_left, roadName)
            else -> stringResource(Res.string.map_step_keep_right, roadName)
        }

        else -> when (step.modifier) {
            "left" -> stringResource(Res.string.map_step_turn_left, roadName)
            "right" -> stringResource(Res.string.map_step_turn_right, roadName)
            "slight left" -> stringResource(Res.string.map_step_slight_left, roadName)
            "slight right" -> stringResource(Res.string.map_step_slight_right, roadName)
            "sharp left" -> stringResource(Res.string.map_step_sharp_left, roadName)
            "sharp right" -> stringResource(Res.string.map_step_sharp_right, roadName)
            "uturn" -> stringResource(Res.string.map_step_uturn, roadName)
            else -> stringResource(Res.string.map_step_straight, roadName)
        }
    }
}

internal fun routeStepGlyph(step: RouteStep): String = when (step.maneuver) {
    "depart" -> "◉"
    "arrive" -> "⚑"
    "roundabout", "rotary", "roundabout turn" -> "⟳"
    else -> when (step.modifier) {
        "left", "slight left", "sharp left" -> "↰"
        "right", "slight right", "sharp right" -> "↱"
        "uturn" -> "⤺"
        else -> "↑"
    }
}
