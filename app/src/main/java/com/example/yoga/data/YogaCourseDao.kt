package com.example.yoga.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@Dao
interface YogaCourseDao {
    @Query("SELECT * FROM yoga_courses ORDER BY id DESC")
    fun getAll(): List<YogaCourse>

    @Query("SELECT * FROM yoga_courses WHERE id IN (:yogaCourseId)")
    fun findById(yogaCourseId: Int): YogaCourse

    @Insert
    fun insert(yogaCourse: YogaCourse)

    @Query("DELETE FROM yoga_courses WHERE id IN (:yogaCourseId)")
    fun delete(yogaCourseId: Int)

    @Update
    fun updateById(yogaCourse: YogaCourse)

    @Query("DELETE FROM yoga_courses")
    fun deleteAll()

    @Query(
        """
    SELECT * FROM yoga_classes 
    WHERE courseId = :courseId 
    AND teacher LIKE '%' || :teacher || '%'
    ORDER BY id DESC
"""
    )
    fun classesFindByTeacher(courseId: Int, teacher: String?): List<YogaClass>

    @Query("SELECT * FROM yoga_classes WHERE courseId = :courseId AND date IN (:date)")
    fun classesSortByDate(courseId: Int, date: String?): List<YogaClass>

    @Query("SELECT * FROM yoga_classes WHERE courseId = :courseId ORDER BY id DESC")
    fun getClassesByCourse(courseId: Int): List<YogaClass>

    @Query("SELECT * FROM yoga_classes WHERE id IN (:yogaClassId)")
    fun findClassById(yogaClassId: Int): YogaClass

    @Insert
    fun insertYogaClass(yogaClass: YogaClass)

    @Update
    fun updateYogaClass(yogaClass: YogaClass)

    @Query("DELETE FROM yoga_classes WHERE id = :classId")
    fun deleteClass(classId: Int)

    @Query("DELETE FROM yoga_classes WHERE courseId = :courseId")
    fun deleteClassesByCourse(courseId: Int)

    @Query("SELECT * FROM yoga_classes ORDER BY id DESC")
    fun getAllClasses(): List<YogaClass>
}

interface YogaCourseService {
    @POST("syncYogaCourses")
    suspend fun syncCourses(@Body courses: List<YogaCourse>): Response<Void>
}

interface YogaClassService {
    @POST("syncYogaClasses")
    suspend fun syncClasses(@Body classes: List<YogaClass>): Response<Void>
}