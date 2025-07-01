package com.viktormykhailiv.kmp.health.records

/**
 * Type of meal.
 */
enum class MealType {
    /**
     * Use this for the first meal of the day, usually the morning meal.
     */
    Breakfast,

    /**
     * Use this for the noon meal.
     */
    Lunch,

    /**
     * Use this for last meal of the day, usually the evening meal.
     */
    Dinner,

    /**
     * Any meal outside of the usual three meals per day.
     */
    Snack,
}
