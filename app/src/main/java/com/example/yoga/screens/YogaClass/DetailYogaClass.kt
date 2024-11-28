package com.example.yoga.screens.YogaClass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.yoga.LocalNavigation
import com.example.yoga.LocalSyncCourses
import com.example.yoga.LocalYogaCourseDao
import com.example.yoga.data.YogaClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailYogaClass(courseId: Int, classId: Int) {
    val yogaCourseDao = LocalYogaCourseDao.current
    val nav = LocalNavigation.current
    val syncCourses = LocalSyncCourses.current

    val chosenYogaCourse = yogaCourseDao.findById(courseId)
    val chosenYogaClass = yogaCourseDao.findClassById(classId)

    val formatter = SimpleDateFormat("EEEE/MM/dd/yyyy", Locale.getDefault())
    val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

    var showErrorAlert by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }

    var teacher by remember { mutableStateOf(chosenYogaClass.teacher) }
    var comments by remember { mutableStateOf(chosenYogaClass.comments ?: "") }
    var date by remember { mutableStateOf(chosenYogaClass.date) }
    var dayOfTheWeek by remember { mutableStateOf("") }

    fun convertMillisToDate(millis: Long): String {
        dayOfTheWeek = dayOfWeekFormatter.format(Date(millis))
        date = dateFormatter.format(Date(millis))
        return formatter.format(Date(millis))
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateFormatter.parse(date).time
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: formatter.format(dateFormatter.parse(date))


    fun onUpdate() {
        // Check if any required attribute is empty
        if (dayOfTheWeek.isBlank() || teacher.isBlank() || date.isBlank() || dayOfTheWeek != chosenYogaCourse.dayOfTheWeek) {
            showErrorAlert = true
            return
        }

        val newYogaClass = YogaClass(
            date = date,
            teacher = teacher,
            comments = comments,
            courseId = courseId,
            id = chosenYogaClass.id
        )


        yogaCourseDao.updateYogaClass(newYogaClass)
        showSuccessAlert = true
        CoroutineScope(Dispatchers.IO).launch {
            syncCourses() // Call the syncCourses function provided through CompositionLocal
        }
    }

    if (showErrorAlert) {
        AlertDialog(
            onDismissRequest = { showErrorAlert = false },
            title = { Text(textAlign = TextAlign.Center, text = "An error has occurred") },
            text = { Text(text = "Please fill in all required fields!") },
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Error") },
            confirmButton = {
                TextButton(onClick = { showErrorAlert = false }) {
                    Text("OK")
                }
            },
        )
    }



    if (showSuccessAlert) {
        AlertDialog(onDismissRequest = { showSuccessAlert = false },
            title = {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Update yoga class successfully"
                )
            },
            text = { Text(text = "The updated class has been updated into the system") },
            icon = { Icon(imageVector = Icons.Default.Check, contentDescription = "Success") },
            confirmButton = {
                TextButton(onClick = { showSuccessAlert = false }) {
                    Text("OK")
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Update Yoga Class")
            },
            navigationIcon = {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
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

            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = teacher,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { teacher = it },
                label = {
                    Text(
                        text = "Teacher*"
                    )
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = comments,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { comments = it },
                label = {
                    Text(
                        text = "Comments"
                    )
                })


            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = selectedDate,
                    onValueChange = { },
                    label = {
                        Text(text = "Date*")
                    },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = !showDatePicker }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                            )
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clickable { showDatePicker = !showDatePicker }
                        .background(Color.Transparent)
                )
            }


            if (showDatePicker) {
                Popup(
                    onDismissRequest = { showDatePicker = false },
                    alignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        DatePicker(
                            state = datePickerState, showModeToggle = false
                        )
                        Button(onClick = {
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }



            if (dayOfTheWeek != chosenYogaCourse.dayOfTheWeek && dayOfTheWeek.isNotBlank()) {
                Text(
                    text = "Wrong day of the week (The day must be ${chosenYogaCourse.dayOfTheWeek})",
                    color = Color.Red
                )
            }


            ElevatedButton(onClick = { onUpdate() }) {
                Text("Update Yoga Class")
            }


        }
    }
}



