package com.mz.cannainfinity.data

import com.mz.cannainfinity.model.CannaLogEntry
import com.mz.cannainfinity.model.RequestState
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

typealias Entries = RequestState<Map<LocalDate, List<CannaLogEntry>>>


interface MongoRepository {

    fun configureTheRealm()
    fun getAllEntries(): Flow<Entries>
    fun getSelectedEntry(entryId: ObjectId): Flow<RequestState<CannaLogEntry>>
    suspend fun insertEntry(cannaLogEntry: CannaLogEntry): RequestState<CannaLogEntry>
    suspend fun updateEntry(cannaLogEntry: CannaLogEntry): RequestState<CannaLogEntry>
    suspend fun deleteEntry(id: ObjectId): RequestState<Boolean>
    suspend fun deleteAllEntries(): RequestState<Boolean>
}