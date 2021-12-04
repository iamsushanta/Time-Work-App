package iamzen.`in`.timework

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

private const val TAG = "AppProvider"

const val CONTENT_AUTHORITY = "iamzen.in.timework.provider"
private const val TASK = 100
private const val TASK_ID = 101
private const val TIMING = 200
private const val TIMING_ID = 201
private const val DURATION = 400
private const val DURATION_ID = 401
val CONTENT_URI_AUTHORITY: Uri = Uri.parse("content://${CONTENT_AUTHORITY}")
class AppProvider : ContentProvider(){

    private val uriMatcher  by lazy {builderUriMatcher()}

    private fun builderUriMatcher():UriMatcher{
        Log.d(TAG,"UriMatcher starts $CONTENT_URI_AUTHORITY")

        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        // Task Contract add uri
        matcher.addURI(CONTENT_AUTHORITY,TaskContract.TABLE_NAME,TASK)
        matcher.addURI(CONTENT_AUTHORITY,"${TaskContract.TABLE_NAME}/#",TASK_ID)

        // Timing Contract add uri
        matcher.addURI(CONTENT_AUTHORITY,TimingContract.TABLE_NAME, TIMING)
        matcher.addURI(CONTENT_AUTHORITY,"${TimingContract.TABLE_NAME}/#", TIMING_ID)

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
        val queryMatch = uriMatcher.match(uri)
        val queryBuilder = SQLiteQueryBuilder()
        Log.d(TAG,"query Match $queryMatch")
        when (queryMatch){
            TASK -> queryBuilder.tables = TaskContract.TABLE_NAME
            TASK_ID -> {
                queryBuilder.tables = TaskContract.TABLE_NAME
                val taskId = TaskContract.getId(uri)
                queryBuilder.appendWhere("${TaskContract.Collum.TASK_ID} = ")
                queryBuilder.appendWhereEscapeString("$taskId")
            }
            TIMING -> queryBuilder.tables = TimingContract.TABLE_NAME
            TIMING_ID -> {
                queryBuilder.tables = TimingContract.TABLE_NAME
                val taskId = TimingContract.getId(uri)
                queryBuilder.appendWhere("${TimingContract.Collum.ID} = ")
                queryBuilder.appendWhereEscapeString("$taskId")
            }

//            DURATION -> queryBuilder.tables = DurationContract.TABLE_NAME
//            DURATION_ID-> {
//                queryBuilder.tables = DurationContract.TABLE_NAME
//                val taskId = DurationContract.getId(uri)
//                queryBuilder.appendWhere("${DurationContract.Collum.TASK_ID} = ")
//                queryBuilder.appendWhereEscapeString("$taskId")
//            }

            else -> throw IllegalArgumentException("Unknown uri $uri")

        }
        val context = context ?: throw NullPointerException("In insert function.  Context can't be null here!")

        val appDatabase = AppDataBase.getInstance(context).readableDatabase
        val cursor = queryBuilder.query(appDatabase,projection,selection,selectionArgs,null,null,sortOrder)
        Log.d(TAG,"query End ${cursor.count}")
         return cursor

    }

    override fun getType(uri: Uri): String {
        Log.d(TAG,"getType starts:")
        val match = uriMatcher.match(uri)
        return when (match){
            TASK -> TaskContract.CONTENT_TYPE
            TASK_ID -> TaskContract.CONTENT_ITEM_TYPE
            TIMING -> TimingContract.CONTENT_TYPE
            TIMING_ID -> TimingContract.CONTENT_ITEM_TYPE
//            TASK -> DurationContract.CONTENT_TYPE
//            TASK_ID -> DurationContract.CONTENT_ITEM_TYPE
//
            else -> throw IllegalArgumentException("uri not Match ")
        }
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri{
        Log.d(TAG,"insert starts:")
        val context = context ?: throw NullPointerException("In insert function.  Context can't be null here!")

        var recordId:Long
        var returnUri:Uri
        val match = uriMatcher.match(uri)
         when (match){
             TASK -> {
                 val db = AppDataBase.getInstance(context).writableDatabase
                 recordId = db.insert(TaskContract.TABLE_NAME,null, values)
                 if( recordId!= -1L){
                     returnUri = TaskContract.buildUriFromId(recordId)
                 } else {
                     throw SQLException("uri is invalid $uri")
                 }
             }
             TIMING -> {
                 val db = AppDataBase.getInstance(context).writableDatabase
                 recordId = db.insert(TimingContract.TABLE_NAME,null, values)
                 if( recordId!= -1L){
                     returnUri = TaskContract.buildUriFromId(recordId)
                 } else {
                     throw SQLException("uri is invalid $uri")
                 }
             }
             else -> throw IllegalArgumentException("Unknown uri $uri")

         }

        Log.d(TAG,"insert end: return uri $returnUri")
        return returnUri
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