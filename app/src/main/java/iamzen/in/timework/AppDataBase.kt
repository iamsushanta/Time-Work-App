package iamzen.`in`.timework

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

const val TAG = "AppDataBase"
const val DATABASE_NAME = "TimeWork"
const val DATABASE_VERSION = 1

internal class AppDataBase constructor(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null, DATABASE_VERSION
) {

    init {
        Log.d(TAG, "AppDataBase is Initialize")
    }

    override fun onCreate(db: SQLiteDatabase) {
        //CREATE TABLE Task(_id INTEGER PRIMARY KEY NOT NULL,Name TEXT NOT NULL,Description TEXT,ShortOrder INTEGER ); ̥

        val sSql = """CREATE TABLE ${TaskContract.TABLE_NAME}(
            ${TaskContract.Task.TASK_ID} INTEGER PRIMARY KEY NOT NULL,
            ${TaskContract.Task.TASK_NAME} TEXT NOT NULL,
            ${TaskContract.Task.TASK_DESCRIPTION} TEXT,
            ${TaskContract.Task.TASK_SHORT_ORDER} INTEGER);""".trimIndent()

        db.execSQL(sSql)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}