package iamzen.`in`.timework

import android.app.Application
import android.content.ContentValues
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val TAG = "TimeWorkViewModel"
class TimeWorkViewModel(application: Application): AndroidViewModel(application) {

    private val contentObserver = object : ContentObserver(Handler()){
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




        private fun loadTask() {
            val projection = arrayOf(
                TaskContract.Collum.TASK_ID,
                TaskContract.Collum.TASK_NAME,
                TaskContract.Collum.TASK_DESCRIPTION,
                TaskContract.Collum.TASK_SHORT_ORDER
            )

            GlobalScope.launch{
            val shortOrder =
                "${TaskContract.Collum.TASK_SHORT_ORDER}, ${TaskContract.Collum.TASK_NAME}"
            val cursor = getApplication<Application>().contentResolver.query(
                TaskContract.CONTENT_URI,
                projection, null, null, shortOrder
            )
            Log.d(TAG, cursor.toString())



            databaseCursor.postValue(cursor)
        }
    }

    fun deleteTask(taskId:Long){
        GlobalScope.launch{
            getApplication<Application>().contentResolver?.delete(TaskContract.buildUriFromId(taskId),null,null)
        }
    }

    fun saveTask(task:Task):Task{
        Log.d(TAG,"saveTask called viewModel")
        val value = ContentValues()

        if(task.Name.isNotEmpty()) {
            Log.d(TAG,"task name is not empty")
            value.put(TaskContract.Collum.TASK_NAME, task.Name)
            value.put(TaskContract.Collum.TASK_DESCRIPTION, task.Description)
            value.put(TaskContract.Collum.TASK_SHORT_ORDER, task.ShortOrder)

            if (task.id == 0L) {
                GlobalScope.launch {
                    Log.d(TAG, "add new task")
                    val uri = getApplication<Application>().contentResolver.insert(
                        TaskContract.CONTENT_URI,
                        value
                    )
                    if (uri != null) {
                        task.id = TaskContract.getId(uri)
                        Log.d(TAG, "saving new task id ${task.id}")
                    }
                }
            } else{
                GlobalScope.launch {
                    Log.d(TAG,"updating task ")
                    getApplication<Application>().contentResolver.update(TaskContract.buildUriFromId(task.id),value,null,null)
                }
            }
        }

        return  task
    }

    override fun onCleared() {
        Log.d(TAG,"OnCleared called")
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}