package data.local

import domain.model.WorkSettings
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.util.prefs.Preferences

class PreferencesRepository {
    private val prefs = Preferences.userRoot().node("com/worktime/tracker")
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    companion object {
        private const val KEY_WORK_SETTINGS = "work_settings"
    }
    
    fun saveSettings(settings: WorkSettings) {
        val jsonString = json.encodeToString(settings)
        prefs.put(KEY_WORK_SETTINGS, jsonString)
        prefs.flush()
    }
    
    fun loadSettings(): WorkSettings {
        val jsonString = prefs.get(KEY_WORK_SETTINGS, null)
        return if (jsonString != null) {
            try {
                json.decodeFromString(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                WorkSettings()
            }
        } else {
            WorkSettings()
        }
    }
}
