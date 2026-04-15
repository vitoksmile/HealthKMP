package com.viktormykhailiv.kmp.health.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationFlow
import com.viktormykhailiv.kmp.health.HealthDataType.MenstruationPeriod
import com.viktormykhailiv.kmp.health.HealthDataType.OvulationTest
import com.viktormykhailiv.kmp.health.HealthDataType.Power
import com.viktormykhailiv.kmp.health.HealthDataType.SexualActivity
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.HealthManagerFactory
import com.viktormykhailiv.kmp.health.sample.dataType.BloodGlucoseScreen
import com.viktormykhailiv.kmp.health.sample.dataType.BloodPressureScreen
import com.viktormykhailiv.kmp.health.sample.dataType.BodyFatScreen
import com.viktormykhailiv.kmp.health.sample.dataType.BodyTemperatureScreen
import com.viktormykhailiv.kmp.health.sample.dataType.CyclingPedalingCadenceScreen
import com.viktormykhailiv.kmp.health.sample.dataType.ExerciseScreen
import com.viktormykhailiv.kmp.health.sample.dataType.HeartRateScreen
import com.viktormykhailiv.kmp.health.sample.dataType.HeightScreen
import com.viktormykhailiv.kmp.health.sample.dataType.LeanBodyMassScreen
import com.viktormykhailiv.kmp.health.sample.dataType.MenstruationFlowScreen
import com.viktormykhailiv.kmp.health.sample.dataType.MenstruationPeriodScreen
import com.viktormykhailiv.kmp.health.sample.dataType.OvulationTestScreen
import com.viktormykhailiv.kmp.health.sample.dataType.PowerScreen
import com.viktormykhailiv.kmp.health.sample.dataType.SexualActivityScreen
import com.viktormykhailiv.kmp.health.sample.dataType.SleepScreen
import com.viktormykhailiv.kmp.health.sample.dataType.StepsScreen
import com.viktormykhailiv.kmp.health.sample.dataType.WeightScreen
import com.viktormykhailiv.kmp.health.sample.navigation.LocalNavController
import com.viktormykhailiv.kmp.health.sample.navigation.NavDestinations
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.sample.ui.AppBar
import com.viktormykhailiv.kmp.health.sample.ui.AppButton
import kotlinx.coroutines.launch

