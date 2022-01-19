package iamzen.`in`.timework

import android.util.Log
import java.util.*

private const val TAG = "Timing"


class Timing(var taskId:Long, val startTime:Long = Date().time / 1000, var id:Long = 0){


    var duration: Long = 0
    private set

    fun setDuration(){
        duration = Date().time / 1000 - startTime
        Log.d(TAG," $taskId startTime is $startTime Duration is $")
    }
}