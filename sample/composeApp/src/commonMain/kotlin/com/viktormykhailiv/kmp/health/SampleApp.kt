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
import com.viktormykhailiv.kmp.health.HealthDataType.HeartRate
import com.viktormykhailiv.kmp.health.HealthDataType.Height
import com.viktormykhailiv.kmp.health.HealthDataType.Sleep
import com.viktormykhailiv.kmp.health.HealthDataType.Steps
import com.viktormykhailiv.kmp.health.HealthDataType.Weight
import com.viktormykhailiv.kmp.health.aggregate.BloodGlucoseAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.BloodPressureAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeartRateAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.HeightAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.SleepAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.StepsAggregatedRecord
import com.viktormykhailiv.kmp.health.aggregate.WeightAggregatedRecord
import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.MealType
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.sleep.SleepSessionCanvas
import com.viktormykhailiv.kmp.health.units.BloodGlucose as BloodGlucoseUnit
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random
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
            HeartRate,
            Height,
            Sleep,
            Steps,
            Weight,
        )
    }
    val writeTypes = remember {
        listOf(
            BloodGlucose,
            BloodPressure,
            HeartRate,
            Height,
            Sleep,
            Steps,
            Weight,
        )
    }

    var isAvailableResult by remember { mutableStateOf(Result.success(false)) }
    var isAuthorizedResult by remember { mutableStateOf<Result<Boolean>?>(null) }
    var isRevokeSupported by remember { mutableStateOf(false) }

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
                            Text("Read $type")
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
                            Text("Aggregate $type")
                        }

                        records[type]
                            ?.onSuccess { records ->
                                Text("$type records count ${records.size}")

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

                                    is HeartRate -> {
                                        val heartRates = records.filterIsInstance<HeartRateRecord>()
                                            .map { it.samples }
                                            .flatten()
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
                                Text("Failed to read $type records $it")
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
                                Text("Failed to read $type records $it")
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
                            Text("Blood glucose wrote successfully")
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
                            Text("Steps wrote blood pressure")
                        }
                        ?.onFailure {
                            Text("Failed to write blood pressure $it")
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
                            Text("Heart rate wrote successfully")
                        }
                        ?.onFailure {
                            Text("Failed to write heart rate $it")
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
                            Text("Sleep wrote successfully")
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
                            Text("Steps wrote successfully")
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
                            Text("Height wrote successfully")
                        }
                        ?.onFailure {
                            Text("Failed to write height $it")
                        }

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    var weight by remember { mutableStateOf(Random.nextInt(50, 100)) }
                    TextField(
                        value = weight.toString(),
                        onValueChange = { weight = it.toIntOrNull() ?: 0 },
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
                                            weight = Mass.kilograms(weight.toDouble()),
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
                            Text("Weight wrote successfully")
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
