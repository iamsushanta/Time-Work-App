package iamzen.`in`.timework

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TimingContract {


    internal const val TABLE_NAME = "Timing"

    // Timing Table catch
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_URI_AUTHORITY,TABLE_NAME)
    val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"
    val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    // Timing field
    object Collum {
        const val ID = BaseColumns._ID
        const val TIMING_TASK_ID = "TimingId"
        const val TIMING_START_TIME = "StartTiming"
        const val TIMING_TASK_DURATION = "TimingDuration"
    }

    fun getId(uri: Uri):Long {
        return ContentUris.parseId(uri)
    }

    fun buildUriFromId(id:Long): Uri {
        return ContentUris.withAppendedId(CONTENT_URI,id)
    }
}
