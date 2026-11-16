package com.example.bookshelf.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/** The only class that touches DataStore. */
class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val READ_FILTER = stringPreferencesKey("read_filter")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_LOW_RATED = booleanPreferencesKey("hide_low_rated")
    }

    val preferences: Flow<UserPreferences> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e("PreferencesRepository", "Error reading preferences, using defaults", e)
                emit(emptyPreferences()) // maps to defaults below
            } else {
                throw e // anything else is a real bug: rethrow
            }
        }
        .map { p ->
            val d = UserPreferences()
            UserPreferences(
                readFilter = p[Keys.READ_FILTER]?.let { runCatching { ReadFilter.valueOf(it) }.getOrNull() } ?: d.readFilter,
                sortOrder = p[Keys.SORT_ORDER]?.let { runCatching { SortOrder.valueOf(it) }.getOrNull() } ?: d.sortOrder,
                hideLowRated = p[Keys.HIDE_LOW_RATED] ?: d.hideLowRated,
            )
        }

    suspend fun setReadFilter(filter: ReadFilter) {
        dataStore.edit { it[Keys.READ_FILTER] = filter.name }
    }

    suspend fun setSortOrder(order: SortOrder) {
        dataStore.edit { it[Keys.SORT_ORDER] = order.name }
    }

    suspend fun setHideLowRated(hide: Boolean) {
        dataStore.edit { it[Keys.HIDE_LOW_RATED] = hide }
    }
}
