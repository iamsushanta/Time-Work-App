package iamzen.`in`.timework

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TaskContract {
    internal const val TABLE_NAME = "Task"

    val CONTENT_URI:Uri = Uri.withAppendedPath(CONTENT_URI_AUTHORITY,TABLE_NAME)
    val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"
    val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    object Collum {
        const val TASK_ID = BaseColumns._ID
        const val TASK_NAME = "Name"
        const val TASK_DESCRIPTION = "Description"
        const val TASK_SHORT_ORDER = "TaskShortOrder"
    }

    fun getId(uri: Uri):Long {
        return ContentUris.parseId(uri)
    }

    fun buildUriFromId(id:Long):Uri{
        return ContentUris.withAppendedId(CONTENT_URI,id)
    }
    }