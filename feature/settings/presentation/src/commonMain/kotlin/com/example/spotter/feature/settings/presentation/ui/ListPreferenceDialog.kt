package com.example.spotter.feature.settings.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.feature.settings.presentation.generated.resources.Res
import com.example.spotter.feature.settings.presentation.generated.resources.settings_dialog_done
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_default_view
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_default_view_list
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_default_view_map
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_preference
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_remember_category
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_sort_distance
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_sort_name_asc
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_sort_name_desc
import com.example.spotter.feature.settings.presentation.generated.resources.settings_list_sort_order
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ListPreferenceDialog(
    state: SettingsUiState,
    onDismiss: () -> Unit,
    onDefaultViewSelected: (DefaultHomeViewMode) -> Unit,
    onSortOrderSelected: (ListSortOrder) -> Unit,
    onRememberCategoryChanged: (Boolean) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.settings_list_preference),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ListPreferenceSectionTitle(stringResource(Res.string.settings_list_default_view))
                SettingsPickerOption(
                    label = stringResource(Res.string.settings_list_default_view_list),
                    selected = state.defaultHomeViewMode == DefaultHomeViewMode.LIST,
                    onClick = { onDefaultViewSelected(DefaultHomeViewMode.LIST) },
                )
                SettingsPickerOption(
                    label = stringResource(Res.string.settings_list_default_view_map),
                    selected = state.defaultHomeViewMode == DefaultHomeViewMode.MAP,
                    onClick = { onDefaultViewSelected(DefaultHomeViewMode.MAP) },
                )

                ListPreferenceSectionTitle(
                    text = stringResource(Res.string.settings_list_sort_order),
                    modifier = Modifier.padding(top = 12.dp),
                )
                SettingsPickerOption(
                    label = stringResource(Res.string.settings_list_sort_distance),
                    selected = state.listSortOrder == ListSortOrder.DISTANCE,
                    onClick = { onSortOrderSelected(ListSortOrder.DISTANCE) },
                )
                SettingsPickerOption(
                    label = stringResource(Res.string.settings_list_sort_name_asc),
                    selected = state.listSortOrder == ListSortOrder.NAME_ASC,
                    onClick = { onSortOrderSelected(ListSortOrder.NAME_ASC) },
                )
                SettingsPickerOption(
                    label = stringResource(Res.string.settings_list_sort_name_desc),
                    selected = state.listSortOrder == ListSortOrder.NAME_DESC,
                    onClick = { onSortOrderSelected(ListSortOrder.NAME_DESC) },
                )

                ListPreferenceToggleRow(
                    title = stringResource(Res.string.settings_list_remember_category),
                    checked = state.rememberLastCategory,
                    onCheckedChange = onRememberCategoryChanged,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.settings_dialog_done))
            }
        },
    )
}

@Composable
private fun ListPreferenceSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(bottom = 4.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
    )
}

@Composable
private fun ListPreferenceToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            ),
        )
    }
}

@Composable
internal fun listPreferenceSummary(
    defaultHomeViewMode: DefaultHomeViewMode,
    listSortOrder: ListSortOrder,
): String {
    val viewLabel = when (defaultHomeViewMode) {
        DefaultHomeViewMode.LIST -> stringResource(Res.string.settings_list_default_view_list)
        DefaultHomeViewMode.MAP -> stringResource(Res.string.settings_list_default_view_map)
    }
    val sortLabel = when (listSortOrder) {
        ListSortOrder.DISTANCE -> stringResource(Res.string.settings_list_sort_distance)
        ListSortOrder.NAME_ASC -> stringResource(Res.string.settings_list_sort_name_asc)
        ListSortOrder.NAME_DESC -> stringResource(Res.string.settings_list_sort_name_desc)
    }
    return "$viewLabel · $sortLabel"
}
