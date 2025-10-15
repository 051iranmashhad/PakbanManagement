package ir.pakbanmanagement.other

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.pakbanmanagement.mapper.UserMapper
import kotlinx.coroutines.flow.map

object PrefManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("pref_manager")
    private val mDataStore: DataStore<Preferences> = App.appContext.dataStore
    private val mUser = stringPreferencesKey("USER")

    internal suspend fun setUser(input: UserMapper?) {
        mDataStore.edit { preferences ->
            preferences[mUser] = input?.fromMapper() ?: UserMapper().fromMapper()
        }
    }

    internal val getUser = mDataStore.data.map {
        it[mUser]?.toMapper<UserMapper>() ?: UserMapper()
    }

    internal suspend fun deleteUser() {
        setUser(null)
    }
}
