package com.viktormykhailiv.kmp.health.dataType

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.dataType.base.DataTypeScreen
import com.viktormykhailiv.kmp.health.dataType.base.ValuesPicker
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.ExerciseLap
import com.viktormykhailiv.kmp.health.records.ExerciseRoute
import com.viktormykhailiv.kmp.health.records.ExerciseSegment
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.units.meters
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@Composable
fun ExerciseScreen() {
    val exerciseTypes = remember {
        listOf(
            ExerciseType.Biking,
            ExerciseType.Dancing,
            ExerciseType.Golf,
            ExerciseType.Hiking,
            ExerciseType.Running,
            ExerciseType.Tennis,
            ExerciseType.Yoga,
        )
    }

    DataTypeScreen(
        title = "Exercise",
        type = HealthDataType.Exercise(),
        initialValue = { exerciseTypes.random() },
        writer = { exerciseType ->
            val segmentsCount = 5
            val sampleInterval = 10.minutes
            val endTime = Clock.System.now()
            val startTime = endTime.minus(sampleInterval * segmentsCount)

            listOf(
                ExerciseSessionRecord(
                    startTime = startTime,
                    endTime = endTime,
                    exerciseType = exerciseType,
                    title = "Title ${Random.nextInt()}",
                    notes = "Notes ${Random.nextInt()}",
                    segments = List(segmentsCount - 1) {
                        ExerciseSegment(
                            startTime = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                            endTime = startTime.plus((it * sampleInterval.inWholeMinutes + sampleInterval.inWholeMinutes).minutes),
                            segmentType = ExerciseSegment.Type.OtherWorkout,
                            repetitions = Random.nextInt(1, 10),
                        )
                    },
                    laps = List(segmentsCount - 1) {
                        ExerciseLap(
                            startTime = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                            endTime = startTime.plus((it * sampleInterval.inWholeMinutes + sampleInterval.inWholeMinutes).minutes),
                            length = Random.nextInt(
                                1,
                                100
                            ).meters,
                        )
                    },
                    exerciseRoute = run {
                        val latitude =
                            Random.nextDouble(-90.0, 90.0)
                        val longitude =
                            Random.nextDouble(-180.0, 180.0)

                        ExerciseRoute(
                            route = List(segmentsCount - 1) {
                                ExerciseRoute.Location(
                                    time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                    latitude = latitude + it.toDouble() / 100 * (it + 1),
                                    longitude = longitude + it.toDouble() / 100 * (it - 1),
                                    horizontalAccuracy = Random.nextInt(1, 100).meters,
                                    verticalAccuracy = Random.nextInt(1, 100).meters,
                                    altitude = Random.nextInt(1, 100).meters,
                                )
                            },
                        )
                    },
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        pickerContent = { controller ->
            ValuesPicker(
                modifier = Modifier.fillMaxWidth(),
                values = exerciseTypes,
                currentValue = controller.value,
                onChanged = { controller.value = it },
                mapper = { it::class.simpleName.orEmpty() },
            )
        },
        listContent = { exercise ->
            if (exercise.isEmpty()) {
                Text("No exercises yet")
            }

            exercise.forEach { exercise ->
                Text(
                    "${exercise.exerciseType::class.simpleName}, " +
                            "duration ${exercise.endTime - exercise.startTime}"
                )
            }
        },
    )
}
