package iamzen.`in`.timework

import android.net.Uri

object CurrentTimingContract {


    internal const val TABLE_NAME = "vwCurrentTiming"

    // Timing Table catch
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_URI_AUTHORITY,TABLE_NAME)
    val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"
    val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    // Timing field
    object Collum {
        const val TASK_ID = TaskContract.Collum.TASK_ID
        const val TIMING_ID = TimingContract.Collum.TIMING_TASK_ID
        const val TIMING_START_TIME = TimingContract.Collum.TIMING_START_TIME
        const val TASK_NAME = TaskContract.Collum.TASK_NAME
    }


}
