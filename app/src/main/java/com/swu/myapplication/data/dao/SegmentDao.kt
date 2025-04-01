package com.swu.myapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.swu.myapplication.data.model.Segment

@Dao
interface SegmentDao {
    @Query("SELECT * FROM segments WHERE noteId = :noteId ORDER BY `order` ASC")
    fun getSegmentsByNoteId(noteId: Long): LiveData<List<Segment>>

    @Insert
    suspend fun insert(segment: Segment)

    @Insert
    suspend fun insertAll(segments: List<Segment>)

    @Update
    suspend fun update(segment: Segment)

    @Delete
    suspend fun delete(segment: Segment)

    @Query("DELETE FROM segments WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT MAX(`order`) FROM segments WHERE noteId = :noteId")
    suspend fun getMaxOrderForNote(noteId: Long): Int?
} 