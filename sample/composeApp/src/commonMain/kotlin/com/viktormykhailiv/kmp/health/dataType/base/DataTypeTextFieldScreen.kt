package com.viktormykhailiv.kmp.health.dataType.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthRecord
import com.viktormykhailiv.kmp.health.LocalHealthManager
import com.viktormykhailiv.kmp.health.ui.AppBar
import com.viktormykhailiv.kmp.health.ui.AppButton
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@Composable
inline fun <T, reified R : HealthRecord> DataTypeTextFieldScreen(
    title: String,
    type: HealthDataType,
    noinline initialValue: () -> T,
    noinline serializer: (T) -> String,
    noinline deserializer: (String) -> T,
    noinline writer: (T) -> List<R>,
    noinline listContent: @Composable (List<R>) -> Unit,
) {
    DataTypeTextFieldScreen<T, R, HealthAggregatedRecord>(
        title = title,
        type = type,
        initialValue = initialValue,
        serializer = serializer,
        deserializer = deserializer,
        writer = writer,
        aggregatedContent = null,
        listContent = listContent,
    )
}

@Composable
inline fun <T, reified R : HealthRecord, reified A : HealthAggregatedRecord> DataTypeTextFieldScreen(
    title: String,
    type: HealthDataType,
    noinline initialValue: () -> T,
    noinline serializer: (T) -> String,
    noinline deserializer: (String) -> T,
    noinline writer: (T) -> List<R>,
    noinline aggregatedContent: (@Composable (A) -> Unit)?,
    noinline listContent: @Composable (List<R>) -> Unit,
) {
    DataTypeScreen(
        recordKClass = R::class,
        aggregatedRecordKClass = A::class,
        title = title,
        type = type,
        initialValue = initialValue,
        writer = writer,
        pickerContent = { controller ->
            DataTypeScreenDefaults.TextField(
                controller = controller,
                title = title,
                serializer = serializer,
                deserializer = deserializer,
            )
        },
        aggregatedContent = aggregatedContent,
        listContent = listContent,
    )
}

@Composable
inline fun <T, reified R : HealthRecord> DataTypeScreen(
    title: String,
    type: HealthDataType,
    noinline initialValue: () -> T,
    noinline writer: (T) -> List<R>,
    noinline pickerContent: @Composable ColumnScope.(DataTypeScreenPickerController<T>) -> Unit,
    noinline listContent: @Composable (List<R>) -> Unit,
) {
    DataTypeScreen<T, R, HealthAggregatedRecord>(
        title = title,
        type = type,
        initialValue = initialValue,
        writer = writer,
        pickerContent = pickerContent,
        aggregatedContent = null,
        listContent = listContent,
    )
}

@Composable
inline fun <T, reified R : HealthRecord, reified A : HealthAggregatedRecord> DataTypeScreen(
    title: String,
    type: HealthDataType,
    noinline initialValue: () -> T,
    noinline writer: (T) -> List<R>,
    noinline pickerContent: @Composable ColumnScope.(DataTypeScreenPickerController<T>) -> Unit,
    noinline aggregatedContent: (@Composable (A) -> Unit)?,
    noinline listContent: @Composable (List<R>) -> Unit,
) {
    DataTypeScreen(
        recordKClass = R::class,
        aggregatedRecordKClass = A::class,
        title = title,
        type = type,
        initialValue = initialValue,
        writer = writer,
        pickerContent = pickerContent,
        aggregatedContent = aggregatedContent,
        listContent = listContent,
    )
}

@Composable
fun <T, R : HealthRecord, A : HealthAggregatedRecord> DataTypeScreen(
    recordKClass: KClass<R>,
    aggregatedRecordKClass: KClass<A>?,
    title: String,
    type: HealthDataType,
    initialValue: () -> T,
    writer: (T) -> List<R>,
    pickerContent: @Composable ColumnScope.(controller: DataTypeScreenPickerController<T>) -> Unit,
    aggregatedContent: (@Composable (A) -> Unit)?,
    listContent: @Composable (List<R>) -> Unit,
) {
    val healthManager = LocalHealthManager.current
    val coroutineScope = rememberCoroutineScope()

    val pickerController = remember { DataTypeScreenPickerControllerImpl(initialValue) }
    var readResult by remember { mutableStateOf<Result<List<R>>?>(null) }
    var aggregatedResult by remember { mutableStateOf<Result<A>?>(null) }
    var writeResult by remember { mutableStateOf<Result<Unit>?>(null) }

    fun readData() {
        coroutineScope.launch {
            readResult = healthManager.readData(
                startTime = Clock.System.now()
                    .minus(7.days),
                endTime = Clock.System.now(),
                type = type,
            ).map { list ->
                list
                    .filter { it::class == recordKClass }
                    .map {
                        @Suppress("UNCHECKED_CAST")
                        it as R
                    }
            }
        }
    }

    val isAggregateSupported = aggregatedRecordKClass != null && aggregatedContent != null
    fun aggregateData() {
        if (!isAggregateSupported) return
        coroutineScope.launch {
            aggregatedResult = healthManager.aggregate(
                startTime = Clock.System.now()
                    .minus(7.days),
                endTime = Clock.System.now(),
                type = type,
            ).map {
                @Suppress("UNCHECKED_CAST")
                it as A
            }
        }
    }

    LaunchedEffect(Unit) {
        readData()
        aggregateData()
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                withNavigationButton = true,
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppButton(
                text = "Read",
                onClick = { readData() },
            )
            readResult
                ?.onSuccess { listContent(it) }
                ?.onFailure { Text("Failed to read $it") }

            Divider()

            if (isAggregateSupported) {
                AppButton(
                    text = "Aggregate",
                    onClick = { aggregateData() },
                )
                aggregatedResult
                    ?.onSuccess { aggregatedContent(it) }
                    ?.onFailure { Text("Failed to aggregate $it") }

                Divider()
            }

            pickerContent(pickerController)

            AppButton(
                text = pickerController.value
                    .takeIf { it != Unit }
                    ?.let { "Write \"$it\"" }
                    ?: "Write",
                onClick = {
                    coroutineScope.launch {
                        val records = writer(pickerController.value)
                        writeResult = healthManager.writeData(records)
                            .onSuccess {
                                pickerController.value = initialValue()
                                readData()
                            }
                    }
                },
            )

            writeResult
                ?.onSuccess { Text("Successfully wrote") }
                ?.onFailure { Text("Failed to write $it") }
        }
    }
}

object DataTypeScreenDefaults {

    @Composable
    fun <T> TextField(
        controller: DataTypeScreenPickerController<T>,
        title: String,
        serializer: (T) -> String,
        deserializer: (String) -> T,
        keyboardType: KeyboardType = KeyboardType.Number,
    ) {
        TextField(
            value = serializer(controller.value),
            onValueChange = { controller.value = deserializer(it) },
            label = { Text(title) },
            keyboardOptions = remember(keyboardType) { KeyboardOptions(keyboardType = keyboardType) },
        )
    }
}

interface DataTypeScreenPickerController<T> {

    var value: T

}

class DataTypeScreenPickerControllerImpl<T>(
    initialValue: () -> T,
) : DataTypeScreenPickerController<T> {

    override var value: T by mutableStateOf(initialValue())

}
