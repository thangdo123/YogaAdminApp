package com.example.yoga

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.yoga.data.AppDatabase
import com.example.yoga.data.YogaClassService
import com.example.yoga.data.YogaCourseDao
import com.example.yoga.data.YogaCourseService
import com.example.yoga.screens.YogaClass.AddYogaClass
import com.example.yoga.screens.YogaClass.DetailYogaClass
import com.example.yoga.screens.YogaClass.ListYogaClasses
import com.example.yoga.screens.YogaCourse.AddYogaCourse
import com.example.yoga.screens.YogaCourse.DetailYogaCourse
import com.example.yoga.screens.YogaCourse.ListYogaCourses
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

val LocalYogaCourseDao = compositionLocalOf<YogaCourseDao> { error("Not initialized") }
val LocalNavigation = compositionLocalOf<NavHostController> { error("Not initialized") }
val LocalSyncCourses = compositionLocalOf<suspend () -> Unit> { error("SyncCourses not provided") }
val LocalSyncClasses = compositionLocalOf<suspend () -> Unit> { error("YogaClasses not provided") }
    

@Composable
fun App() {
    val navigate = rememberNavController()

    val db = Room.databaseBuilder(
        context = LocalContext.current,
        AppDatabase::class.java,
        "yoga_courses"
    ).allowMainThreadQueries().build()

    val yogaCourseDao = db.yogaCourseDao()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.13:3000") // Your server URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val yogaCourseService = retrofit.create(YogaCourseService::class.java)
    val yogaClassService = retrofit.create(YogaClassService::class.java)

    suspend fun syncCourses() {
        try {
            val localCourses = yogaCourseDao.getAll() // Fetch all local courses
            val response = yogaCourseService.syncCourses(localCourses)

            if (response.isSuccessful) {
                Log.d("Sync", "Courses synced successfully")
            } else {
                Log.e("Sync", "Sync failed: ${response.errorBody()?.string()}")
            }
        } catch (e: IOException) {
            // Network error like server down or no internet connection
            Log.e("Sync", "Network error: ${e.message}")
        } catch (e: Exception) {
            // Handle other types of errors
            Log.e("Sync", "Error syncing courses: ${e.message}")
        }
    }

    suspend fun syncClasses() {
        try {
            val localClasses = yogaCourseDao.getAllClasses() // Fetch all local courses
            val response = yogaClassService.syncClasses(localClasses)

            if (response.isSuccessful) {
                Log.d("Sync", "Classes synced successfully")
            } else {
                Log.e("Sync", "Sync failed: ${response.errorBody()?.string()}")
            }
        } catch (e: IOException) {
            // Network error like server down or no internet connection
            Log.e("Sync", "Network error: ${e.message}")
        } catch (e: Exception) {
            // Handle other types of errors
            Log.e("Sync", "Error syncing classes: ${e.message}")
        }
    }


    LaunchedEffect(Unit) {
        syncCourses()
        syncClasses()
    }


    CompositionLocalProvider(
        LocalYogaCourseDao provides yogaCourseDao,
        LocalNavigation provides navigate,
        LocalSyncCourses provides { syncCourses() },
        LocalSyncClasses provides { syncClasses() }
    ) {
        NavHost(navController = navigate, startDestination = "listYogaCourses") {
            composable("listYogaCourses") { ListYogaCourses() }
            composable("addYogaCourse") { AddYogaCourse() }
            composable("detailYogaCourse/{yogaCourseId}") { navBackStackEntry ->
                val yogaCourseId = navBackStackEntry.arguments?.getString("yogaCourseId")
                yogaCourseId?.let { DetailYogaCourse(it.toInt()) }
            }

            composable("listYogaClasses/{yogaCourseId}") { navBackStackEntry ->
                val yogaCourseId = navBackStackEntry.arguments?.getString("yogaCourseId")
                yogaCourseId?.let { ListYogaClasses(it.toInt()) }
            }

            composable("addYogaClass/{yogaCourseId}") { navBackStackEntry ->
                val yogaCourseId = navBackStackEntry.arguments?.getString("yogaCourseId")
                yogaCourseId?.let { AddYogaClass(it.toInt()) }
            }

            composable("detailYogaClass/{yogaCourseId}&&{yogaClassId}") { navBackStackEntry ->
                val yogaCourseId =
                    navBackStackEntry.arguments?.getString("yogaCourseId")?.toIntOrNull()
                val yogaClassId =
                    navBackStackEntry.arguments?.getString("yogaClassId")?.toIntOrNull()

                if (yogaCourseId != null && yogaClassId != null) {
                    DetailYogaClass(courseId = yogaCourseId, classId = yogaClassId)
                }
            }

        }

    }
}