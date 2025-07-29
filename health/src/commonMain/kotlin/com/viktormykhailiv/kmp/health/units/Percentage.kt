package com.viktormykhailiv.kmp.health.units

import kotlin.jvm.JvmSynthetic

/**
 * Represents a value as a percentage, not a fraction - for example 100%, 89.62%, etc.
 */
data class Percentage(val value: Double) : Comparable<Percentage> {

    override fun compareTo(other: Percentage): Int = value.compareTo(other.value)

    override fun toString(): String = "$value%"
}

/** Creates [Percentage] with the specified percentage value, not a fraction. */
@get:JvmSynthetic
val Double.percent: Percentage
    get() = Percentage(value = this)

/** Creates [Percentage] with the specified percentage value, not a fraction. */
@get:JvmSynthetic
val Long.percent: Percentage
    get() = toDouble().percent

/** Creates [Percentage] with the specified percentage value, not a fraction. */
@get:JvmSynthetic
val Float.percent: Percentage
    get() = toDouble().percent

/** Creates [Percentage] with the specified percentage value, not a fraction. */
@get:JvmSynthetic
val Int.percent: Percentage
    get() = toDouble().percent
