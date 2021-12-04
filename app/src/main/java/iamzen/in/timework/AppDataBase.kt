package iamzen.`in`.timework

/**
 * Author Name: Sushanta Das
 *
 * App DataBase only know [AppProvider] class
 */
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDataBase"
const val DATABASE_NAME = "TimeWork.db"
const val DATABASE_VERSION = 3

internal class AppDataBase private constructor(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null, DATABASE_VERSION
) {

    init {
        Log.d(TAG, "AppDataBase is Initialize")
    }

    override fun onCreate(db: SQLiteDatabase) {
        //CREATE TABLE Task(_id INTEGER PRIMARY KEY NOT NULL,Name TEXT NOT NULL,Description TEXT,ShortOrder INTEGER ); ̥

        Log.d(TAG,"onCreate Start ")
        val sSql = """CREATE TABLE ${TaskContract.TABLE_NAME}(
            ${TaskContract.Collum.TASK_ID} INTEGER PRIMARY KEY NOT NULL,
            ${TaskContract.Collum.TASK_NAME} TEXT NOT NULL,
            ${TaskContract.Collum.TASK_DESCRIPTION} TEXT,
            ${TaskContract.Collum.TASK_SHORT_ORDER} INTEGER);""".replaceIndent(" ")

        Log.d(TAG,sSql)

        db.execSQL(sSql)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG,"OnUpgrade start")
        when(oldVersion){
            1 -> {
                // some code hare
                Log.d(TAG,"Some Code hare")
            } else -> {
                Log.d(TAG,"Exception Unknown newVersion $newVersion")
            }
        }
    }

    // This code SingletonHolder Class call easy way for understand
    companion object : SingletonHolder<AppDataBase,Context> (::AppDataBase)


//    companion object {
//
//
//        @Volatile
//        var instance:AppDataBase? = null
//
//        fun getInstance(context: Context):AppDataBase =
//         instance ?: synchronized(AppDataBase)  {
//             instance ?: AppDataBase(context).also { instance = it }
//         }
//
//    }
}