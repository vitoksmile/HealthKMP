package com.viktormykhailiv.kmp.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.HealthDataType.BloodGlucose
import com.viktormykhailiv.kmp.health.HealthDataType.BloodPressure
import com.viktormykhailiv.kmp.health.HealthDataType.BodyFat
import com.viktormykhailiv.kmp.health.HealthDataType.BodyTemperature
import com.viktormykhailiv.kmp.health.HealthDataType.Exercise
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.LeanBodyMass
import com.viktormykhailiv.kmp.health.HealthDataType.CyclingPedalingCadence
import com.viktormykhailiv.kmp.health.HealthDataType.Power
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.BloodGlucoseAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BloodPressureAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BodyFatAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BodyTemperatureAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.LeanBodyMassAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.CyclingPedalingCadenceAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.PowerAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.exercise.ExerciseTypePicker
import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseLap
import com.viktormykhailiv.kmp.health.records.ExerciseRoute
import com.viktormykhailiv.kmp.health.records.ExerciseSegment
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.MealType
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import com.viktormykhailiv.kmp.health.region.preferred
import com.viktormykhailiv.kmp.health.sleep.SleepSessionCanvas
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.Temperature
import com.viktormykhailiv.kmp.health.units.meters
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import com.viktormykhailiv.kmp.health.units.percent
import com.viktormykhailiv.kmp.health.units.watts
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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
            Power,
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
            Power,
            Sleep,
            Steps,
            Weight,
        )
    }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }
    var regionalPreferencesResult by remember { mutableStateOf<Result<RegionalPreferences>?>(null) }

    val records = remember {
        mutableStateMapOf<HealthDataType, Result<List<HealthRecord>>>()
    }
    val aggregatedRecords = remember {
        mutableStateMapOf<HealthDataType, Result<HealthAggregatedRecord>>()
    }

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

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAuthorizedResult = health.requestAuthorization(
                                readTypes = readTypes,
                                writeTypes = writeTypes,
                            )
                        }
                    },
                ) {
                    Text("Request permissions")
                }

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
                Column {
                    readTypes.forEach { type ->
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    records[type] = health.readData(
                                        startTime = Clock.System.now()
                                            .minus(7.days),
                                        endTime = Clock.System.now(),
                                        type = type,
                                    )
                                }
                            },
                        ) {
                            Text("Read ${type::class.simpleName}")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    aggregatedRecords[type] = health.aggregate(
                                        startTime = Clock.System.now()
                                            .minus(7.days),
                                        endTime = Clock.System.now(),
                                        type = type,
                                    )
                                }
                            },
                        ) {
                            Text("Aggregate ${type::class.simpleName}")
                        }

                        records[type]
                            ?.onSuccess { records ->
                                Text(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    text = "${type::class.simpleName} records count ${records.size}",
                                )

                                when (type) {
                                    is BloodGlucose -> {
                                        val glucose = records.filterIsInstance<BloodGlucoseRecord>()
                                        val average =
                                            glucose.map { it.level.inMillimolesPerLiter }.average()
                                                .let { BloodGlucoseUnit.millimolesPerLiter(it) }
                                        val min =
                                            glucose.minOfOrNull { it.level.inMillimolesPerLiter }
                                                ?.let { BloodGlucoseUnit.millimolesPerLiter(it) }
                                        val max =
                                            glucose.maxOfOrNull { it.level.inMillimolesPerLiter }
                                                ?.let { BloodGlucoseUnit.millimolesPerLiter(it) }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    is BloodPressure -> {
                                        val bloodPressure =
                                            records.filterIsInstance<BloodPressureRecord>()
                                        val systolicAvg =
                                            bloodPressure.map { it.systolic.inMillimetersOfMercury }
                                                .average()
                                        val systolicMin =
                                            bloodPressure.minOfOrNull { it.systolic.inMillimetersOfMercury }
                                        val systolicMax =
                                            bloodPressure.maxOfOrNull { it.systolic.inMillimetersOfMercury }
                                        val diastolicAvg =
                                            bloodPressure.map { it.diastolic.inMillimetersOfMercury }
                                                .average()
                                        val diastolicMin =
                                            bloodPressure.minOfOrNull { it.diastolic.inMillimetersOfMercury }
                                        val diastolicMax =
                                            bloodPressure.maxOfOrNull { it.diastolic.inMillimetersOfMercury }
                                        Text("Average $systolicAvg/$diastolicAvg")
                                        Text("Min $systolicMin/$diastolicMin")
                                        Text("Max $systolicMax/$diastolicMax")
                                    }

                                    is BodyFat -> {
                                        val fat = records.filterIsInstance<BodyFatRecord>()
                                        val average = fat.map { it.percentage.value }
                                            .average().percent
                                        val min = fat.minOfOrNull { it.percentage.value }?.percent
                                        val max = fat.maxOfOrNull { it.percentage.value }?.percent
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    is BodyTemperature -> {
                                        val unit =
                                            regionalPreferencesResult?.getOrNull()?.temperature
                                                ?: TemperatureRegionalPreference.Celsius

                                        val temperature =
                                            records.filterIsInstance<BodyTemperatureRecord>()
                                        val average =
                                            temperature.map { it.temperature.inCelsius }.average()
                                                .let { Temperature.celsius(it).preferred(unit) }
                                        val min =
                                            temperature.minOfOrNull { it.temperature.inCelsius }
                                                ?.let { Temperature.celsius(it).preferred(unit) }
                                        val max =
                                            temperature.maxOfOrNull { it.temperature.inCelsius }
                                                ?.let { Temperature.celsius(it).preferred(unit) }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    CyclingPedalingCadence -> {
                                        val weight =
                                            records.filterIsInstance<CyclingPedalingCadenceRecord>()
                                                .flatMap { it.samples }
                                        val average =
                                            weight.map { it.revolutionsPerMinute }.average()
                                        val min = weight.minOfOrNull { it.revolutionsPerMinute }
                                        val max = weight.maxOfOrNull { it.revolutionsPerMinute }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    is Exercise -> {
                                        val records =
                                            records.filterIsInstance<ExerciseSessionRecord>()

                                        records.forEach { record ->
                                            Text(
                                                "${record.exerciseType::class.simpleName}, " +
                                                        "duration ${record.endTime - record.startTime}"
                                            )
                                        }
                                    }

                                    is HeartRate -> {
                                        val heartRates = records.filterIsInstance<HeartRateRecord>()
                                            .flatMap { it.samples }
                                        val average = heartRates.map { it.beatsPerMinute }.average()
                                        val min = heartRates.minOfOrNull { it.beatsPerMinute }
                                        val max = heartRates.maxOfOrNull { it.beatsPerMinute }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    is Height -> {
                                        val height = records.filterIsInstance<HeightRecord>()
                                        val average = height.map { it.height.inMeters }.average()
                                            .let { Length.meters(it) }
                                        val min = height.minOfOrNull { it.height.inMeters }
                                            ?.let { Length.meters(it) }
                                        val max = height.maxOfOrNull { it.height.inMeters }
                                            ?.let { Length.meters(it) }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    LeanBodyMass -> {
                                        val mass = records.filterIsInstance<LeanBodyMassRecord>()
                                        val average = mass.map { it.mass.inKilograms }.average()
                                            .let { Mass.kilograms(it) }
                                        val min = mass.minOfOrNull { it.mass.inKilograms }
                                            ?.let { Mass.kilograms(it) }
                                        val max = mass.maxOfOrNull { it.mass.inKilograms }
                                            ?.let { Mass.kilograms(it) }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    Power -> {
                                        val weight = records.filterIsInstance<PowerRecord>()
                                            .flatMap { it.samples }
                                        val average = weight.map { it.power.inWatts }.average()
                                        val min = weight.minOfOrNull { it.power }
                                        val max = weight.maxOfOrNull { it.power }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }

                                    Sleep -> {
                                        Spacer(Modifier.size(16.dp))
                                        records
                                            .filterIsInstance<SleepSessionRecord>()
                                            .forEach { record ->
                                                Text("Sleep session duration ${record.duration}")
                                                SleepSessionCanvas(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    record = record,
                                                )
                                                Spacer(Modifier.size(16.dp))
                                            }
                                    }

                                    Steps -> {
                                        val steps = records.filterIsInstance<StepsRecord>()
                                        val average = steps.map { it.count }.average()
                                        val total = steps.sumOf { it.count }
                                        Text("Average $average")
                                        Text("Total $total")
                                    }

                                    Weight -> {
                                        val weight = records.filterIsInstance<WeightRecord>()
                                        val average = weight.map { it.weight.inKilograms }.average()
                                            .let { Mass.kilograms(it) }
                                        val min = weight.minOfOrNull { it.weight.inKilograms }
                                            ?.let { Mass.kilograms(it) }
                                        val max = weight.maxOfOrNull { it.weight.inKilograms }
                                            ?.let { Mass.kilograms(it) }
                                        Text("Average $average")
                                        Text("Min $min")
                                        Text("Max $max")
                                    }
                                }
                            }
                            ?.onFailure {
                                Text("Failed to read ${type::class.simpleName} records $it")
                            }

                        aggregatedRecords[type]
                            ?.onSuccess { record ->
                                Spacer(Modifier.size(16.dp))
                                Text("Aggregated $type")

                                when (record) {
                                    is BloodGlucoseAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is BloodPressureAggregatedRecord -> {
                                        Text("Average ${record.systolic.avg}/${record.diastolic.avg}")
                                        Text("Min ${record.systolic.min}/${record.diastolic.min}")
                                        Text("Max ${record.systolic.max}/${record.diastolic.max}")
                                    }

                                    is BodyFatAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is BodyTemperatureAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is CyclingPedalingCadenceAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is HeartRateAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is HeightAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is LeanBodyMassAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is PowerAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }

                                    is SleepAggregatedRecord -> {
                                        Text("Total ${record.totalDuration}")
                                    }

                                    is StepsAggregatedRecord -> {
                                        Text("Total ${record.count}")
                                    }

                                    is WeightAggregatedRecord -> {
                                        Text("Average ${record.avg}")
                                        Text("Min ${record.min}")
                                        Text("Max ${record.max}")
                                    }
                                }
                            }
                            ?.onFailure {
                                Spacer(Modifier.size(16.dp))
                                Text("Failed to read ${type::class.simpleName} records $it")
                            }

                        Divider()
                    }

                    Spacer(modifier = Modifier.height(64.dp))
                    var bloodGlucose by remember { mutableStateOf(Random.nextInt(20, 40)) }
                    TextField(
                        value = bloodGlucose.toString(),
                        onValueChange = { bloodGlucose = it.toIntOrNull() ?: 0 },
                        label = { Text("Blood glucose") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeBloodGlucose by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeBloodGlucose = health.writeData(
                                    listOf(
                                        BloodGlucoseRecord(
                                            time = Clock.System.now(),
                                            level = BloodGlucoseUnit.millimolesPerLiter(bloodGlucose.toDouble()),
                                            specimenSource = BloodGlucoseRecord.SpecimenSource.entries.random(),
                                            mealType = MealType.entries.random(),
                                            relationToMeal = BloodGlucoseRecord.RelationToMeal.entries.random(),
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $bloodGlucose blood glucose")
                    }
                    writeBloodGlucose
                        ?.onSuccess {
                            Text("Successfully wrote blood glucose")
                        }
                        ?.onFailure {
                            Text("Failed to write blood glucose $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var systolicBloodPressure by remember {
                        mutableStateOf(Random.nextInt(100, 140))
                    }
                    var diastolicBloodPressure by remember {
                        mutableStateOf(Random.nextInt(70, 90))
                    }
                    TextField(
                        value = systolicBloodPressure.toString(),
                        onValueChange = { systolicBloodPressure = it.toIntOrNull() ?: 0 },
                        label = { Text("Systolic blood pressure") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    TextField(
                        value = diastolicBloodPressure.toString(),
                        onValueChange = { diastolicBloodPressure = it.toIntOrNull() ?: 0 },
                        label = { Text("Diastolic blood pressure") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeBloodPressure by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeBloodPressure = health.writeData(
                                    listOf(
                                        BloodPressureRecord(
                                            time = Clock.System.now(),
                                            systolic = systolicBloodPressure.millimetersOfMercury,
                                            diastolic = diastolicBloodPressure.millimetersOfMercury,
                                            bodyPosition = null,
                                            measurementLocation = null,
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $systolicBloodPressure/$diastolicBloodPressure blood pressure")
                    }
                    writeBloodPressure
                        ?.onSuccess {
                            Text("Successfully wrote blood pressure")
                        }
                        ?.onFailure {
                            Text("Failed to write blood pressure $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var bodyFat by remember {
                        mutableStateOf(Random.nextInt(1, 100))
                    }
                    TextField(
                        value = bodyFat.toString(),
                        onValueChange = { bodyFat = it.toIntOrNull() ?: 0 },
                        label = { Text("Body fat") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeBodyFat by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeBodyFat = health.writeData(
                                    listOf(
                                        BodyFatRecord(
                                            time = Clock.System.now(),
                                            percentage = bodyFat.percent,
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $bodyFat body fat")
                    }
                    writeBodyFat
                        ?.onSuccess {
                            Text("Successfully wrote body fat")
                        }
                        ?.onFailure {
                            Text("Failed to write body fat $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var bodyTemperature by remember {
                        mutableStateOf(Random.nextInt(356, 399) / 10.0)
                    }
                    TextField(
                        value = bodyTemperature.toString(),
                        onValueChange = { bodyTemperature = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Body temperature") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeBodyTemperature by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeBodyTemperature = health.writeData(
                                    listOf(
                                        BodyTemperatureRecord(
                                            time = Clock.System.now(),
                                            temperature = Temperature.celsius(bodyTemperature),
                                            measurementLocation = null,
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $bodyTemperature body temperature")
                    }
                    writeBodyTemperature
                        ?.onSuccess {
                            Text("Successfully wrote body temperature")
                        }
                        ?.onFailure {
                            Text("Failed to write body temperature $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var writePedalingCadence by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val samplesCount = 6
                                val sampleInterval = 10.minutes
                                val endTime = Clock.System.now()
                                val startTime = endTime.minus(sampleInterval * samplesCount)
                                writePedalingCadence = health.writeData(
                                    listOf(
                                        CyclingPedalingCadenceRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            samples = List(samplesCount) {
                                                CyclingPedalingCadenceRecord.Sample(
                                                    time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                    revolutionsPerMinute = Random.nextDouble(
                                                        10.0,
                                                        150.0
                                                    ),
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        ),
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write pedaling cadence")
                    }
                    writePedalingCadence
                        ?.onSuccess {
                            Text("Successfully wrote pedaling cadence")
                        }
                        ?.onFailure {
                            Text("Failed to write pedaling cadence $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    val exerciseTypes = remember {
                        listOf(
                            ExerciseType.Biking,
                            ExerciseType.Dancing,
                            ExerciseType.Golf,
                            ExerciseType.Hiking,
                            ExerciseType.Running,
                            ExerciseType.Tennis,
                            ExerciseType.Yoga,
                        )
                    }
                    var exerciseType by remember { mutableStateOf(exerciseTypes.random()) }
                    ExerciseTypePicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        exerciseType = exerciseType,
                        onChanged = { exerciseType = it },
                    )
                    var writeExercise by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val segmentsCount = 5
                                val sampleInterval = 10.minutes
                                val endTime = Clock.System.now()
                                val startTime = endTime.minus(sampleInterval * segmentsCount)
                                writeExercise = health.writeData(
                                    listOf(
                                        ExerciseSessionRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            exerciseType = exerciseType,
                                            title = "Title ${Random.nextInt()}",
                                            notes = "Notes ${Random.nextInt()}",
                                            segments = List(segmentsCount - 1) {
                                                ExerciseSegment(
                                                    startTime = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                    endTime = startTime.plus((it * sampleInterval.inWholeMinutes + sampleInterval.inWholeMinutes).minutes),
                                                    segmentType = ExerciseSegment.Type.OtherWorkout,
                                                    repetitions = Random.nextInt(1, 10),
                                                )
                                            },
                                            laps = List(segmentsCount - 1) {
                                                ExerciseLap(
                                                    startTime = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                    endTime = startTime.plus((it * sampleInterval.inWholeMinutes + sampleInterval.inWholeMinutes).minutes),
                                                    length = Random.nextInt(1, 100).meters,
                                                )
                                            },
                                            exerciseRoute = run {
                                                val latitude = Random.nextDouble(-90.0, 90.0)
                                                val longitude = Random.nextDouble(-180.0, 180.0)

                                                ExerciseRoute(
                                                    route = List(segmentsCount - 1) {
                                                        ExerciseRoute.Location(
                                                            time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                            latitude = latitude + it.toDouble() / 100 * (it + 1),
                                                            longitude = longitude + it.toDouble() / 100 * (it - 1),
                                                            horizontalAccuracy =
                                                                Random.nextInt(1, 100).meters,
                                                            verticalAccuracy =
                                                                Random.nextInt(1, 100).meters,
                                                            altitude =
                                                                Random.nextInt(1, 100).meters,
                                                        )
                                                    }
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        ),
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write ${exerciseType::class.simpleName} exercise")
                    }
                    writeExercise
                        ?.onSuccess {
                            Text("Successfully wrote exercise")
                        }
                        ?.onFailure {
                            Text("Failed to write exercise $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var writeHeartRate by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val samplesCount = 6
                                val sampleInterval = 10.minutes
                                val endTime = Clock.System.now()
                                val startTime = endTime.minus(sampleInterval * samplesCount)
                                writeHeartRate = health.writeData(
                                    listOf(
                                        HeartRateRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            samples = List(samplesCount) {
                                                HeartRateRecord.Sample(
                                                    time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                    beatsPerMinute = Random.nextInt(40, 300),
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        ),
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write heart rate")
                    }
                    writeHeartRate
                        ?.onSuccess {
                            Text("Successfully wrote heart rate")
                        }
                        ?.onFailure {
                            Text("Failed to write heart rate $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var leanBodyMass by remember {
                        mutableStateOf(Random.nextInt(30, 60).toDouble())
                    }
                    TextField(
                        value = leanBodyMass.toString(),
                        onValueChange = { leanBodyMass = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Lean body mass, kg") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeLeanBodyMass by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeLeanBodyMass = health.writeData(
                                    listOf(
                                        LeanBodyMassRecord(
                                            time = Clock.System.now(),
                                            mass = Mass.kilograms(leanBodyMass),
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $leanBodyMass kg")
                    }
                    writeLeanBodyMass
                        ?.onSuccess {
                            Text("Successfully wrote lean body mass")
                        }
                        ?.onFailure {
                            Text("Failed to write lean body mass $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var writePower by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val samplesCount = 6
                                val sampleInterval = 10.minutes
                                val endTime = Clock.System.now()
                                val startTime = endTime.minus(sampleInterval * samplesCount)
                                writePower = health.writeData(
                                    listOf(
                                        PowerRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            samples = List(samplesCount) {
                                                PowerRecord.Sample(
                                                    time = startTime.plus((it * sampleInterval.inWholeMinutes).minutes),
                                                    power = Random.nextDouble(50.0, 300.0).watts,
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        ),
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write power")
                    }
                    writePower
                        ?.onSuccess {
                            Text("Successfully wrote power")
                        }
                        ?.onFailure {
                            Text("Failed to write power $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var writeSleep by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val startTime = Clock.System.now()
                                    .minus(12.hours)
                                val endTime = Clock.System.now()
                                    .minus(11.hours)
                                val types = listOf(
                                    SleepStageType.Awake,
                                    SleepStageType.OutOfBed,
                                    SleepStageType.Sleeping,
                                    SleepStageType.Light,
                                    SleepStageType.Deep,
                                    SleepStageType.REM,
                                )

                                writeSleep = health.writeData(
                                    listOf(
                                        SleepSessionRecord(
                                            startTime = startTime,
                                            endTime = endTime,
                                            stages = List(6) {
                                                SleepSessionRecord.Stage(
                                                    startTime = startTime.plus((10 * it).minutes),
                                                    endTime = startTime.plus((10 * it).minutes + 10.minutes),
                                                    type = types[it],
                                                )
                                            },
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write sleep")
                    }
                    writeSleep
                        ?.onSuccess {
                            Text("Successfully wrote sleep")
                        }
                        ?.onFailure {
                            Text("Failed to write sleep $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var steps by remember { mutableStateOf(Random.nextInt(1, 100)) }
                    TextField(
                        value = steps.toString(),
                        onValueChange = { steps = it.toIntOrNull() ?: 0 },
                        label = { Text("Steps") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeSteps by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeSteps = health.writeData(
                                    listOf(
                                        StepsRecord(
                                            startTime = Clock.System.now()
                                                .minus(1.hours),
                                            endTime = Clock.System.now(),
                                            count = steps,
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $steps steps")
                    }
                    writeSteps
                        ?.onSuccess {
                            Text("Successfully wrote steps")
                        }
                        ?.onFailure {
                            Text("Failed to write steps $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var height by remember { mutableStateOf(Random.nextInt(150, 200)) }
                    TextField(
                        value = height.toString(),
                        onValueChange = { height = it.toIntOrNull() ?: 0 },
                        label = { Text("Height, cm") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeHeight by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeHeight = health.writeData(
                                    listOf(
                                        HeightRecord(
                                            time = Clock.System.now(),
                                            height = Length.meters(height / 100.0),
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $height cm")
                    }
                    writeHeight
                        ?.onSuccess {
                            Text("Successfully wrote height")
                        }
                        ?.onFailure {
                            Text("Failed to write height $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var weight by remember { mutableStateOf(Random.nextInt(50, 100).toDouble()) }
                    TextField(
                        value = weight.toString(),
                        onValueChange = { weight = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Weight, kg") },
                        keyboardOptions = remember { KeyboardOptions(keyboardType = KeyboardType.Number) },
                    )
                    var writeWeight by remember { mutableStateOf<Result<Unit>?>(null) }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                writeWeight = health.writeData(
                                    listOf(
                                        WeightRecord(
                                            time = Clock.System.now(),
                                            weight = Mass.kilograms(weight),
                                            metadata = generateManualEntryMetadata(),
                                        )
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Write $weight kg")
                    }
                    writeWeight
                        ?.onSuccess {
                            Text("Successfully wrote weight")
                        }
                        ?.onFailure {
                            Text("Failed to write weight $it")
                        }
                }
            }
        }
    }
}

expect fun getPlatformName(): String
