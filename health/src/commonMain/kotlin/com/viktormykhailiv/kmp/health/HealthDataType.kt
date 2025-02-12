package com.viktormykhailiv.kmp.health

sealed interface HealthDataType {
    data object Steps : HealthDataType

    data object Weight : HealthDataType
}