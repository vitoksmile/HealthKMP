package com.viktormykhailiv.kmp.health.records

/**
 * Type of exercise (e.g. walking, swimming).
 */
sealed interface ExerciseType {
    data object OtherWorkout : ExerciseType
    data object Badminton : ExerciseType
    data object Baseball : ExerciseType
    data object Basketball : ExerciseType
    data object Biking : ExerciseType
    data object BikingStationary : ExerciseType
    data object BootCamp : ExerciseType
    data object Boxing : ExerciseType
    data object Calisthenics : ExerciseType
    data object Cricket : ExerciseType
    data object Dancing : ExerciseType
    data object Elliptical : ExerciseType
    data object ExerciseClass : ExerciseType
    data object Fencing : ExerciseType
    data object FootballAmerican : ExerciseType
    data object FootballAustralian : ExerciseType
    data object FrisbeeDisc : ExerciseType
    data object Golf : ExerciseType
    data object GuidedBreathing : ExerciseType
    data object Gymnastics : ExerciseType
    data object Handball : ExerciseType
    data object HighIntensityIntervalTraining : ExerciseType
    data object Hiking : ExerciseType
    data object IceHockey : ExerciseType
    data object IceSkating : ExerciseType
    data object MartialArts : ExerciseType
    data object Paddling : ExerciseType
    data object Paragliding : ExerciseType
    data object Pilates : ExerciseType
    data object Racquetball : ExerciseType
    data object RockClimbing : ExerciseType
    data object RollerHockey : ExerciseType
    data object Rowing : ExerciseType
    data object RowingMachine : ExerciseType
    data object Rugby : ExerciseType
    data object Running : ExerciseType
    data object RunningTreadmill : ExerciseType
    data object Sailing : ExerciseType
    data object ScubaDiving : ExerciseType
    data object Skating : ExerciseType
    data object Skiing : ExerciseType
    data object Snowboarding : ExerciseType
    data object Snowshoeing : ExerciseType
    data object Soccer : ExerciseType
    data object Softball : ExerciseType
    data object Squash : ExerciseType
    data object StairClimbing : ExerciseType
    data object StairClimbingMachine : ExerciseType
    data object StrengthTraining : ExerciseType
    data object Stretching : ExerciseType
    data object Surfing : ExerciseType
    data object SwimmingOpenWater : ExerciseType
    data object SwimmingPool : ExerciseType
    data object TableTennis : ExerciseType
    data object Tennis : ExerciseType
    data object Volleyball : ExerciseType
    data object Walking : ExerciseType
    data object WaterPolo : ExerciseType
    data object Weightlifting : ExerciseType
    data object Wheelchair : ExerciseType
    data object Yoga : ExerciseType
}