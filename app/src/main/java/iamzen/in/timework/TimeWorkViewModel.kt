package iamzen.`in`.timework

import android.app.Application
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


private const val TAG = "TimeWorkViewModel"
class TimeWorkViewModel(application: Application): AndroidViewModel(application) {

    val contentObserver = object : ContentObserver(Handler()){
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.d(TAG,"On change starts ")
            loadTask()
        }
    }
    private val databaseCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = databaseCursor

    init {
        Log.d(TAG,"TimeWorkViewModel initialize")
        getApplication<Application>().contentResolver.registerContentObserver(
            TaskContract.CONTENT_URI,
            true,
            contentObserver)
        loadTask()
    }

    private fun loadTask(){
        val projection = arrayOf(TaskContract.Collum.TASK_ID,
            TaskContract.Collum.TASK_NAME,
            TaskContract.Collum.TASK_DESCRIPTION,
            TaskContract.Collum.TASK_SHORT_ORDER
            )
        val shortOrder = "${TaskContract.Collum.TASK_SHORT_ORDER}, ${TaskContract.Collum.TASK_NAME}"
        val cursor = getApplication<Application>().contentResolver.query(TaskContract.CONTENT_URI,
            projection,null,null,shortOrder)
        Log.d(TAG,cursor.toString())



        databaseCursor.postValue(cursor)
    }

    override fun onCleared() {
        Log.d(TAG,"OnCleared called")
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}