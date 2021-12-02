package iamzen.`in`.timework

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext

private const val TAG = "AppProvider"

const val CONTENT_AUTHORITY = "iamzen.in.timework.provider"
private const val TASK = 100
private const val TASK_ID = 101
private const val TIMING = 200
private const val TIMING_ID = 201
private const val DURATION = 400
private const val DURATION_ID = 401
val CONTENT_URI_AUTHORITY: Uri = Uri.parse("content://$CONTENT_AUTHORITY")
class AppProvider : ContentProvider(){

    private val uriMatcher  by lazy {builderUriMatcher()}

    private fun builderUriMatcher():UriMatcher{
        Log.d(TAG,"UriMatcher starts $CONTENT_URI_AUTHORITY")

        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        matcher.addURI(CONTENT_AUTHORITY,TaskContract.TABLE_NAME,TASK)
        matcher.addURI(CONTENT_AUTHORITY,"${TaskContract.TABLE_NAME}/#",TASK_ID)

        return matcher

    }

    override fun onCreate(): Boolean {
        Log.d(TAG,"OnCreate: Starts")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        Log.d(TAG,"query starts")
        val context = requireContext(this)
      val queryBuilder = SQLiteQueryBuilder()
        val queryMatch = uriMatcher.match(uri)
        Log.d(TAG,"query Match $queryMatch")
        when (queryMatch){
            TASK -> queryBuilder.tables = TaskContract.TABLE_NAME
            TASK_ID -> {
                queryBuilder.tables = TaskContract.TABLE_NAME
                val taskId = TaskContract.getId(uri)
                queryBuilder.appendWhere("${TaskContract.Task.TASK_ID} = $taskId")
            }

            else -> throw IllegalArgumentException("Unknown uri $uri")

        }
        val appDatabase = AppDataBase.getInstance(context).readableDatabase
        val cursor = queryBuilder.query(appDatabase,projection,selection,selectionArgs,null,null,sortOrder)
        Log.d(TAG,"query End")
         return cursor

    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }



    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }


}