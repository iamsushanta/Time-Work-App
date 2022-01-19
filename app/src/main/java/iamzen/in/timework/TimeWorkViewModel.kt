package iamzen.`in`.timework

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val TAG = "TimeWorkViewModel"

@DelicateCoroutinesApi
class TimeWorkViewModel(application: Application): AndroidViewModel(application) {


    @DelicateCoroutinesApi
    private val contentObserver = object : ContentObserver(Handler(Looper.myLooper()!!)){
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.d(TAG,"On change starts ")
            loadTask()
        }
    }




    private val setting = application.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)
    private var ignoreLessThan = setting.getInt(SETTING_IGNORE_LESSTHAN, SETTING_DEFAULT_LESSTHAN_VALUE)

    private val settingListener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences, key ->
        when (key){
            SETTING_IGNORE_LESSTHAN -> {
                 ignoreLessThan = sharedPreferences.getInt(key , SETTING_DEFAULT_LESSTHAN_VALUE)
                Log.d(TAG,"settingIgnoreLess now than is : $ignoreLessThan")
                val values = ContentValues()

                values.put(ParametersContract.Collum.VALUE, ignoreLessThan)

                viewModelScope.launch{ Dispatchers.IO
                    getApplication<Application>().contentResolver.update(
                        ParametersContract.buildUriFromId(ParametersContract.ID_SHORT_TIMING),
                        values,
                        null,
                        null
                    )
                } // viewModelScope is end
            }
        }
    }

    private var currentTime:Timing? = null


    private val databaseCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = databaseCursor

    private val taskTiming = MutableLiveData<String?>()
    val timing : LiveData<String?>
        get() = taskTiming

    var editTaskId = 0L
    private set

    init {
        Log.d(TAG,"TimeWorkViewModel initialize")
        getApplication<Application>().contentResolver.registerContentObserver(
            TaskContract.CONTENT_URI,
            true,
            contentObserver)

        setting.registerOnSharedPreferenceChangeListener(settingListener)
        currentTime = retrieveTiming()
        loadTask()
    }




        @SuppressLint("Recycle")
        @DelicateCoroutinesApi
        private fun loadTask() {
            val projection = arrayOf(
                TaskContract.Collum.TASK_ID,
                TaskContract.Collum.TASK_NAME,
                TaskContract.Collum.TASK_DESCRIPTION,
                TaskContract.Collum.TASK_SHORT_ORDER,
            )

            viewModelScope.launch(Dispatchers.IO){
            val shortOrder =
                "${TaskContract.Collum.TASK_SHORT_ORDER}, ${TaskContract.Collum.TASK_NAME} COLLATE NOCASE"
            val cursor = getApplication<Application>().contentResolver.query(
                TaskContract.CONTENT_URI,
                projection, null, null,
                shortOrder
            )
            Log.d(TAG, cursor.toString())



            databaseCursor.postValue(cursor!!)
        }
    }

    @SuppressLint("Range")
    private fun retrieveTiming():Timing?{
        Log.d(TAG,"retrieve Timing Start...")
        val timing:Timing?

        val timingCursor:Cursor? = getApplication<Application>().contentResolver?.query(
            CurrentTimingContract.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if(timingCursor != null && timingCursor.moveToFirst()){
            val taskId = timingCursor.getLong(timingCursor.getColumnIndex(CurrentTimingContract.Collum.TASK_ID))
            val timingId = timingCursor.getLong(timingCursor.getColumnIndex(CurrentTimingContract.Collum.TIMING_ID))
            val startTime = timingCursor. getLong(timingCursor.getColumnIndex(CurrentTimingContract.Collum.TIMING_START_TIME))
            val name = timingCursor.getString(timingCursor.getColumnIndex(CurrentTimingContract.Collum.TASK_NAME))
            timing = Timing(taskId,startTime,timingId)

            taskTiming.value = name

        }else{
            timing = null

        }

        timingCursor?.close()
        Log.d(TAG,"retrieve data end")
        return timing
    }

    fun deleteTask(taskId:Long){
        viewModelScope.launch(Dispatchers.IO){
            getApplication<Application>().contentResolver?.delete(TaskContract.buildUriFromId(taskId),null,null)
        }

        if(currentTime?.taskId == taskId){
            currentTime = null
            taskTiming.value = null
        }
    }

    fun startEditing(taskId:Long){
        if(BuildConfig.DEBUG && editTaskId != 0L) throw IllegalStateException (
                "start editing called without stop previous edit : editTaskId: $editTaskId task id: $taskId"
                )
        editTaskId = taskId
    }

    fun stopEditing(){
        editTaskId = 0L
    }

    @DelicateCoroutinesApi
    fun timingTask(task:Task){
        Log.d(TAG,"timingTask called")
        val timingRecord = currentTime

        if(timingRecord == null){
            // new timing created
            currentTime = Timing(task.id)
            saveTiming(currentTime!!)

        } else{
            // exiting one tab
                Log.d(TAG,"else block called")
            timingRecord.setDuration()
            saveTiming(timingRecord)

            //second time click that mean stop time
            currentTime = if(task.id == timingRecord.taskId){
                null
            } else{
                // first time click that mean start time and save time
                val newTiming = Timing(task.id)
                saveTiming(newTiming)
                newTiming
            }

        }
        taskTiming.value = if(currentTime != null) task.Name else null
    }

    @DelicateCoroutinesApi
    private fun saveTiming(currentTiming:Timing){
        Log.d(TAG,"saveTiming called")

        // Are you update or inserting a new row
        val inserting = (currentTiming.duration == 0L)
        val values = ContentValues().apply {
            if(inserting) {
                put(TimingContract.Collum.TIMING_TASK_ID, currentTiming.taskId)
                put(TimingContract.Collum.TIMING_START_TIME, currentTiming.startTime)
            }
            put(TimingContract.Collum.TIMING_TASK_DURATION,currentTiming.duration)
        }

       viewModelScope.launch(Dispatchers.IO){

           if(inserting){
               Log.d(TAG,"inserting called $inserting")
               val uri = getApplication<Application>().contentResolver.insert(TimingContract.CONTENT_URI,
               values)
               if(uri != null){
                   currentTiming.id = TimingContract.getId(uri)
               }
           } else{
                   Log.d(TAG,"saving timing ignore lessThan $ignoreLessThan current timing duration is : ${currentTiming.duration}")
                   getApplication<Application>().contentResolver.update(
                       TimingContract.buildUriFromId(currentTiming.id),values,null,null)
           }
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
                viewModelScope.launch(Dispatchers.IO) {
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
                viewModelScope.launch(Dispatchers.IO) {
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
        setting.unregisterOnSharedPreferenceChangeListener(settingListener)
        databaseCursor.value?.close()
    }
}