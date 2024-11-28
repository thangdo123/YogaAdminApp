package com.example.yoga.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [YogaCourse::class, YogaClass::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun yogaCourseDao(): YogaCourseDao
}