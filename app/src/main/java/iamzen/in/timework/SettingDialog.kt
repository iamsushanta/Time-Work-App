package iamzen.`in`.timework

import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.setting_dialog.*
import java.util.*

private val TAG = "SettingDialog"
const val SETTING_FIRST_DAY = "firstDay"
const val SETTING_IGNORE_LESSTHAN = "IgnoreLessThan"
const val SETTING_DEFAULT_LESSTHAN_VALUE = 0

class SettingDialog: AppCompatDialogFragment() {

    private val defaultDayOfWeek = GregorianCalendar(Locale.getDefault()).firstDayOfWeek
    private var firstDay = defaultDayOfWeek
    private var ignoreLessThan = SETTING_DEFAULT_LESSTHAN_VALUE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"OnCreateView Starts:")
        return inflater.inflate(R.layout.setting_dialog,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "OnViewCreated starts:")
        super.onViewCreated(view, savedInstanceState)

        okButton.setOnClickListener{
            saveDay()
            dismiss()
        }
        cancelButton.setOnClickListener{
            dismiss()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewStateRestore starts:")
        super.onViewStateRestored(savedInstanceState)
        readValue()
        fristDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY)
        ignoreSeekBar.progress = ignoreLessThan
    }

    private fun readValue(){
        Log.d(TAG,"readValue starts: ")
        with(getDefaultSharedPreferences(context)){
            firstDay = getInt(SETTING_FIRST_DAY,defaultDayOfWeek)
            ignoreLessThan = getInt(SETTING_IGNORE_LESSTHAN, SETTING_DEFAULT_LESSTHAN_VALUE)
        }
        Log.d(TAG,"read value ends: first day $firstDay ignoreLessThan $ignoreLessThan")

    }


    private fun saveDay(){
        val newFirstDay = fristDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        val newIgnoreLess = ignoreSeekBar.progress
        Log.d(TAG,"save Day starts:")
        with(getDefaultSharedPreferences(context).edit()){
            if(newFirstDay != firstDay){
                putInt(SETTING_FIRST_DAY,newFirstDay)
            }
            if(newIgnoreLess != ignoreLessThan){
                putInt(SETTING_IGNORE_LESSTHAN,newIgnoreLess)
            }
            apply()
        }
        Log.d(TAG,"saveDay value ends: newFirst day $newFirstDay newIgnoreLessThan $newIgnoreLess")

    }

}