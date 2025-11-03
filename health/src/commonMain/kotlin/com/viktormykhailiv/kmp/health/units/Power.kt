package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Represents a unit of power. Supported units:
 * - watts - see [Power.watts], [Double.watts]
 * - kilocalories/day - see [Power.kilocaloriesPerDay], [Double.kilocaloriesPerDay]
 */
data class Power private constructor(
    private val value: Double,
    private val type: Type,
) : Comparable<Power> {

    /** Returns the power in Watts. */
    @get:JvmName("getWatts")
    val inWatts: Double
        get() = value * type.wattsPerUnit

    /** Returns the power in kilocalories/day. */
    @get:JvmName("getKilocaloriesPerDay")
    val inKilocaloriesPerDay: Double
        get() = get(type = Type.KILOCALORIES_PER_DAY)

    private fun get(type: Type): Double =
        if (this.type == type) value else inWatts / type.wattsPerUnit

    /** Returns zero [Power] of the same [Type]. */
    internal fun zero(): Power = ZEROS.getValue(type)

    override fun compareTo(other: Power): Int =
        if (type == other.type) {
            value.compareTo(other.value)
        } else {
            inWatts.compareTo(other.inWatts)
        }

    override fun toString(): String = "$value ${type.title}"

    companion object {
        private val ZEROS = Type.entries.associateWith { Power(value = 0.0, type = it) }

        /** Creates [Power] with the specified value in Watts. */
        @JvmStatic
        fun watts(value: Double): Power = Power(value, Type.WATTS)

        /** Creates [Power] with the specified value in kilocalories/day. */
        @JvmStatic
        fun kilocaloriesPerDay(value: Double): Power = Power(value, Type.KILOCALORIES_PER_DAY)
    }

    private enum class Type {
        WATTS {
            override val wattsPerUnit: Double = 1.0
            override val title: String = "Watts"
        },
        KILOCALORIES_PER_DAY {
            override val wattsPerUnit: Double = 0.0484259259
            override val title: String = "kcal/day"
        };

        abstract val wattsPerUnit: Double
        abstract val title: String
    }
}

/** Creates [Power] with the specified value in Watts. */
@get:JvmSynthetic
val Double.watts: Power
    get() = Power.watts(value = this)

/** Creates [Power] with the specified value in Watts. */
@get:JvmSynthetic
val Long.watts: Power
    get() = toDouble().watts

/** Creates [Power] with the specified value in Watts. */
@get:JvmSynthetic
val Float.watts: Power
    get() = toDouble().watts

/** Creates [Power] with the specified value in Watts. */
@get:JvmSynthetic
val Int.watts: Power
    get() = toDouble().watts

/** Creates [Power] with the specified value in kilocalories/day. */
@get:JvmSynthetic
val Double.kilocaloriesPerDay: Power
    get() = Power.kilocaloriesPerDay(value = this)

/** Creates [Power] with the specified value in kilocalories/day. */
@get:JvmSynthetic
val Long.kilocaloriesPerDay: Power
    get() = toDouble().kilocaloriesPerDay

/** Creates [Power] with the specified value in kilocalories/day. */
@get:JvmSynthetic
val Float.kilocaloriesPerDay: Power
    get() = toDouble().kilocaloriesPerDay

/** Creates [Power] with the specified value in kilocalories/day. */
@get:JvmSynthetic
val Int.kilocaloriesPerDay: Power
    get() = toDouble().kilocaloriesPerDay
