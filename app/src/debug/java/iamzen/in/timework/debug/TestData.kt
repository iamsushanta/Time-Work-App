package iamzen.`in`.timework.debug

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import iamzen.`in`.timework.TaskContract
import iamzen.`in`.timework.TimingContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

class TaskTiming internal constructor(var taskId:Long,date:Long,var duration:Long){
    var startTime:Long = 0

    init {
        startTime = duration / 1000
    }

}
object TestData {

    private const val SEC_IN_DAY = 86400
    private const val LOWER_BOUND = 100
    private const val UPPER_BOUND = 500
    private const val MAX_DURATION = SEC_IN_DAY / 6

    @SuppressLint("Range")
    fun generateTestData(contentResolver: ContentResolver){
        val projection = arrayOf(TaskContract.Collum.TASK_ID)
        val uri = TaskContract.CONTENT_URI
        val cursor = contentResolver.query(uri,projection,null,null,null)
        if( cursor != null && cursor.moveToFirst()){
            do {
                val taskId = cursor.getLong(cursor.getColumnIndex(TaskContract.Collum.TASK_ID))

                val loopCount = LOWER_BOUND + getRandomInt(UPPER_BOUND - LOWER_BOUND)
                for (i in 0 until loopCount){
                    val date = getRandomDate()
                    val duration = getRandomInt(MAX_DURATION).toLong()

                    val testTiming = TaskTiming(taskId,date,duration)

                    saveCurrentTiming(contentResolver,testTiming)

                }
            }
                while (cursor.moveToNext())
                cursor.close()
        }
    }

    private fun getRandomInt(max:Int):Int{
        return (Math.random() * max).roundToInt()
    }

    private fun getRandomDate():Long{
        val startYear = 2020
        val endYear = 2021

        val sec = getRandomInt(53)
        val min = getRandomInt(53)
        val hour = getRandomInt(23)
        val month = getRandomInt(11)

        val year = startYear + getRandomInt(endYear - startYear)
        val gc = GregorianCalendar(year,month,1)
        val day = 1 + getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - 1)
        gc.set(year,month,day ,hour,min,sec)
        return gc.timeInMillis
    }

    private fun saveCurrentTiming(contentResolver: ContentResolver,currentTime:TaskTiming){
        val values = ContentValues()
         values.put(TimingContract.Collum.TIMING_TASK_ID,currentTime.taskId)
        values.put(TimingContract.Collum.TIMING_START_TIME, currentTime.startTime)
        values.put(TimingContract.Collum.TIMING_TASK_DURATION,currentTime.duration)

        GlobalScope.launch {
            contentResolver.insert(TimingContract.CONTENT_URI,values)
        }

    }
}