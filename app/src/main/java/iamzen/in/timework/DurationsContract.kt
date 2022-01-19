package iamzen.`in`.timework

import android.net.Uri

object DurationsContract {
    internal const val TABLE_NAME = "vwTaskDuration"

    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_URI_AUTHORITY, TABLE_NAME)

    const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    // Timing field
    object Collum {
       const val NAME = TaskContract.Collum.TASK_NAME
        const val DESCRIPTION = TaskContract.Collum.TASK_DESCRIPTION
        const val START_TIME = TimingContract.Collum.TIMING_START_TIME
        const val DURATION = TimingContract.Collum.TIMING_TASK_DURATION
        const val START_DATE = "StartDate"
    }

}