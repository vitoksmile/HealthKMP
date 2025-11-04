package com.viktormykhailiv.kmp.health.dataType.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> ValuesPicker(
    modifier: Modifier = Modifier,
    values: List<T>,
    currentValue: T,
    onChanged: (T) -> Unit,
    mapper: (T) -> String,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        values.forEach { value ->
            val isSelected = value == currentValue

            FilterChip(
                selected = isSelected,
                onClick = { onChanged(value) },
            ) {
                Text(
                    text = mapper(value),
                    style = LocalTextStyle.current
                        .copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                )
            }
        }
    }
}
