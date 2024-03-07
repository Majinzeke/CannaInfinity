package com.mz.cannainfinity.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.mz.cannainfinity.model.CannaLogEntry
import com.mz.cannainfinity.model.RequestState
import com.mz.cannainfinity.presentation.components.toInstant
import com.mz.cannainfinity.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.ZoneId

object MongoDB : MongoRepository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm


    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(CannaLogEntry::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<CannaLogEntry>(query = "ownerId == $0", user.id),
                        name = "User's Entries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllEntries(): Flow<Entries> {
        return if (user != null) {
            try {
                realm.query<CannaLogEntry>(query = "ownerId == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getSelectedEntry(entryId: ObjectId): Flow<RequestState<CannaLogEntry>> {
            return if (user != null) {
                try {
                    realm.query<CannaLogEntry>(query = "_id == $0", entryId).asFlow().map {
                        RequestState.Success(data = it.list.first())
                    }
                } catch (e: Exception) {
                    flow { emit(RequestState.Error(e)) }
                }
            } else {
                flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
            }
    }

    override suspend fun insertEntry(cannaLogEntry: CannaLogEntry): RequestState<CannaLogEntry> {
        return if (user != null) {
            realm.write {
                try {
                    val addedDiary = copyToRealm(cannaLogEntry.apply { ownerId = user.id })
                    RequestState.Success(data = addedDiary)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateEntry(cannaLogEntry: CannaLogEntry): RequestState<CannaLogEntry> {
        return if (user != null) {
            realm.write {
                val queriedEntry = query<CannaLogEntry>(query = "_id == $0", cannaLogEntry._id).first().find()
                if (queriedEntry != null) {
                    queriedEntry.title = cannaLogEntry.title
                    queriedEntry.description = cannaLogEntry.description
                    queriedEntry.cannaStage = cannaLogEntry.cannaStage
                    queriedEntry.images = cannaLogEntry.images
                    queriedEntry.date = cannaLogEntry.date
                    RequestState.Success(data = queriedEntry)
                } else {
                    RequestState.Error(error = Exception("Queried Diary does not exist."))
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }    }

    override suspend fun deleteEntry(id: ObjectId): RequestState<Boolean> {
        return if (user != null) {
            realm.write {
                val diary =
                    query<CannaLogEntry>(query = "_id == $0 AND ownerId == $1", id, user.id)
                        .first().find()
                if (diary != null) {
                    try {
                        delete(diary)
                        RequestState.Success(data = true)
                    } catch (e: Exception) {
                        RequestState.Error(e)
                    }
                } else {
                    RequestState.Error(Exception("Diary does not exist."))
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteAllEntries(): RequestState<Boolean> {
        return if (user != null) {
            realm.write {
                val diaries = this.query<CannaLogEntry>("ownerId == $0", user.id).find()
                try {
                    delete(diaries)
                    RequestState.Success(data = true)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }


    private class UserNotAuthenticatedException : Exception("User is not logged in.")
}