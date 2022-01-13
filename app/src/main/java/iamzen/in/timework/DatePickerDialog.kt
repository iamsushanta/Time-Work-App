package iamzen.`in`.timework

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.*

private  const val TAG = "DatePickerDialog"
const val DATE_PICKER_ID = "ID"
const val DATE_PICKER_TITLE = "TITLE"
const val DATE_PICKER_DATE = "DATE"
const val DATE_PICKER_FDW = "FIRST DAY"

class DatePickerDialogFragment: AppCompatDialogFragment(),DatePickerDialog.OnDateSetListener {

    private var dialogID = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = GregorianCalendar()
        var title:String? = null
        val arguments = arguments
        if(arguments != null){
            dialogID = arguments.getInt(DATE_PICKER_ID)
            title = arguments.getString(DATE_PICKER_TITLE)
            val givenDate = arguments.getSerializable(DATE_PICKER_DATE) as Date?
            if(givenDate != null){
                cal.time = givenDate
                Log.d(TAG,"retrieve date is $givenDate ")
            }

        }

        val year = cal.get(GregorianCalendar.YEAR)
        val month = cal.get(GregorianCalendar.MONTH)
        val day = cal.get(GregorianCalendar.DAY_OF_MONTH)
        val dbp = MyDatePickerDialog(requireContext(),this,year,month,day)

        if(title != null){
            dbp.setTitle(title)
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            val firstDayOfWeek = arguments?.getInt(DATE_PICKER_FDW,cal.firstDayOfWeek) ?: cal.firstDayOfWeek
            dbp.datePicker.firstDayOfWeek = firstDayOfWeek
        }

        return dbp
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context !is DatePickerDialog.OnDateSetListener){
            throw ClassCastException("must be implement DatePickerDialog.OnDateSetLister interface")
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        view.tag = dialogID

        (context as DatePickerDialog.OnDateSetListener?)?.onDateSet(view,year,month,dayOfMonth)
        Log.d(TAG,"onDate set is done")
    }
}