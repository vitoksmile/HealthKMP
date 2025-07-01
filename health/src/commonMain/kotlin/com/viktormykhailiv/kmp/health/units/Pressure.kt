package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Represents a unit of pressure. Supported units:
 * - millimeters of Mercury (mmHg) - see [Pressure.millimetersOfMercury],
 *   [Double.millimetersOfMercury].
 */
data class Pressure private constructor(
    private val value: Double,
) : Comparable<Pressure> {

    /** Returns the pressure in millimeters of Mercury (mmHg). */
    @get:JvmName("getMillimetersOfMercury")
    val inMillimetersOfMercury: Double
        get() = value

    /** Returns zero [Pressure] of the same type (currently there is only one type - mmHg). */
    internal fun zero(): Pressure = ZERO

    override fun compareTo(other: Pressure): Int = value.compareTo(other.value)

    override fun toString(): String = "$value mmHg"

    companion object {
        private val ZERO = Pressure(value = 0.0)

        /** Creates [Pressure] with the specified value in millimeters of Mercury (mmHg). */
        @JvmStatic
        fun millimetersOfMercury(value: Double): Pressure = Pressure(value)
    }
}

/** Creates [Pressure] with the specified value in millimeters of Mercury (mmHg). */
@get:JvmSynthetic
val Double.millimetersOfMercury: Pressure
    get() = Pressure.millimetersOfMercury(value = this)

/** Creates [Pressure] with the specified value in millimeters of Mercury (mmHg). */
@get:JvmSynthetic
val Long.millimetersOfMercury: Pressure
    get() = toDouble().millimetersOfMercury

/** Creates [Pressure] with the specified value in millimeters of Mercury (mmHg). */
@get:JvmSynthetic
val Float.millimetersOfMercury: Pressure
    get() = toDouble().millimetersOfMercury

/** Creates [Pressure] with the specified value in millimeters of Mercury (mmHg). */
@get:JvmSynthetic
val Int.millimetersOfMercury: Pressure
    get() = toDouble().millimetersOfMercury
