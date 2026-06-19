package com.example.immunify.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "jadwal_vaksin")
data class LocalJadwal(
    @PrimaryKey val id: String,
    val namaVaksin: String,
    val jenis: String,
    val dosis: String,
    val scheduledTimestamp: Long,
    val urgencyLevel: String,
    val username: String
)

@Dao
interface JadwalDao {
    @Query("SELECT * FROM jadwal_vaksin WHERE username = :username ORDER BY scheduledTimestamp ASC")
    fun getUpcomingJadwal(username: String): Flow<List<LocalJadwal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJadwal(jadwal: LocalJadwal)

    @Query("DELETE FROM jadwal_vaksin WHERE id = :id")
    suspend fun deleteJadwal(id: String)
}

@Database(entities = [LocalJadwal::class], version = 1, exportSchema = false)
abstract class ImmunifyDatabase : RoomDatabase() {
    abstract fun jadwalDao(): JadwalDao
}