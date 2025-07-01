package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Represents a unit of length. Supported units:
 * - meters - see [Length.meters], [Double.meters]
 * - kilometers - see [Length.kilometers], [Double.kilometers]
 * - miles - see [Length.miles], [Double.miles]
 * - inches - see [Length.inches], [Double.inches]
 * - feet - see [Length.feet], [Double.feet]
 * */
data class Length private constructor(
    private val value: Double,
    private val type: Type,
) : Comparable<Length> {

    /** Returns the length in meters. */
    @get:JvmName("getMeters")
    val inMeters: Double
        get() = value * type.metersPerUnit

    /** Returns the length in kilometers. */
    @get:JvmName("getKilometers")
    val inKilometers: Double
        get() = get(type = Type.KILOMETERS)

    /** Returns the length in miles. */
    @get:JvmName("getMiles")
    val inMiles: Double
        get() = get(type = Type.MILES)

    /** Returns the length in inches. */
    @get:JvmName("getInches")
    val inInches: Double
        get() = get(type = Type.INCHES)

    /** Returns the length in feet. */
    @get:JvmName("getFeet")
    val inFeet: Double
        get() = get(type = Type.FEET)

    private fun get(type: Type): Double =
        if (this.type == type) value else inMeters / type.metersPerUnit

    /** Returns zero [Length] of the same [Type]. */
    internal fun zero(): Length = ZEROS.getValue(type)

    override fun compareTo(other: Length): Int =
        if (type == other.type) {
            value.compareTo(other.value)
        } else {
            inMeters.compareTo(other.inMeters)
        }

    companion object {
        private val ZEROS = Type.entries.associateWith { Length(value = 0.0, type = it) }

        /** Creates [Length] with the specified value in meters. */
        @JvmStatic
        fun meters(value: Double): Length = Length(value, Type.METERS)

        /** Creates [Length] with the specified value in kilometers. */
        @JvmStatic
        fun kilometers(value: Double): Length = Length(value, Type.KILOMETERS)

        /** Creates [Length] with the specified value in miles. */
        @JvmStatic
        fun miles(value: Double): Length = Length(value, Type.MILES)

        /** Creates [Length] with the specified value in inches. */
        @JvmStatic
        fun inches(value: Double): Length = Length(value, Type.INCHES)

        /** Creates [Length] with the specified value in feet. */
        @JvmStatic
        fun feet(value: Double): Length = Length(value, Type.FEET)
    }

    private enum class Type {
        METERS {
            override val metersPerUnit: Double = 1.0
        },
        KILOMETERS {
            override val metersPerUnit: Double = 1000.0
        },
        MILES {
            override val metersPerUnit: Double = 1609.34
        },
        INCHES {
            override val metersPerUnit: Double = 0.0254
        },
        FEET {
            override val metersPerUnit: Double = 0.3048
        };

        abstract val metersPerUnit: Double
    }
}

/** Creates [Length] with the specified value in meters. */
@get:JvmSynthetic
val Double.meters: Length
    get() = Length.meters(value = this)

/** Creates [Length] with the specified value in meters. */
@get:JvmSynthetic
val Long.meters: Length
    get() = toDouble().meters

/** Creates [Length] with the specified value in meters. */
@get:JvmSynthetic
val Float.meters: Length
    get() = toDouble().meters

/** Creates [Length] with the specified value in meters. */
@get:JvmSynthetic
val Int.meters: Length
    get() = toDouble().meters

/** Creates [Length] with the specified value in kilometers. */
@get:JvmSynthetic
val Double.kilometers: Length
    get() = Length.kilometers(value = this)

/** Creates [Length] with the specified value in kilometers. */
@get:JvmSynthetic
val Float.kilometers: Length
    get() = toDouble().kilometers

/** Creates [Length] with the specified value in kilometers. */
@get:JvmSynthetic
val Long.kilometers: Length
    get() = toDouble().kilometers

/** Creates [Length] with the specified value in kilometers. */
@get:JvmSynthetic
val Int.kilometers: Length
    get() = toDouble().kilometers

/** Creates [Length] with the specified value in miles. */
@get:JvmSynthetic
val Double.miles: Length
    get() = Length.miles(value = this)

/** Creates [Length] with the specified value in miles. */
@get:JvmSynthetic
val Float.miles: Length
    get() = toDouble().miles

/** Creates [Length] with the specified value in miles. */
@get:JvmSynthetic
val Long.miles: Length
    get() = toDouble().miles

/** Creates [Length] with the specified value in miles. */
@get:JvmSynthetic
val Int.miles: Length
    get() = toDouble().miles

/** Creates [Length] with the specified value in inches. */
@get:JvmSynthetic
val Double.inches: Length
    get() = Length.inches(value = this)

/** Creates [Length] with the specified value in inches. */
@get:JvmSynthetic
val Float.inches: Length
    get() = toDouble().inches

/** Creates [Length] with the specified value in inches. */
@get:JvmSynthetic
val Long.inches: Length
    get() = toDouble().inches

/** Creates [Length] with the specified value in inches. */
@get:JvmSynthetic
val Int.inches: Length
    get() = toDouble().inches

/** Creates [Length] with the specified value in feet. */
@get:JvmSynthetic
val Double.feet: Length
    get() = Length.feet(value = this)

/** Creates [Length] with the specified value in feet. */
@get:JvmSynthetic
val Float.feet: Length
    get() = toDouble().feet

/** Creates [Length] with the specified value in feet. */
@get:JvmSynthetic
val Long.feet: Length
    get() = toDouble().feet

/** Creates [Length] with the specified value in feet. */
@get:JvmSynthetic
val Int.feet: Length
    get() = toDouble().feet
