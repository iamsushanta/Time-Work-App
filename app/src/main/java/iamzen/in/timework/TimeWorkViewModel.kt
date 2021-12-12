package iamzen.`in`.timework

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class TimeWorkViewModel(application: Application): AndroidViewModel(application) {

    private val databaseCursor = MutableLiveData<Cursor>()
    var cursor: LiveData<Cursor> = databaseCursor

    private fun loadData(){
        val projection = arrayOf(TaskContract.Collum.TASK_ID,
            TaskContract.Collum.TASK_NAME,
            TaskContract.Collum.TASK_DESCRIPTION,
            TaskContract.Collum.TASK_SHORT_ORDER
            )
        val shortOrder = "${TaskContract.Collum.TASK_SHORT_ORDER}, ${TaskContract.Collum.TASK_NAME}"
        val cursor = getApplication<Application>().contentResolver?.query(TaskContract.CONTENT_URI,
            projection,null,null,shortOrder)
        databaseCursor.postValue(cursor)
    }
}