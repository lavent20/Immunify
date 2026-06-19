package com.example.immunify.di

import android.content.Context
import androidx.room.Room
import com.example.immunify.data.local.ImmunifyDatabase
import com.example.immunify.data.local.JadwalDao
import com.example.immunify.data.repository.AuthRepository
import com.example.immunify.data.repository.AuthRepositoryImpl
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): ImmunifyDatabase {
        return Room.databaseBuilder(
            context,
            ImmunifyDatabase::class.java,
            "immunify_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesJadwalDao(database: ImmunifyDatabase): JadwalDao {
        return database.jadwalDao()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesUserViewModel(): UserViewModel {
        return UserViewModel()
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(@ApplicationContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }
}
