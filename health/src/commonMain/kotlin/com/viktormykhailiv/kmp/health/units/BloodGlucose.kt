package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Represents a unit of blood glucose level (glycaemia). Supported units:
 * - mmol/L - see [BloodGlucose.millimolesPerLiter]
 * - mg/dL - see [BloodGlucose.milligramsPerDeciliter]
 */
data class BloodGlucose private constructor(
    private val value: Double,
    private val type: Type,
) : Comparable<BloodGlucose> {

    /** Returns the blood glucose level in mmol/L. */
    @get:JvmName("getMillimolesPerLiter")
    val inMillimolesPerLiter: Double
        get() = value * type.millimolesPerLiterPerUnit

    /** Returns the blood glucose level concentration in mg/dL. */
    @get:JvmName("getMilligramsPerDeciliter")
    val inMilligramsPerDeciliter: Double
        get() = get(type = Type.MILLIGRAMS_PER_DECILITER)

    private fun get(type: Type): Double =
        if (this.type == type) value else inMillimolesPerLiter / type.millimolesPerLiterPerUnit

    /** Returns zero [BloodGlucose] of the same [Type]. */
    internal fun zero(): BloodGlucose = ZEROS.getValue(type)

    override fun compareTo(other: BloodGlucose): Int =
        if (type == other.type) {
            value.compareTo(other.value)
        } else {
            inMillimolesPerLiter.compareTo(other.inMillimolesPerLiter)
        }

    override fun toString(): String = "$value ${type.title}"

    companion object {
        private val ZEROS = Type.entries.associateWith { BloodGlucose(value = 0.0, type = it) }

        /** Creates [BloodGlucose] with the specified value in mmol/L. */
        @JvmStatic
        fun millimolesPerLiter(value: Double): BloodGlucose =
            BloodGlucose(value, Type.MILLIMOLES_PER_LITER)

        /** Creates [BloodGlucose] with the specified value in mg/dL. */
        @JvmStatic
        fun milligramsPerDeciliter(value: Double): BloodGlucose =
            BloodGlucose(value, Type.MILLIGRAMS_PER_DECILITER)
    }

    private enum class Type {
        MILLIMOLES_PER_LITER {
            override val millimolesPerLiterPerUnit: Double = 1.0
            override val title: String
                get() = "mmol/L"
        },
        MILLIGRAMS_PER_DECILITER {
            override val millimolesPerLiterPerUnit: Double = 1 / 18.0
            override val title: String = "mg/dL"
        };

        abstract val millimolesPerLiterPerUnit: Double
        abstract val title: String
    }
}
