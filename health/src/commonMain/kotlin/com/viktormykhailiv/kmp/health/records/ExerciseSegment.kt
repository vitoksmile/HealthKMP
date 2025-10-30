package com.viktormykhailiv.kmp.health.records

import kotlin.time.Instant

/**
 * Represents particular exercise within an exercise session.
 *
 * Each segment contains start and end time of the exercise, exercise type and optional number of
 * repetitions.
 *
 * @param segmentType Type of segment (e.g. biking, plank).
 * @param repetitions Number of repetitions in the segment. Must be non-negative.
 *
 * @see ExerciseSessionRecord
 */
data class ExerciseSegment(
    val startTime: Instant,
    val endTime: Instant,
    val segmentType: Type,
    val repetitions: Int = 0,
) {

    init {
        require(startTime < endTime) { "startTime must be before endTime." }
        require(repetitions >= 0) { "repetitions can not be negative." }
    }

    sealed interface Type {
        data object Unknown : Type
        data object ArmCurl : Type
        data object BackExtension : Type
        data object BallSlam : Type
        data object BarbellShoulderPress : Type
        data object BenchPress : Type
        data object BenchSitUp : Type
        data object Biking : Type
        data object BikingStationary : Type
        data object Burpee : Type
        data object Crunch : Type
        data object Deadlift : Type
        data object DoubleArmTricepsExtension : Type
        data object DumbbellCurlLeftArm : Type
        data object DumbbellCurlRightArm : Type
        data object DumbbellFrontRaise : Type
        data object DumbbellLateralRaise : Type
        data object DumbbellRow : Type
        data object DumbbellTricepsExtensionLeftArm : Type
        data object DumbbellTricepsExtensionRightArm : Type
        data object DumbbellTricepsExtensionTwoArm : Type
        data object Elliptical : Type
        data object ForwardTwist : Type
        data object FrontRaise : Type
        data object HighIntensityIntervalTraining : Type
        data object HipThrust : Type
        data object HulaHoop : Type
        data object JumpingJack : Type
        data object JumpRope : Type
        data object KettlebellSwing : Type
        data object LateralRaise : Type
        data object LatPullDown : Type
        data object LegCurl : Type
        data object LegExtension : Type
        data object LegPress : Type
        data object LegRaise : Type
        data object Lunge : Type
        data object MountainClimber : Type
        data object OtherWorkout : Type
        data object Pause : Type
        data object Pilates : Type
        data object Plank : Type
        data object PullUp : Type
        data object Punch : Type
        data object Rest : Type
        data object RowingMachine : Type
        data object Running : Type
        data object RunningTreadmill : Type
        data object ShoulderPress : Type
        data object SingleArmTricepsExtension : Type
        data object SitUp : Type
        data object Squat : Type
        data object StairClimbing : Type
        data object StairClimbingMachine : Type
        data object Stretching : Type
        data object SwimmingBackstroke : Type
        data object SwimmingBreaststroke : Type
        data object SwimmingButterfly : Type
        data object SwimmingFreestyle : Type
        data object SwimmingMixed : Type
        data object SwimmingOpenWater : Type
        data object SwimmingOther : Type
        data object SwimmingPool : Type
        data object UpperTwist : Type
        data object Walking : Type
        data object Weightlifting : Type
        data object Wheelchair : Type
        data object Yoga : Type
    }
}
