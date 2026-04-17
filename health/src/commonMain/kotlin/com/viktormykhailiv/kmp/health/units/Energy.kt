package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Represents a unit of energy. Supported units:
 * - calories - see [Energy.calories], [Double.calories]
 * - kilocalories - see [Energy.kilocalories], [Double.kilocalories]
 * - joules - see [Energy.joules], [Double.joules]
 * - kilojoules - see [Energy.kilojoules], [Double.kilojoules]
 */
data class Energy private constructor(
    private val value: Double,
    private val type: Type,
) : Comparable<Energy> {

    /** Returns the energy in calories. */
    @get:JvmName("getCalories")
    val inCalories: Double
        get() = value * type.caloriesPerUnit

    /** Returns the energy in kilocalories. */
    @get:JvmName("getKilocalories")
    val inKilocalories: Double
        get() = get(type = Type.KILOCALORIES)

    /** Returns the energy in joules. */
    @get:JvmName("getJoules")
    val inJoules: Double
        get() = get(type = Type.JOULES)

    /** Returns the energy in kilojoules. */
    @get:JvmName("getKilojoules")
    val inKilojoules: Double
        get() = get(type = Type.KILOJOULES)

    private fun get(type: Type): Double =
        if (this.type == type) value else inCalories / type.caloriesPerUnit

    /** Returns zero [Energy] of the same [Type]. */
    internal fun zero(): Energy = ZEROS.getValue(type)

    override fun compareTo(other: Energy): Int =
        if (type == other.type) {
            value.compareTo(other.value)
        } else {
            inCalories.compareTo(other.inCalories)
        }

    override fun toString(): String = "$value ${type.name.lowercase()}"

    companion object {
        private val ZEROS = Type.entries.associateWith { Energy(value = 0.0, type = it) }

        /** Creates [Energy] with the specified value in calories. */
        @JvmStatic
        fun calories(value: Double): Energy = Energy(value, Type.CALORIES)

        /** Creates [Energy] with the specified value in kilocalories. */
        @JvmStatic
        fun kilocalories(value: Double): Energy = Energy(value, Type.KILOCALORIES)

        /** Creates [Energy] with the specified value in joules. */
        @JvmStatic
        fun joules(value: Double): Energy = Energy(value, Type.JOULES)

        /** Creates [Energy] with the specified value in kilojoules. */
        @JvmStatic
        fun kilojoules(value: Double): Energy = Energy(value, Type.KILOJOULES)
    }

    private enum class Type {
        CALORIES {
            override val caloriesPerUnit: Double = 1.0
        },
        KILOCALORIES {
            override val caloriesPerUnit: Double = 1000.0
        },
        JOULES {
            override val caloriesPerUnit: Double = 0.2390057361
        },
        KILOJOULES {
            override val caloriesPerUnit: Double = 239.0057361
        };

        abstract val caloriesPerUnit: Double
    }
}

/** Creates [Energy] with the specified value in calories. */
@get:JvmSynthetic
val Double.calories: Energy
    get() = Energy.calories(value = this)

/** Creates [Energy] with the specified value in calories. */
@get:JvmSynthetic
val Float.calories: Energy
    get() = toDouble().calories

/** Creates [Energy] with the specified value in calories. */
@get:JvmSynthetic
val Long.calories: Energy
    get() = toDouble().calories

/** Creates [Energy] with the specified value in calories. */
@get:JvmSynthetic
val Int.calories: Energy
    get() = toDouble().calories

/** Creates [Energy] with the specified value in kilocalories. */
@get:JvmSynthetic
val Double.kilocalories: Energy
    get() = Energy.kilocalories(value = this)

/** Creates [Energy] with the specified value in kilocalories. */
@get:JvmSynthetic
val Float.kilocalories: Energy
    get() = toDouble().kilocalories

/** Creates [Energy] with the specified value in kilocalories. */
@get:JvmSynthetic
val Long.kilocalories: Energy
    get() = toDouble().kilocalories

/** Creates [Energy] with the specified value in kilocalories. */
@get:JvmSynthetic
val Int.kilocalories: Energy
    get() = toDouble().kilocalories

/** Creates [Energy] with the specified value in joules. */
@get:JvmSynthetic
val Double.joules: Energy
    get() = Energy.joules(value = this)

/** Creates [Energy] with the specified value in joules. */
@get:JvmSynthetic
val Float.joules: Energy
    get() = toDouble().joules

/** Creates [Energy] with the specified value in joules. */
@get:JvmSynthetic
val Long.joules: Energy
    get() = toDouble().joules

/** Creates [Energy] with the specified value in joules. */
@get:JvmSynthetic
val Int.joules: Energy
    get() = toDouble().joules

/** Creates [Energy] with the specified value in kilojoules. */
@get:JvmSynthetic
val Double.kilojoules: Energy
    get() = Energy.kilojoules(value = this)

/** Creates [Energy] with the specified value in kilojoules. */
@get:JvmSynthetic
val Float.kilojoules: Energy
    get() = toDouble().kilojoules

/** Creates [Energy] with the specified value in kilojoules. */
@get:JvmSynthetic
val Long.kilojoules: Energy
    get() = toDouble().kilojoules

/** Creates [Energy] with the specified value in kilojoules. */
@get:JvmSynthetic
val Int.kilojoules: Energy
    get() = toDouble().kilojoules
