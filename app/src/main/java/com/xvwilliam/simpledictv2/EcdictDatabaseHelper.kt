package com.xvwilliam.simpledictv2

import android.content.Context
//import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
//import java.io.File
import java.io.FileOutputStream
//import java.io.InputStream

class EcdictDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecdict.db"
        private const val DATABASE_VERSION = 1
    }

    private fun databaseExists(): Boolean {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    private fun copyDatabase() {
        try {
            val inputStream = context.assets.open(DATABASE_NAME)
            val outputStream = FileOutputStream(context.getDatabasePath(DATABASE_NAME))

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (!databaseExists()) {
            copyDatabase()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No implementation needed
    }

    fun getWordData(word: String): List<WordData> {
        val db = this.readableDatabase
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

    fun getTranslationData(translation: String): List<String> {
        val db = readableDatabase
        val query = "SELECT word, translation FROM ecdict"
        val cursor = db.rawQuery(query, null)
        val results = mutableListOf<String>()
        while (cursor.moveToNext() && results.size < 20) {
            val dataWord = cursor.getString(cursor.getColumnIndexOrThrow("word"))
            val dataTranslation = cursor.getString(cursor.getColumnIndexOrThrow("translation"))
            if (dataTranslation.contains(translation)) {
                results.add(dataWord)
            }
        }
        cursor.close()
        return results
    }
}