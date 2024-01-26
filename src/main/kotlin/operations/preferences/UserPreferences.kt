package operations.preferences

import java.util.prefs.Preferences

object UserPreferences {
    private val prefs = Preferences.userRoot().node(this::class.java.name)

    fun saveLastOpenPath(path: String) {
        prefs.put("lastOpenPath", path)
    }

    fun getLastOpenPath(): String {
        return prefs.get("lastOpenPath", "")
    }
}