package com.example.immunify.di

import com.example.immunify.data.repository.AuthRepository
import com.example.immunify.data.repository.AuthRepositoryImpl
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.data.repository.RiwayatRepository
import com.example.immunify.data.repository.VaksinRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")
    }

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun providesVaksinRepository(database: FirebaseDatabase): VaksinRepository {
        return VaksinRepository(database)
    }

    @Provides
    @Singleton
    fun providesPenyediaRepository(database: FirebaseDatabase): PenyediaRepository {
        return PenyediaRepository(database)
    }

    @Provides
    @Singleton
    fun providesRiwayatRepository(database: FirebaseDatabase): RiwayatRepository {
        return RiwayatRepository(database)
    }
}