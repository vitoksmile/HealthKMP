package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Length
import kotlinx.datetime.Instant

/**
 * Captures a route associated with an exercise session a user does.
 *
 * Contains a sequence of location points, with timestamps, which do not have to be in order.
 *
 * Location points contain a timestamp, longitude, latitude, and optionally altitude, horizontal and
 * vertical accuracy.
 */
data class ExerciseRoute(val route: List<Location>) {

    init {
        val sortedRoute = route.sortedBy { it.time }
        for (i in 0 until sortedRoute.lastIndex) {
            require(sortedRoute[i].time < sortedRoute[i + 1].time)
        }
    }

    /**
     * Represents a single location point recorded during an exercise.
     *
     * @param time The point in time when the location was recorded; Required field.
     * @param latitude Latitude of the location point; Required field; Valid range [-90; 90]
     * @param longitude Longitude of the location point; Required field; Valid range [-180; 180]
     * @param altitude in [Length] unit. Optional field.
     * @param horizontalAccuracy in [Length] unit. Optional field. Valid range: non-negative
     *   numbers.
     * @param verticalAccuracy in [Length] unit. Optional field. Valid range: non-negative numbers.
     */
    data class Location(
        val time: Instant,
        val latitude: Double,
        val longitude: Double,
        val horizontalAccuracy: Length? = null,
        val verticalAccuracy: Length? = null,
        val altitude: Length? = null
    ) {

        private companion object {
            const val MIN_LONGITUDE = -180.0
            const val MAX_LONGITUDE = 180.0
            const val MIN_LATITUDE = -90.0
            const val MAX_LATITUDE = 90.0
        }

        init {
            latitude.requireNotLess(other = MIN_LATITUDE, name = "latitude")
            latitude.requireNotMore(other = MAX_LATITUDE, name = "latitude")
            longitude.requireNotLess(other = MIN_LONGITUDE, name = "longitude")
            longitude.requireNotMore(other = MAX_LONGITUDE, name = "longitude")
            horizontalAccuracy?.requireNotLess(
                other = horizontalAccuracy.zero(),
                name = "horizontalAccuracy"
            )
            verticalAccuracy?.requireNotLess(
                other = verticalAccuracy.zero(),
                name = "verticalAccuracy"
            )
        }
    }
}
