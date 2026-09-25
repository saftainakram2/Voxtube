package com.example.data.local

import androidx.room.*
import com.example.data.model.AudioProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM audio_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<AudioProject>>

    @Query("SELECT * FROM audio_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): AudioProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: AudioProject): Long

    @Update
    suspend fun updateProject(project: AudioProject)

    @Delete
    suspend fun deleteProject(project: AudioProject)

    @Query("DELETE FROM audio_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("UPDATE audio_projects SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
