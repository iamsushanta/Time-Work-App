package iamzen.`in`.timework

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_durations.*
import kotlinx.android.synthetic.main.task_details.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


private const val TAG = "DurationsActivity"

private const val DIALOG_FILTER = 1
private const val DIALOG_DELETE = 2
private const val DELETION_DATE = "Deletion date"

@DelicateCoroutinesApi
class DurationsReport : AppCompatActivity() , View.OnClickListener,
    AppDialog.DialogEvents,
     DatePickerDialog.OnDateSetListener{

//    private val durationsViewModel by lazy { ViewModelProvider(this).get(DurationsViewModel::class.java)}
    private val durationsViewModel :DurationsViewModel by viewModels()
    private val reportAdapter by lazy{DurationRVAdapter(this,null)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate starts ")

        setContentView(R.layout.activity_durations)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        task_details_list.layoutManager = LinearLayoutManager(this)
        task_details_list.adapter = reportAdapter

        durationsViewModel.cursor.observe(this,{cursor -> reportAdapter.swapCursor(cursor)?.close()})

        task_name.setOnClickListener(this)
        task_duration.setOnClickListener(this)
        task_date.setOnClickListener(this)
        task_description?.setOnClickListener(this) // description is  not present in prorate mode

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.rm_title_period -> {
                durationsViewModel.toggleDisplayWeek()
                invalidateOptionsMenu()
                return true
            }
            R.id.rm_title_data -> {
                showDialogFragment(getString(R.string.date_title_filter), DIALOG_FILTER)
                return true
            }
            R.id.rm_title_delete -> {
                showDialogFragment(getString(R.string.optionDeleteSelecte), DIALOG_DELETE)
                return true
            }
            R.id.rm_setting -> {
                val dialog = SettingDialog()
                dialog.show(supportFragmentManager, "setting ")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.rm_title_period)
        if(item != null){
            if(durationsViewModel.displayWeek){
                item.setIcon(R.drawable.ic_baseline_filter_1_24)
                item.setTitle(R.string.rm_filter_day)

            }else{
                item.setIcon(R.drawable.ic_baseline_filter_7_24)
                item.setTitle(R.string.rm_filter_week)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onClick(v: View) {
        Log.d(TAG,"onClick is clicked ${v.id}")
        when(v.id){
            R.id.task_name -> durationsViewModel.shortOrder = SortCollum.NAME
            R.id.task_description -> durationsViewModel.shortOrder = SortCollum.DESCRIPTION
            R.id.task_duration -> durationsViewModel.shortOrder = SortCollum.DURATION
            R.id.task_date -> durationsViewModel.shortOrder = SortCollum.DATE
            else -> throw IllegalArgumentException("wrong button clicked")
        }
    }

    private fun showDialogFragment(title:String,dialogId:Int){

        val dialogFragment = DatePickerDialogFragment()

        val arguments = Bundle()
        arguments.putInt(DATE_PICKER_ID,dialogId)
        arguments.putString(DATE_PICKER_TITLE,title)
        arguments.putSerializable(DATE_PICKER_DATE,durationsViewModel.dateFilter())
        arguments.putInt(DATE_PICKER_FDW,durationsViewModel.firstDayOfWeek)
        dialogFragment.arguments = arguments
        dialogFragment.show(supportFragmentManager,"datePicker")

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(TAG,"onDateSet called")
        Log.d(TAG,"dialog id is : ${view.tag}")


        when (val dialogID = view.tag!! as Int){
            DIALOG_FILTER ->{
                durationsViewModel.setReportDate(year,month,dayOfMonth)
            }

            DIALOG_DELETE -> {
                val cal = GregorianCalendar()
                cal.set(year,month,dayOfMonth,0,0,0)
                val dateFormat = DateFormat.getDateFormat(this).format(cal.time)

                val dialog = AppDialog()
                val args = Bundle()
                args.putInt(DIALOG_ID, DIALOG_DELETE)
                args.putString(DIALOG_MESSAGE,getString(R.string.deleteSureTitle,dateFormat))
                args.putLong(DELETION_DATE,cal.timeInMillis)
                dialog.arguments = args
                dialog.show(supportFragmentManager,"dialog is deleted")
            }
            else -> throw IllegalArgumentException("InValidate mode selected $dialogID")
        }
    }

    override fun setOnPositiveRid(dialogId: Int, args: Bundle) {
        Log.d(TAG,"setOnPositiveRid called dialogId is $dialogId")
        if(dialogId == DIALOG_DELETE){
            Log.d(TAG,"dialog id is same")
            val delDate = args.getLong(DELETION_DATE)
            durationsViewModel.deleteDuration(delDate)
        }
    }
}