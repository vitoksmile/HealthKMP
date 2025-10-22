package com.viktormykhailiv.kmp.health.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.records.ExerciseType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExerciseTypePicker(
    modifier: Modifier = Modifier,
    types: List<ExerciseType> = listOf(
        ExerciseType.Biking,
        ExerciseType.Dancing,
        ExerciseType.Golf,
        ExerciseType.Hiking,
        ExerciseType.Running,
        ExerciseType.Tennis,
        ExerciseType.Yoga,
    ),
    exerciseType: ExerciseType,
    onChanged: (ExerciseType) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        types.forEach { type ->
            val isSelected = type == exerciseType

            FilterChip(
                selected = isSelected,
                onClick = { onChanged(type) },
            ) {
                Text(
                    text = type::class.simpleName.orEmpty(),
                    style = LocalTextStyle.current
                        .copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        ),
                )
            }
        }
    }
}
