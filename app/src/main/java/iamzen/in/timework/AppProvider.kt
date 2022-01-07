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
private const val CURRENT_TIMING = 300
private const val TASK_DURATION = 400

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
        matcher.addURI(CONTENT_AUTHORITY,CurrentTimingContract.TABLE_NAME, CURRENT_TIMING)

        // Duration Contract add uri
        matcher.addURI(CONTENT_AUTHORITY,DurationsContract.TABLE_NAME, TASK_DURATION)
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
    ): Cursor {

        Log.d(TAG,"query start with $uri")
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

            CURRENT_TIMING -> {
                queryBuilder.tables = CurrentTimingContract.TABLE_NAME
            }

            TASK_DURATION -> {
                queryBuilder.tables = DurationsContract.TABLE_NAME
            }


            else -> throw IllegalArgumentException("Unknown uri $uri")

        }
        val context = context!! //?: throw NullPointerException("In insert function.  Context can't be null here!")

        val db = AppDataBase.getInstance(context).readableDatabase
        Log.d(TAG,"db is  $db projection is $projection  selection is $selection selectionArgs is $selectionArgs sortOrder is $sortOrder")
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs,null,null,sortOrder)
        Log.d(TAG,"returning count is: ${cursor.count} cursor is $cursor")


         return cursor

    }

    override fun getType(uri: Uri): String {
        Log.d(TAG,"getType starts:")
        return when (uriMatcher.match(uri)){
            TASK -> TaskContract.CONTENT_TYPE
            TASK_ID -> TaskContract.CONTENT_ITEM_TYPE

            TIMING -> TimingContract.CONTENT_TYPE
            TIMING_ID -> TimingContract.CONTENT_ITEM_TYPE

            CURRENT_TIMING -> CurrentTimingContract.CONTENT_ITEM_TYPE

            TASK_DURATION -> DurationsContract.CONTENT_TYPE

            else -> throw IllegalArgumentException("uri not Match ")
        }
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri{
        Log.d(TAG,"insert starts:")
        val context = context ?: throw NullPointerException("In insert function.  Context can't be null here!")

        val recordId:Long
        val returnUri:Uri
        val match = uriMatcher.match(uri)
        Log.d(TAG,"uri match $match")
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
                     returnUri = TimingContract.buildUriFromId(recordId)
                 } else {
                     throw SQLException("uri is invalid $uri")
                 }
             }
             else -> throw IllegalArgumentException("Unknown uri $uri")

         }

        if(recordId > 0){
            Log.d(TAG,"insert will be change")
            context.contentResolver?.notifyChange(uri,null)
        }

        Log.d(TAG,"insert end: return uri $returnUri")

        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {

        Log.d(TAG,"delete start uri is $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"uri match $match")

        val count:Int
        var selectionCriteria:String
        val context = context ?: throw NullPointerException("In update function.  Context can't be null here!")

        when(match){
            TASK -> {
                val db = AppDataBase.getInstance(context).writableDatabase
                count = db.delete(TaskContract.TABLE_NAME,selection,selectionArgs)
            }
            TASK_ID -> {
                val db = AppDataBase.getInstance(context).writableDatabase
                val id = TaskContract.getId(uri)

                selectionCriteria = "${TaskContract.Collum.TASK_ID} = $id"

                if(selection != null && selection.isNotEmpty()){
                    selectionCriteria += "AND $selection"
                }


                count = db.delete(TaskContract.TABLE_NAME,selectionCriteria,selectionArgs)

            }
            TIMING -> {
                val db = AppDataBase.getInstance(context ).writableDatabase
                count = db.delete(TimingContract.TABLE_NAME,selection,selectionArgs)
            }
            TIMING_ID -> {
                val db = AppDataBase.getInstance(context).writableDatabase
                val id = TimingContract.getId(uri)

                selectionCriteria = "${TimingContract.Collum.ID} = $id"

                if(selection != null && selection.isNotEmpty()){
                    selectionCriteria += "AND $selection"
                }
                count = db.delete(TimingContract.TABLE_NAME,selection,selectionArgs)

            }
            else -> throw IllegalArgumentException("unknown uri $uri")
        }

        if(count > 0){
            Log.d(TAG,"delete will be change")
            context.contentResolver?.notifyChange(uri,null)
        }

        Log.d(TAG,"delete exiting $count")
        return count
    }



    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {

        Log.d(TAG,"update start uri is $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"uri match $match")
        
        val count:Int
        var selectionCriteria:String
        val context = context ?: throw NullPointerException("In update function.  Context can't be null here!")

        when(match){
            TASK -> {
                val db = AppDataBase.getInstance(context ).writableDatabase
                count = db.update(TaskContract.TABLE_NAME,values,selection,selectionArgs)
            }
            TASK_ID -> {
                val db = AppDataBase.getInstance(context).writableDatabase
                val id = TaskContract.getId(uri)
                
                selectionCriteria = "${TaskContract.Collum.TASK_ID} = $id"
                
                if(selection != null && selection.isNotEmpty()){
                    selectionCriteria += "AND $selection"
                } 
                count = db.update(TaskContract.TABLE_NAME,values,selectionCriteria,selectionArgs)
                
            }
            TIMING -> {
                val db = AppDataBase.getInstance(context ).writableDatabase
                count = db.update(TimingContract.TABLE_NAME,values,selection,selectionArgs)
            }
            TIMING_ID -> {
                val db = AppDataBase.getInstance(context).writableDatabase
                val id = TimingContract.getId(uri)
                
                selectionCriteria = "${TimingContract.Collum.ID} = $id"
                
                if(selection != null && selection.isNotEmpty()){
                    selectionCriteria += "AND $selection"
                } 
                count = db.update(TimingContract.TABLE_NAME,values,selection,selectionArgs)
                
            }
            else -> throw IllegalArgumentException("unknown uri $uri")
        }
        
        Log.d(TAG,"update exiting $count")

        if(count > 0) {
            Log.d(TAG,"update will be change")
            context.contentResolver?.notifyChange(uri,null)
        }
        return count
    }


}