package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.InstantaneousRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import kotlin.time.Instant

/**
 * Captures the concentration of glucose in the blood. Each record represents a single instantaneous
 * blood glucose reading.
 *
 * @param level Blood glucose level or concentration. Required field. Valid range: 0-50 mmol/L.
 * @param specimenSource Type of body fluid used to measure the blood glucose.
 * @param mealType Type of meal related to the blood glucose measurement.
 * @param relationToMeal Relationship of the meal to the blood glucose measurement.
 */
data class BloodGlucoseRecord(
    override val time: Instant,
    val level: BloodGlucoseUnit,
    val specimenSource: SpecimenSource?,
    val mealType: MealType?,
    val relationToMeal: RelationToMeal?,
    override val metadata: Metadata,
) : InstantaneousRecord {

    override val dataType: HealthDataType = BloodGlucose

    init {
        level.requireNotLess(other = level.zero(), name = "level")
        level.requireNotMore(other = MAX_BLOOD_GLUCOSE_LEVEL, name = "level")
    }

    /**
     * List of supported blood glucose specimen sources (type of body fluid used to measure the
     * blood glucose).
     */
    enum class SpecimenSource {
        InterstitialFluid,
        CapillaryBlood,
        Plasma,
        Serum,
        Tears,
        WholeBlood,
    }

    /**
     * Temporal relationship of measurement time to a meal.
     */
    enum class RelationToMeal {
        General,
        Fasting,
        BeforeMeal,
        AfterMeal,
    }

    private companion object {
        val MAX_BLOOD_GLUCOSE_LEVEL = BloodGlucoseUnit.millimolesPerLiter(50.0)
    }

}
