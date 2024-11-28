package com.example.yoga.screens.YogaCourse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.yoga.LocalNavigation
import com.example.yoga.LocalSyncCourses
import com.example.yoga.LocalYogaCourseDao
import com.example.yoga.R
import com.example.yoga.data.YogaCourse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailYogaCourse(courseId: Int) {
    val yogaCourseDao = LocalYogaCourseDao.current
    val nav = LocalNavigation.current
    val syncCourses = LocalSyncCourses.current
    val yogaCourse = yogaCourseDao.findById(courseId)

    var showErrorAlert by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }

    var capacity by remember { mutableStateOf(yogaCourse.capacity) }
    var duration by remember { mutableStateOf(yogaCourse.duration) }
    var price by remember { mutableStateOf(yogaCourse.price) }
    var typeOfClass by remember { mutableStateOf(yogaCourse.typeOfClass) }
    var description by remember { mutableStateOf(yogaCourse.description) }
    var dayOfTheWeek by remember { mutableStateOf(yogaCourse.dayOfTheWeek) }

    var dayExpanded by remember { mutableStateOf(false) }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    var typeExpanded by remember { mutableStateOf(false) }
    val typeOfYogaCourses = listOf("Flow Yoga", "Aerial Yoga", "Family Yoga")

    fun convertStringToTime(time: String):
            List<String> {
        val timeArr = time.split(":")
        return timeArr
    }

    val selectedTime = rememberTimePickerState(
        convertStringToTime(time = yogaCourse.time)[0].toInt(),
        convertStringToTime(time = yogaCourse.time)[1].toInt()
    )
    var showTimeInput by remember { mutableStateOf(false) }

    fun convertTimeToString(selectedTime: TimePickerState): String {
        return "${selectedTime.hour}:${if (selectedTime.minute < 10) "0${selectedTime.minute}" else selectedTime.minute}"
    }

    fun onUpdate() {
        if (dayOfTheWeek.isBlank() || convertTimeToString(selectedTime).isBlank() || capacity.isBlank() || duration.isBlank() || price.isBlank() || typeOfClass.isBlank()) {
            showErrorAlert = true
            return
        }
        val newYogaCourse = YogaCourse(
            id = yogaCourse.id,
            dayOfTheWeek = dayOfTheWeek,
            time = convertTimeToString(selectedTime),
            capacity = capacity,
            duration = duration,
            price = price,
            typeOfClass = typeOfClass,
            description = description,
        )
        yogaCourseDao.updateById(newYogaCourse)
        showSuccessAlert = true
        CoroutineScope(Dispatchers.IO).launch {
            syncCourses()
        }
    }

    if (showSuccessAlert) {
        AlertDialog(onDismissRequest = { showSuccessAlert = false },
            title = {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Update yoga course successfully"
                )
            },
            text = { Text(text = "The updated course has been updated into the system") },
            icon = { Icon(imageVector = Icons.Default.Check, contentDescription = "Success") },
            confirmButton = {
                TextButton(onClick = { showSuccessAlert = false }) {
                    Text("OK")
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Text("Yoga Course Detail")
        },
            navigationIcon = {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
            actions = {
                IconButton(onClick = { nav.navigate("listYogaClasses/${courseId}") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "List of yoga classes"
                    )
                }
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { nav.navigate("listYogaCourses") }) {
            Icon(Icons.Default.Home, contentDescription = "Add")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = typeOfClass,
                    onValueChange = { },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    readOnly = true,
                    label = {
                        Text(
                            text = "Type Of Class*"
                        )
                    })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clickable { typeExpanded = true }
                        .background(Color.Transparent)
                )
                DropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false },
                ) {
                    typeOfYogaCourses.forEachIndexed { _, s ->
                        DropdownMenuItem(text = { Text(text = s) }, onClick = {
                            typeExpanded = false
                            typeOfClass = s
                        })
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = dayOfTheWeek,
                    onValueChange = { },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    readOnly = true,
                    label = {
                        Text(
                            text = "Day of the week*"
                        )
                    })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clickable { dayExpanded = true }
                        .background(Color.Transparent)
                )
                DropdownMenu(
                    expanded = dayExpanded,
                    onDismissRequest = { dayExpanded = false },
                ) {
                    days.forEachIndexed { _, s ->
                        DropdownMenuItem(text = { Text(text = s) }, onClick = {
                            dayExpanded = false
                            dayOfTheWeek = s
                        })
                    }
                }
            }
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = capacity,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        capacity = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = "Capacity*"
                    )
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = duration,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        duration = it
                    }
                },
                label = {
                    Text(
                        text = "Duration*"
                    )
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = price,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                onValueChange = { price = it },
                label = {
                    Text(
                        text = "Price*"
                    )
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = description ?: "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { description = it },
                label = {
                    Text(
                        text = "Description"
                    )
                })
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "${selectedTime.hour}:${if (selectedTime.minute < 10) "0${selectedTime.minute}" else selectedTime.minute}",
                    onValueChange = { },
                    label = {
                        Text(
                            text = "Time*"
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimeInput = !showTimeInput }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_access_time_filled_24),
                                contentDescription = "Select time"
                            )
                        }
                    },
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clickable { showTimeInput = !showTimeInput }
                        .background(Color.Transparent)
                )
            }

            if (showTimeInput) {
                AlertDialog(onDismissRequest = { showTimeInput = false },
                    title = { Text(text = "Enter time") },
                    text = { TimeInput(state = selectedTime) },
                    confirmButton = {
                        TextButton(onClick = { showTimeInput = false }) {
                            Text("OK")
                        }
                    })

            }

            ElevatedButton(onClick = { onUpdate() }) {
                Text("Update Yoga Course")
            }


        }
    }
}