@Composable
fun SampleApp() {
    val coroutineScope = rememberCoroutineScope()
    val health = remember { HealthManagerFactory().createManager() }

    val readTypes = remember {
        listOf(
            BloodGlucose,
            BloodPressure,
            BodyFat,
            BodyTemperature,
            CyclingPedalingCadence,
            Exercise(),
            HeartRate,
            Height,
            LeanBodyMass,
            MenstruationFlow,
            MenstruationPeriod,
            OvulationTest,
            Power,
            SexualActivity,
            Sleep,
            Steps,
            Weight,
        )
    }
    val writeTypes = remember {
        listOf(
            BloodGlucose,
            BloodPressure,
            BodyFat,
            BodyTemperature,
            CyclingPedalingCadence,
            Exercise(),
            HeartRate,
            Height,
            LeanBodyMass,
            MenstruationFlow,
            MenstruationPeriod,
            OvulationTest,
            Power,
            SexualActivity,
            Sleep,
            Steps,
            Weight,
        )
    }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }
    var regionalPreferencesResult by remember { mutableStateOf<Result<RegionalPreferences>?>(null) }

    LaunchedEffect(health) {
        isAvailableResult = health.isAvailable()

        if (isAvailableResult.getOrNull() == false) return@LaunchedEffect
        isAuthorizedResult = health.isAuthorized(
            readTypes = readTypes,
            writeTypes = writeTypes,
        )
        isRevokeSupported = health.isRevokeAuthorizationSupported().getOrNull() == true
        regionalPreferencesResult = health.getRegionalPreferences()
    }

    val navController = rememberNavController()
    MaterialTheme {
        CompositionLocalProvider(
            LocalHealthManager provides health,
            LocalNavController provides navController,
        ) {
            NavHost(
                navController,
                startDestination = NavDestinations.Root,
            ) {
                composable<NavDestinations.Root> {
                    Scaffold(
                        topBar = {
                            AppBar(
                                title = "HealthKMP",
                            )
                        },
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                                .navigationBarsPadding()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text("Hello, this is HealthKMP for ${getPlatformName()}")

                            isAvailableResult
                                .onFailure {
                                    Text("HealthManager isAvailable=$it")
                                }

                            isAuthorizedResult
                                ?.onFailure {
                                    Text("HealthManager isAuthorized=$it")
                                }
                            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() != true)
                                AppButton(
                                    text = "Request permissions",
                                    onClick = {
                                        coroutineScope.launch {
                                            isAuthorizedResult = health.requestAuthorization(
                                                readTypes = readTypes,
                                                writeTypes = writeTypes,
                                            )
                                        }
                                    },
                                )

                            if (isAvailableResult.getOrNull() == true && isRevokeSupported && isAuthorizedResult?.getOrNull() == true)
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            health.revokeAuthorization()
                                            isAuthorizedResult = health.isAuthorized(
                                                readTypes = readTypes,
                                                writeTypes = writeTypes,
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red,
                                        contentColor = Color.White,
                                    ),
                                ) {
                                    Text("Revoke authorization")
                                }

                            regionalPreferencesResult
                                ?.onSuccess {
                                    Text("Regional temperature preference ${it.temperature}")
                                }
                                ?.onFailure {
                                    Text("Failed to read regional temperature preference $it")
                                }

                            if (isAvailableResult.getOrNull() == true && isAuthorizedResult?.getOrNull() == true) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    mapOf(
                                        "Blood glucose" to NavDestinations.BloodGlucose,
                                        "Blood pressure" to NavDestinations.BloodPressure,
                                        "Body fat" to NavDestinations.BodyFat,
                                        "Body temperature" to NavDestinations.BodyTemperature,
                                        "Cycling pedaling cadence" to NavDestinations.CyclingPedalingCadence,
                                        "Exercise" to NavDestinations.Exercise,
                                        "Heart rate" to NavDestinations.HeartRate,
                                        "Height" to NavDestinations.Height,
                                        "Lean body mass" to NavDestinations.LeanBodyMass,
                                        "Menstruation flow" to NavDestinations.MenstruationFlow,
                                        "Menstruation period" to NavDestinations.MenstruationPeriod,
                                        "Ovulation test" to NavDestinations.OvulationTest,
                                        "Power" to NavDestinations.Power,
                                        "Sexual activity" to NavDestinations.SexualActivity,
                                        "Sleep" to NavDestinations.Sleep,
                                        "Steps" to NavDestinations.Steps,
                                        "Weight" to NavDestinations.Weight,
                                    ).forEach { (title, destination) ->
                                        AppButton(
                                            text = title,
                                            onClick = { navController.navigate(destination) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                composable<NavDestinations.BloodGlucose> { BloodGlucoseScreen() }
                composable<NavDestinations.BloodPressure> { BloodPressureScreen() }
                composable<NavDestinations.BodyFat> { BodyFatScreen() }
                composable<NavDestinations.BodyTemperature> { BodyTemperatureScreen() }
                composable<NavDestinations.CyclingPedalingCadence> { CyclingPedalingCadenceScreen() }
                composable<NavDestinations.Exercise> { ExerciseScreen() }
                composable<NavDestinations.HeartRate> { HeartRateScreen() }
                composable<NavDestinations.Height> { HeightScreen() }
                composable<NavDestinations.LeanBodyMass> { LeanBodyMassScreen() }
                composable<NavDestinations.MenstruationFlow> { MenstruationFlowScreen() }
                composable<NavDestinations.MenstruationPeriod> { MenstruationPeriodScreen() }
                composable<NavDestinations.OvulationTest> { OvulationTestScreen() }
                composable<NavDestinations.Power> { PowerScreen() }
                composable<NavDestinations.SexualActivity> { SexualActivityScreen() }
                composable<NavDestinations.Sleep> { SleepScreen() }
                composable<NavDestinations.Steps> { StepsScreen() }
                composable<NavDestinations.Weight> { WeightScreen() }
            }
        }
    }
}

expect fun getPlatformName(): String
