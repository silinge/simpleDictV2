package com.xvwilliam.simpledictv2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class EcdictDatabaseHelper(private val context: Context) {

    companion object {
        private const val DATABASE_NAME = "ecdict.db"
    }

    init {
        createDatabase()
    }

    private fun createDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (!dbFile.exists()) {
            try {
                copyDatabaseFromAssets()
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }
        }
    }

    private fun copyDatabaseFromAssets() {
        val inputStream = context.assets.open(DATABASE_NAME)
        val outputDatabase = context.getDatabasePath(DATABASE_NAME)
        val outputStream = FileOutputStream(outputDatabase)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }

    fun getReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(
            context.getDatabasePath(DATABASE_NAME).path,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }

    // 查询单词数据
    fun getWordData(word: String): List<WordData> {
        val db = getReadableDatabase()
        val query = "SELECT word, phonetic, definition, translation FROM ecdict WHERE word = ?"
        val cursor = db.rawQuery(query, arrayOf(word))
        val results = mutableListOf<WordData>()
        while (cursor.moveToNext()) {
            val dataWord = cursor.getString(cursor.getColumnIndexOrThrow("word"))
            val phonetic = cursor.getString(cursor.getColumnIndexOrThrow("phonetic"))
            val definition = cursor.getString(cursor.getColumnIndexOrThrow("definition"))
            val translation = cursor.getString(cursor.getColumnIndexOrThrow("translation"))
            results.add(WordData(dataWord, phonetic, definition, translation))
        }
        cursor.close()
        return results
    }

    // 查询翻译数据
    fun getTranslationData(translation: String): List<String> {
        val db = getReadableDatabase()
        val query = "SELECT word FROM ecdict WHERE translation LIKE ? LIMIT 20"
        val cursor = db.rawQuery(query, arrayOf("%$translation%"))
        val results = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val dataWord = cursor.getString(cursor.getColumnIndexOrThrow("word"))
            results.add(dataWord)
        }
        cursor.close()
        return results
    }
}

// 数据类，用于存储单词信息
data class WordData(
    val word: String,
    val phonetic: String,
    val definition: String,
    val translation: String
)