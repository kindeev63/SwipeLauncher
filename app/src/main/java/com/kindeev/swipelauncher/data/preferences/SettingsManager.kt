package com.kindeev.swipelauncher.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingsManager(
    private val context: Context
) {

    private val Context.myDataStore by preferencesDataStore(name = "settings")
    private val OPEN_LAST_APP = booleanPreferencesKey("OPEN_LAST_APP")
    private val TEXT_COLOR_ON_WALLPAPER = intPreferencesKey("TEXT_COLOR_ON_WALLPAPER")
    private val PICK_APP_ACTION_WITH_IMAGE = booleanPreferencesKey("PICK_APP_ACTION_WITH_IMAGE")
    private val OPEN_APP_WHEN_CLICK_ON_CLOCK = stringPreferencesKey("OPEN_APP_WHEN_CLICK_ON_CLOCK")

    val openLastApp: Flow<Boolean> = context.myDataStore.data.map {
        it[OPEN_LAST_APP] ?: false
    }

    suspend fun updateOpenLastApp(value: Boolean) {
        context.myDataStore.edit {
            it[OPEN_LAST_APP] = value
        }
    }

    val textColorOnWallpaper: Flow<Int> = context.myDataStore.data.map {
        it[TEXT_COLOR_ON_WALLPAPER] ?: 0
    }

    suspend fun updateTextColorOnWallpaper(value: Int) {
        context.myDataStore.edit {
            it[TEXT_COLOR_ON_WALLPAPER] = value
        }
    }

    val pickAppActionWithImage: Flow<Boolean> = context.myDataStore.data.map {
        it[PICK_APP_ACTION_WITH_IMAGE] ?: true
    }

    suspend fun updatePickAppActionWithImage(value: Boolean) {
        context.myDataStore.edit {
            it[PICK_APP_ACTION_WITH_IMAGE] = value
        }
    }

    val openAppWhenClickOnClock: Flow<String?> = context.myDataStore.data.map {
        it[OPEN_APP_WHEN_CLICK_ON_CLOCK]
    }

    suspend fun updateOpenAppWhenClickOnClock(value: String?) {
        context.myDataStore.edit {
            if (value == null) {
                it.remove(OPEN_APP_WHEN_CLICK_ON_CLOCK)
            } else {
                it[OPEN_APP_WHEN_CLICK_ON_CLOCK] = value
            }
        }
    }
}