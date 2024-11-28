package com.example.yoga.screens.YogaCourse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoga.LocalNavigation
import com.example.yoga.LocalSyncClasses
import com.example.yoga.LocalSyncCourses
import com.example.yoga.LocalYogaCourseDao
import com.example.yoga.data.YogaCourse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListYogaCourses() {
    val yogaCourseDao = LocalYogaCourseDao.current
    val syncCourses = LocalSyncCourses.current
    val syncClasses = LocalSyncClasses.current
    val nav = LocalNavigation.current

    var yogaCourses by remember { mutableStateOf(yogaCourseDao.getAll()) }

    fun handleDelete(yogaCourse: YogaCourse) {
        yogaCourseDao.delete(yogaCourse.id)
        yogaCourseDao.deleteClassesByCourse(yogaCourse.id)
        yogaCourses = yogaCourseDao.getAll()
        CoroutineScope(Dispatchers.IO).launch {
            syncCourses()
            syncClasses()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("List of Yoga Courses")
                },
                actions = {
                    IconButton(onClick = { nav.navigate("addYogaCourse") }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            )
        },

        ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(yogaCourses) { yogaCourse ->
                    YogaCourseItem(
                        yogaCourse,
                        onDelete = { handleDelete(yogaCourse) },
                        onDetail = { nav.navigate("detailYogaCourse/${yogaCourse.id}") })
                }
            }
        }
    }
}

@Composable
fun YogaCourseItem(yogaCourse: YogaCourse, onDelete: () -> Unit, onDetail: () -> Unit) {
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
                text = yogaCourse.typeOfClass,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = yogaCourse.dayOfTheWeek,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = yogaCourse.time, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

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
                            append("Capacity: ")
                        }
                        append((yogaCourse.capacity + " people"))
                    }
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Duration: ")
                        }
                        append((yogaCourse.duration + " minutes"))
                    }
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Price: ")
                        }
                        append((yogaCourse.price + " $"))

                    }
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Description: ")
                        }
                        append(yogaCourse.description ?: "")
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