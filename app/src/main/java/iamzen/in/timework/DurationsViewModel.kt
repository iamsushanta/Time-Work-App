package iamzen.`in`.timework

import android.app.Application
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

enum class SortCollum{
    NAME,
    DESCRIPTION,
    DATE,
    DURATION
}

private const val TAG = "DurationsViewModel"
@DelicateCoroutinesApi
class DurationsViewModel(application: Application): AndroidViewModel(application) {


    private val contentObserver = object : ContentObserver(Handler()){
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.d(TAG,"On change starts ")
            loadData()
        }
    }


    private var calendar = GregorianCalendar()

    private val setting = application.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE)
    private var _firstDay = setting.getInt(SETTING_FIRST_DAY,calendar.firstDayOfWeek)
    val firstDayOfWeek
    get() = _firstDay


    private val brodCrustReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG,"onReceive is start")

            val action = intent?.action
            if(action == Intent.ACTION_TIMEZONE_CHANGED || action == Intent.ACTION_LOCALE_CHANGED){
                val currentTime = calendar.timeInMillis
                calendar = GregorianCalendar()
                calendar.timeInMillis = currentTime
                _firstDay = setting.getInt(SETTING_FIRST_DAY,calendar.firstDayOfWeek)
                calendar.firstDayOfWeek = firstDayOfWeek
                applyFilter()
            }
        }
    }

    private val settingListener = SharedPreferences.OnSharedPreferenceChangeListener{sharedPreferences,key ->
        when (key){
            SETTING_FIRST_DAY -> {
                _firstDay = sharedPreferences.getInt(key,calendar.firstDayOfWeek)
                calendar.firstDayOfWeek = firstDayOfWeek
                Log.d(TAG,"calendar first day of week $firstDayOfWeek")
                applyFilter()
            }
        }
    }

    private val databaseCursor = MutableLiveData<Cursor>()
    val cursor: LiveData<Cursor>
        get() = databaseCursor

    @DelicateCoroutinesApi
    var shortOrder = SortCollum.NAME
        set(order){
            if(field != order){
                field = order
                loadData()
            }
        }

    private var selection = "${DurationsContract.Collum.START_TIME} BETWEEN ? AND ? "
    private var selectionArgs = emptyArray<String>()
    private var _displayweek = true

    val displayWeek:Boolean
    get() = _displayweek





    init {
        Log.d(TAG,"duration is created firstsWeek is $setting ")
        calendar.firstDayOfWeek = _firstDay

        application.contentResolver.registerContentObserver(TimingContract.CONTENT_URI,true,contentObserver)
        val brodCrustFilter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
        brodCrustFilter.addAction(Intent.ACTION_LOCALE_CHANGED)
        application.registerReceiver(brodCrustReceiver,brodCrustFilter)

        setting.registerOnSharedPreferenceChangeListener(settingListener)
        applyFilter()
    }

    fun toggleDisplayWeek(){
        _displayweek = !_displayweek
        applyFilter()
    }

    fun dateFilter():Date{
        return calendar.time
    }

    fun setReportDate(year: Int,month: Int,dayOfMonth: Int){
        if((calendar.get(GregorianCalendar.YEAR) != year)
            || calendar.get(GregorianCalendar.MONTH) != month
                || calendar.get(GregorianCalendar.DAY_OF_MONTH) != dayOfMonth){
            calendar.set(year,month,dayOfMonth,0,0,0)
            applyFilter()

        }
    }

    private fun applyFilter(){
        Log.d(TAG,"started applyFilter")
        val currentCalendarDate = calendar.timeInMillis

        if(displayWeek){
           //show record entirety week
            val startWeek = calendar.firstDayOfWeek

            calendar.set(GregorianCalendar.DAY_OF_WEEK,startWeek)
            calendar.set(GregorianCalendar.HOUR_OF_DAY,0)
            calendar.set(GregorianCalendar.MINUTE,0)
            calendar.set(GregorianCalendar.SECOND,0)
            val startDate = calendar.timeInMillis / 1000

            calendar.add(GregorianCalendar.DATE,6)
            calendar.set(GregorianCalendar.HOUR_OF_DAY,23)
            calendar.set(GregorianCalendar.MINUTE,59)
            calendar.set(GregorianCalendar.SECOND,59)

            val endDate = calendar.timeInMillis / 1000

            selectionArgs = arrayOf(startDate.toString(),endDate.toString())
            Log.d(TAG,"apply (filter 7) selectionArgs start:$startDate, end:$endDate")
        } else{
            // re-query date picker
            calendar.set(GregorianCalendar.HOUR_OF_DAY,0)
            calendar.set(GregorianCalendar.MINUTE,0)
            calendar.set(GregorianCalendar.SECOND,0)
            val startDate = calendar.timeInMillis / 1000

            calendar.set(GregorianCalendar.HOUR_OF_DAY,23)
            calendar.set(GregorianCalendar.MINUTE,59)
            calendar.set(GregorianCalendar.SECOND,59)
            val endDate = calendar.timeInMillis / 1000

            selectionArgs = arrayOf(startDate.toString(),endDate.toString())
            Log.d(TAG,"apply (filter 1) selectionArgs is set startDate:$startDate, endDate:$endDate")
        }

        calendar.timeInMillis = currentCalendarDate
        loadData()
    }

    @DelicateCoroutinesApi
    private fun loadData(){
        val order = when (shortOrder){
            SortCollum.NAME -> DurationsContract.Collum.NAME
            SortCollum.DESCRIPTION -> DurationsContract.Collum.DESCRIPTION
            SortCollum.DATE -> DurationsContract.Collum.START_DATE
            SortCollum.DURATION -> DurationsContract.Collum.DURATION
        }

        Log.d(TAG,"load Data order id $order")

        GlobalScope.launch{
            Log.d(TAG,"selection is selection $selection args is $selectionArgs, order is $order")
            val cursor = getApplication<Application>().contentResolver.query(
                DurationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                order)
            databaseCursor.postValue(cursor)
            Log.d(TAG,"cursor is $cursor ")
        }
    }

    fun deleteDuration(timeInMillis:Long){
        Log.d(TAG,"deleteDuration is started")

        val delDuration = timeInMillis / 1000

        val selectionArgs = arrayOf(delDuration.toString())
        val selection = "${TimingContract.Collum.TIMING_START_TIME} < ?"

        GlobalScope.launch{
            getApplication<Application>().contentResolver.delete(TimingContract.CONTENT_URI,selection,selectionArgs)
        }

        Log.d(TAG,"deleteDuration exerting.")

    }

    override fun onCleared() {
        Log.d(TAG,"OnCleared called")
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
        getApplication<Application>().unregisterReceiver(brodCrustReceiver)
        setting.unregisterOnSharedPreferenceChangeListener(settingListener)
    }

}
