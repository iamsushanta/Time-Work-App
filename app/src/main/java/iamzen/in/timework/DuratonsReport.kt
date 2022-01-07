package iamzen.`in`.timework

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_durations.*
import kotlinx.android.synthetic.main.task_details.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class SortCollum{
    NAME,
    DESCRIPTION,
    DATE,
    DURATION
}

private const val TAG = "DurationsActivity"

@DelicateCoroutinesApi
class DurationsReport : AppCompatActivity() {

    private val reportAdapter by lazy{DurationRVAdapter(this,null)}

    private var databaseCursor: Cursor? = null

    private var sortOrder = SortCollum.NAME

     private var selection = "${DurationsContract.Collum.START_TIME} BETWEEN ? AND ? "
     private var selectionArgs = arrayOf( "9","1641563798")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate starts ")

        setContentView(R.layout.activity_durations)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        task_details_list.layoutManager = LinearLayoutManager(this)
        task_details_list.adapter = reportAdapter

        loadData()

    }

    @DelicateCoroutinesApi
    private fun loadData(){
        val order = when (sortOrder){
            SortCollum.NAME -> DurationsContract.Collum.NAME
            SortCollum.DESCRIPTION -> DurationsContract.Collum.DESCRIPTION
            SortCollum.DATE -> DurationsContract.Collum.START_DATE
            SortCollum.DURATION -> DurationsContract.Collum.DURATION
        }

        Log.d(TAG,"load Data order id $order")

        GlobalScope.launch{
            Log.d(TAG,"selection is selection $selection args is $selectionArgs, order is $order")
            val cursor = application.contentResolver.query(
                DurationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                order)
            databaseCursor = cursor
            Log.d(TAG,"cursor is $cursor ")
            reportAdapter.swapCursor(cursor)?.close()
        }
    }


}