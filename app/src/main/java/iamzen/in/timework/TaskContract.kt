package iamzen.`in`.timework

import android.provider.BaseColumns

object TaskContract {
    internal const val TABLE_NAME = "Task"

    object Task {
        const val TASK_ID = BaseColumns._ID
        const val TASK_NAME = "Name"
        const val TASK_DESCRIPTION = "Description"
        const val TASK_SHORT_ORDER = "TaskShortOrder"
    }
}