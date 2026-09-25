package com.example.data.local

import androidx.room.*
import com.example.data.model.VoiceCloneProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceCloneDao {
    @Query("SELECT * FROM voice_clones ORDER BY createdAt DESC")
    fun getAllClones(): Flow<List<VoiceCloneProfile>>

    @Query("SELECT * FROM voice_clones WHERE id = :id")
    suspend fun getCloneById(id: Long): VoiceCloneProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClone(clone: VoiceCloneProfile): Long

    @Update
    suspend fun updateClone(clone: VoiceCloneProfile)

    @Delete
    suspend fun deleteClone(clone: VoiceCloneProfile)

    @Query("DELETE FROM voice_clones WHERE id = :id")
    suspend fun deleteCloneById(id: Long)
}
