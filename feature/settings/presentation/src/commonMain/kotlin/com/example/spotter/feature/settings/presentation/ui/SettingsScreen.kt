package com.example.spotter.feature.settings.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.datastore.AppLanguage
import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.core.datastore.ThemeMode
import com.example.spotter.core.designsystem.component.spotterStatusBarsPadding
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.component.SpotterBottomBar
import com.example.spotter.feature.settings.presentation.generated.resources.Res
import com.example.spotter.feature.settings.presentation.generated.resources.settings_app_version
import com.example.spotter.feature.settings.presentation.generated.resources.settings_auto_apply_localization
import com.example.spotter.feature.settings.presentation.generated.resources.settings_dialog_close
import com.example.spotter.feature.settings.presentation.generated.resources.settings_display_units
import com.example.spotter.feature.settings.presentation.generated.resources.settings_distance_kilometers
import com.example.spotter.feature.settings.presentation.generated.resources.settings_distance_miles
import com.example.spotter.feature.settings.presentation.generated.resources.settings_distance_unit
import com.example.spotter.feature.settings.presentation.generated.resources.settings_language
import com.example.spotter.feature.settings.presentation.generated.resources.settings_language_english
import com.example.spotter.feature.settings.presentation.generated.resources.settings_language_system
import com.example.spotter.feature.settings.presentation.generated.resources.settings_language_turkish
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_preference
import com.example.spotter.feature.settings.presentation.generated.resources.settings_section_localization
import com.example.spotter.feature.settings.presentation.generated.resources.settings_section_preferences
import com.example.spotter.feature.settings.presentation.generated.resources.settings_theme_dark
import com.example.spotter.feature.settings.presentation.generated.resources.settings_theme_light
import com.example.spotter.feature.settings.presentation.generated.resources.settings_theme_mode
import com.example.spotter.feature.settings.presentation.generated.resources.settings_theme_system
import com.example.spotter.feature.settings.presentation.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .spotterStatusBarsPadding(),
    ) {
        SettingsContent(
            state = uiState,
            viewModel = viewModel,
            modifier = Modifier.weight(1f),
        )

        SpotterBottomBar(
            selected = SpotterTab.Settings,
            onSelected = viewModel::onTabSelected,
        )
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
            ),
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(28.dp))

        SettingsSectionLabel(stringResource(Res.string.settings_section_preferences))

        SettingsCard {
            SettingsNavigationRow(
                icon = "☰",
                title = stringResource(Res.string.settings_list_preference),
                value = listPreferenceSummary(
                    defaultHomeViewMode = state.defaultHomeViewMode,
                    listSortOrder = state.listSortOrder,
                ),
                onClick = { viewModel.openPicker(SettingsPicker.ListPreference) },
            )
            SettingsDivider()
            SettingsNavigationRow(
                icon = "🌐",
                title = stringResource(Res.string.settings_language),
                value = languageLabel(state.language, state.autoApplyLocalization),
                onClick = { viewModel.openPicker(SettingsPicker.Language) },
            )
            SettingsDivider()
            SettingsNavigationRow(
                icon = "◐",
                title = stringResource(Res.string.settings_theme_mode),
                value = themeModeLabel(state.themeMode),
                onClick = { viewModel.openPicker(SettingsPicker.Theme) },
            )
            SettingsDivider()
            SettingsNavigationRow(
                icon = "📍",
                title = stringResource(Res.string.settings_distance_unit),
                value = distanceUnitLabel(state.distanceUnit),
                onClick = { viewModel.openPicker(SettingsPicker.DistanceUnit) },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSectionLabel(stringResource(Res.string.settings_section_localization))

        SettingsCard {
            SettingsInfoRow(
                title = stringResource(Res.string.settings_display_units),
                subtitle = distanceUnitLabel(state.distanceUnit),
            )
            SettingsDivider()
            SettingsToggleRow(
                title = stringResource(Res.string.settings_auto_apply_localization),
                checked = state.autoApplyLocalization,
                onCheckedChange = viewModel::setAutoApplyLocalization,
            )
        }

        Text(
            text = stringResource(Res.string.settings_app_version),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 12.dp),
            textAlign = TextAlign.Center,
            color = if (colors.background.luminance() < 0.5f) SpotterBlue else SpotterYellow,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    when (state.activePicker) {
        SettingsPicker.Language -> SettingsPickerDialog(
            title = stringResource(Res.string.settings_language),
            onDismiss = viewModel::dismissPicker,
        ) {
            SettingsPickerOption(
                label = stringResource(Res.string.settings_language_english),
                selected = state.language == AppLanguage.ENGLISH.tag && !state.autoApplyLocalization,
                onClick = { viewModel.selectLanguage(AppLanguage.ENGLISH.tag) },
            )
            SettingsPickerOption(
                label = stringResource(Res.string.settings_language_turkish),
                selected = state.language == AppLanguage.TURKISH.tag && !state.autoApplyLocalization,
                onClick = { viewModel.selectLanguage(AppLanguage.TURKISH.tag) },
            )
        }

        SettingsPicker.Theme -> SettingsPickerDialog(
            title = stringResource(Res.string.settings_theme_mode),
            onDismiss = viewModel::dismissPicker,
        ) {
            SettingsPickerOption(
                label = stringResource(Res.string.settings_theme_system),
                selected = state.themeMode == ThemeMode.SYSTEM,
                onClick = { viewModel.selectThemeMode(ThemeMode.SYSTEM) },
            )
            SettingsPickerOption(
                label = stringResource(Res.string.settings_theme_light),
                selected = state.themeMode == ThemeMode.LIGHT,
                onClick = { viewModel.selectThemeMode(ThemeMode.LIGHT) },
            )
            SettingsPickerOption(
                label = stringResource(Res.string.settings_theme_dark),
                selected = state.themeMode == ThemeMode.DARK,
                onClick = { viewModel.selectThemeMode(ThemeMode.DARK) },
            )
        }

        SettingsPicker.DistanceUnit -> SettingsPickerDialog(
            title = stringResource(Res.string.settings_distance_unit),
            onDismiss = viewModel::dismissPicker,
        ) {
            SettingsPickerOption(
                label = stringResource(Res.string.settings_distance_kilometers),
                selected = state.distanceUnit == DistanceUnit.KILOMETERS,
                onClick = { viewModel.selectDistanceUnit(DistanceUnit.KILOMETERS) },
            )
            SettingsPickerOption(
                label = stringResource(Res.string.settings_distance_miles),
                selected = state.distanceUnit == DistanceUnit.MILES,
                onClick = { viewModel.selectDistanceUnit(DistanceUnit.MILES) },
            )
        }

        SettingsPicker.ListPreference -> ListPreferenceDialog(
            state = state,
            onDismiss = viewModel::dismissPicker,
            onDefaultViewSelected = viewModel::selectDefaultHomeViewMode,
            onSortOrderSelected = viewModel::selectListSortOrder,
            onRememberCategoryChanged = viewModel::setRememberLastCategory,
        )

        null -> Unit
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        ),
    )
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 4.dp),
    ) {
        content()
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: String,
    title: String,
    value: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconBadge(icon = icon)
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            color = if (enabled) colors.onSurface else colors.onSurface.copy(alpha = 0.45f),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = value,
            color = colors.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "›",
            modifier = Modifier.padding(start = 8.dp),
            color = colors.onSurfaceVariant,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SpotterYellow,
                checkedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}

@Composable
private fun SettingsIconBadge(icon: String) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = icon, fontSize = 16.sp)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 64.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
    )
}

@Composable
private fun SettingsPickerDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                content()
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.settings_dialog_close))
            }
        },
    )
}

@Composable
internal fun SettingsPickerOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                } else {
                    Color.Transparent
                },
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        if (selected) {
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun languageLabel(language: String, autoApply: Boolean): String {
    if (autoApply) {
        return stringResource(Res.string.settings_language_system)
    }
    return when (AppLanguage.fromTag(language)) {
        AppLanguage.ENGLISH -> stringResource(Res.string.settings_language_english)
        AppLanguage.TURKISH -> stringResource(Res.string.settings_language_turkish)
    }
}

@Composable
private fun themeModeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> stringResource(Res.string.settings_theme_system)
    ThemeMode.LIGHT -> stringResource(Res.string.settings_theme_light)
    ThemeMode.DARK -> stringResource(Res.string.settings_theme_dark)
}

@Composable
private fun distanceUnitLabel(unit: DistanceUnit): String = when (unit) {
    DistanceUnit.KILOMETERS -> stringResource(Res.string.settings_distance_kilometers)
    DistanceUnit.MILES -> stringResource(Res.string.settings_distance_miles)
}
