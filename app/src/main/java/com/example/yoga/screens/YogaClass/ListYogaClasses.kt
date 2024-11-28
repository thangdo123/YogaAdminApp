package com.example.yoga.screens.YogaClass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListYogaClasses(courseId: Int) {
    val yogaCourseDao = LocalYogaCourseDao.current
    val syncCourses = LocalSyncCourses.current
    val nav = LocalNavigation.current

    var yogaClasses by remember { mutableStateOf(yogaCourseDao.getClassesByCourse(courseId)) }
    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }


    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val formatter = SimpleDateFormat("EEEE/MM/dd/yyyy", Locale.getDefault())
    var date by remember { mutableStateOf("") }
    fun handleSortByDate(date: String) {
        yogaClasses = yogaCourseDao.classesSortByDate(courseId, date)
    }

    fun convertMillisToDate(millis: Long): String {
        date = dateFormatter.format(Date(millis))
        handleSortByDate(date)
        return formatter.format(Date(millis))
    }


    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: "Select a date"

    fun clearFilter() {
        yogaClasses = yogaCourseDao.getClassesByCourse(courseId)
        datePickerState.selectedDateMillis = null
    }

    fun handleDelete(yogaClass: YogaClass) {
        yogaCourseDao.deleteClass(yogaClass.id)
        yogaClasses = yogaCourseDao.getClassesByCourse(courseId)
        CoroutineScope(Dispatchers.IO).launch {
            syncCourses()
        }
    }

    fun handleSearch(input: String) {
        searchText = input
        if (input.isBlank()) {
            yogaClasses = yogaCourseDao.getClassesByCourse(courseId)
        } else {
            yogaClasses = yogaCourseDao.classesFindByTeacher(courseId, input)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchText,
                            onValueChange = ({ handleSearch(it) }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(50.dp)
                                .padding(0.dp, 0.dp, 16.dp, 0.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray
                            ),
                            shape = RoundedCornerShape(20.dp),
                            textStyle = TextStyle.Default.merge(fontSize = 18.sp),
                            placeholder = { Text("Enter a teacher name") }
                        )
                    } else {
                        Text("List of Yoga Classes")
                    }


                },
                actions = {
                    IconButton(onClick = { nav.navigate("addYogaClass/${courseId}") }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        if (!isSearchActive) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Localized description"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Localized description",
                                modifier = Modifier.clickable {
                                    yogaClasses = yogaCourseDao.getClassesByCourse(courseId)
                                    searchText = ""
                                    isSearchActive = false
                                }
                            )
                        }
                    }
                }
            )
        }, floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("listYogaCourses") }) {
                Icon(Icons.Default.Home, contentDescription = "Add")
            }
        }) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }
                    ) {
                        Text(
                            selectedDate,
                            modifier = Modifier
                                .clickable(onClick = { showDatePicker = true })

                        )
                        Image(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    Row(modifier = Modifier.clickable {
                        clearFilter()
                    }) {
                        Text(text = "Clear filter")
                    }
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
                                .padding(16.dp)
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
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(yogaClasses) { yogaClass ->
                    YogaClassItem(
                        yogaClass,
                        onDelete = { handleDelete(yogaClass) },
                        onDetail = { nav.navigate("detailYogaClass/${courseId}&&${yogaClass.id}") })
                }
            }
        }
    }
}

@Composable
fun YogaClassItem(yogaClass: YogaClass, onDelete: () -> Unit, onDetail: () -> Unit) {
    val padding = 16.dp

    Column(
        Modifier
            .background(Color.LightGray)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(padding)
            .fillMaxWidth()
            .clickable { onDetail() }

    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = yogaClass.date,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Teacher: ${yogaClass.teacher}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )


        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Column {

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Comments: ")
                        }
                        append(yogaClass.comments ?: "none")
                    }
                )
            }
            Button(
                onClick = { onDelete() },
            ) {
                Text(text = "Delete")
            }
        }
    }
}