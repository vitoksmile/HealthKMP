package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * Represents a unit of temperature. Supported units:
 * - Celsius - see [Temperature.celsius], [Double.celsius]
 * - Fahrenheit - see [Temperature.fahrenheit], [Double.fahrenheit]
 */
data class Temperature private constructor(
    private val value: Double,
    private val type: Type,
) : Comparable<Temperature> {

    /** Returns the temperature in Celsius degrees. */
    @get:JvmName("getCelsius")
    val inCelsius: Double
        get() =
            when (type) {
                Type.CELSIUS -> value
                Type.FAHRENHEIT -> (value - 32.0) / 1.8
            }

    /** Returns the temperature in Fahrenheit degrees. */
    @get:JvmName("getFahrenheit")
    val inFahrenheit: Double
        get() =
            when (type) {
                Type.CELSIUS -> value * 1.8 + 32.0
                Type.FAHRENHEIT -> value
            }

    override fun compareTo(other: Temperature): Int =
        if (type == other.type) {
            value.compareTo(other.value)
        } else {
            inCelsius.compareTo(other.inCelsius)
        }

    override fun toString(): String = "$value ${type.title}"

    companion object {
        /** Creates [Temperature] with the specified value in Celsius degrees. */
        @JvmStatic
        fun celsius(value: Double): Temperature = Temperature(value, Type.CELSIUS)

        /** Creates [Temperature] with the specified value in Fahrenheit degrees. */
        @JvmStatic
        fun fahrenheit(value: Double): Temperature = Temperature(value, Type.FAHRENHEIT)
    }

    private enum class Type {
        CELSIUS {
            override val title: String = "Celsius"
        },
        FAHRENHEIT {
            override val title: String = "Fahrenheit"
        };

        abstract val title: String
    }
}

/** Creates [Temperature] with the specified value in Celsius degrees. */
@get:JvmSynthetic
val Double.celsius: Temperature
    get() = Temperature.celsius(value = this)

/** Creates [Temperature] with the specified value in Celsius degrees. */
@get:JvmSynthetic
val Long.celsius: Temperature
    get() = toDouble().celsius

/** Creates [Temperature] with the specified value in Celsius degrees. */
@get:JvmSynthetic
val Float.celsius: Temperature
    get() = toDouble().celsius

/** Creates [Temperature] with the specified value in Celsius degrees. */
@get:JvmSynthetic
val Int.celsius: Temperature
    get() = toDouble().celsius

/** Creates [Temperature] with the specified value in Fahrenheit degrees. */
@get:JvmSynthetic
val Double.fahrenheit: Temperature
    get() = Temperature.fahrenheit(value = this)

/** Creates [Temperature] with the specified value in Fahrenheit degrees. */
@get:JvmSynthetic
val Long.fahrenheit: Temperature
    get() = toDouble().fahrenheit

/** Creates [Temperature] with the specified value in Fahrenheit degrees. */
@get:JvmSynthetic
val Float.fahrenheit: Temperature
    get() = toDouble().fahrenheit

/** Creates [Temperature] with the specified value in Fahrenheit degrees. */
@get:JvmSynthetic
val Int.fahrenheit: Temperature
    get() = toDouble().fahrenheit
