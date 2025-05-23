package com.example.myapplicationhw1

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HighscoreStorage {
    private const val PREFS_NAME = "highscores_prefs"
    private const val KEY_HIGHSCORES = "key_highscores"
    private val gson = Gson()

    fun save(context: Context, newEntry: HighscoreEntry) {
        val currentList = load(context).toMutableList()
        currentList.add(newEntry)
        currentList.sortByDescending { it.score }
        val topTen = currentList.take(10)

        val json = gson.toJson(topTen)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_HIGHSCORES, json).apply()
    }

    fun load(context: Context): List<HighscoreEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HIGHSCORES, null) ?: return emptyList()
        val type = object : TypeToken<List<HighscoreEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_HIGHSCORES).apply()
    }
}
