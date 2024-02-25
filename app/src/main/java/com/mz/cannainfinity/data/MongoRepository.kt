package com.mz.cannainfinity.data

import com.mz.cannainfinity.model.CannaLogEntry
import com.mz.cannainfinity.model.RequestState
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

typealias Entries = RequestState<Map<LocalDate, List<CannaLogEntry>>>


interface MongoRepository {

    fun configureTheRealm()
    fun getAllEntries(): Flow<Entries>
}