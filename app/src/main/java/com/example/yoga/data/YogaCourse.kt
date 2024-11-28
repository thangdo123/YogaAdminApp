package com.example.yoga.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "yoga_courses")
data class YogaCourse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "dayOfTheWeek") val dayOfTheWeek: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "capacity") val capacity: String,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "typeOfClass") val typeOfClass: String,
    @ColumnInfo(name = "description") val description: String?,
)

@Entity(
    tableName = "yoga_classes",
    foreignKeys = [ForeignKey(
        entity = YogaCourse::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["courseId"])]
)
data class YogaClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "courseId") val courseId: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "comments") val comments: String?
)