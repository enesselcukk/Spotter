package com.example.spotter.core.spotui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterDarkChip
import com.example.spotter.core.designsystem.theme.SpotterDarkSearch
import com.example.spotter.core.designsystem.theme.SpotterDarkSurfaceElevated
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.SpotSearchSuggestion
import com.example.spotter.core.spotui.SpotSearchSuggestionKind
import com.example.spotter.core.spotui.spotCategoryLabel

@Composable
fun SpotSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onFocusChanged: (Boolean) -> Unit = {},
    onClear: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val shape = RoundedCornerShape(16.dp)
    val showClear = query.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
            .height(52.dp)
            .clip(shape)
            .background(if (isDark) SpotterDarkSearch else colors.surface)
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.08f) else colors.outline.copy(alpha = 0.4f),
                shape = shape,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpotSearchIcon(tint = colors.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    color = colors.onSurfaceVariant,
                    fontSize = 15.sp,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.onSurface,
                    fontSize = 15.sp,
                ),
                cursorBrush = SolidColor(SpotterBlue),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
            )
        }
        if (showClear) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClear,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                SpotClearIcon(tint = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SpotSearchSuggestionsPanel(
    suggestions: List<SpotSearchSuggestion>,
    onSuggestionSelected: (SpotSearchSuggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (suggestions.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .clip(shape)
            .background(if (isDark) SpotterDarkSurfaceElevated else colors.surface)
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.08f) else colors.outline.copy(alpha = 0.35f),
                shape = shape,
            ),
    ) {
        suggestions.forEachIndexed { index, suggestion ->
            SpotSearchSuggestionRow(
                suggestion = suggestion,
                onClick = { onSuggestionSelected(suggestion) },
            )
            if (index < suggestions.lastIndex) {
                HorizontalDivider(
                    color = if (isDark) Color.White.copy(alpha = 0.06f) else colors.outline.copy(alpha = 0.25f),
                    thickness = 1.dp,
                )
            }
        }
    }
}

@Composable
private fun SpotSearchSuggestionRow(
    suggestion: SpotSearchSuggestion,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpotSearchSuggestionIcon(
            kind = suggestion.kind,
            category = suggestion.category,
            tint = colors.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = suggestion.label,
            color = colors.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        SpotChevronIcon(tint = colors.onSurfaceVariant.copy(alpha = 0.55f))
    }
}

@Composable
private fun SpotSearchSuggestionIcon(
    kind: SpotSearchSuggestionKind,
    category: String?,
    tint: Color,
) {
    when (kind) {
        SpotSearchSuggestionKind.Recent -> SpotHistoryIcon(tint = tint)
        SpotSearchSuggestionKind.Category -> Text(
            text = SpotCategories.icon(category ?: SpotCategories.ALL),
            fontSize = 18.sp,
        )
        SpotSearchSuggestionKind.Spot -> Text(
            text = SpotCategories.icon(category ?: SpotCategories.CHARGING),
            fontSize = 18.sp,
        )
    }
}

@Composable
fun SpotCategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(categories) { category ->
            val selected = category == selectedCategory
            val chipShape = RoundedCornerShape(22.dp)
            val chipBackground = if (isDark) SpotterDarkChip else colors.surface
            val chipBorder = when {
                selected && isDark -> Color.White.copy(alpha = 0.28f)
                selected -> colors.primary.copy(alpha = 0.55f)
                isDark -> Color.White.copy(alpha = 0.08f)
                else -> colors.outline.copy(alpha = 0.45f)
            }

            Row(
                modifier = Modifier
                    .clip(chipShape)
                    .background(chipBackground)
                    .border(1.dp, chipBorder, chipShape)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = SpotCategories.icon(category), fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = spotCategoryLabel(category),
                    color = if (selected) colors.onSurface else colors.onSurfaceVariant,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun SpotSearchIcon(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val radius = size.minDimension * 0.32f
        val center = Offset(size.width * 0.42f, size.height * 0.42f)
        drawCircle(color = tint, radius = radius, center = center, style = Stroke(width = 2.1f, cap = StrokeCap.Round))
        drawLine(
            color = tint,
            start = Offset(center.x + radius * 0.68f, center.y + radius * 0.68f),
            end = Offset(size.width * 0.88f, size.height * 0.88f),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SpotClearIcon(tint: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        drawLine(
            color = tint,
            start = Offset(size.width * 0.2f, size.height * 0.2f),
            end = Offset(size.width * 0.8f, size.height * 0.8f),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(size.width * 0.8f, size.height * 0.2f),
            end = Offset(size.width * 0.2f, size.height * 0.8f),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SpotHistoryIcon(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round)
        val cx = size.width * 0.5f
        val cy = size.height * 0.52f
        val radius = size.minDimension * 0.34f
        drawCircle(color = tint, radius = radius, center = Offset(cx, cy), style = stroke)
        drawLine(
            color = tint,
            start = Offset(cx, cy),
            end = Offset(cx, cy - radius * 0.55f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(cx, cy),
            end = Offset(cx + radius * 0.45f, cy + radius * 0.08f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SpotChevronIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.34f, size.height * 0.18f)
            lineTo(size.width * 0.66f, size.height * 0.5f)
            lineTo(size.width * 0.34f, size.height * 0.82f)
        }
        drawPath(path, color = tint, style = Stroke(width = 2f, cap = StrokeCap.Round))
    }
}
