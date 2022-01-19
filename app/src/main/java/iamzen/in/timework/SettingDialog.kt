package iamzen.`in`.timework

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.setting_dialog.*
import java.util.*


private const val TAG = "SettingDialog"
const val SETTING_FIRST_DAY = "firstDay"
const val SETTING_IGNORE_LESSTHAN = "IgnoreLessThan"
const val SETTING_DEFAULT_LESSTHAN_VALUE = 0

const val MY_PREFS_NAME = "name"

//                              0,1,2, 3, 4, 5, 6, 7 , 8, 9.10,11,12, 13, 14,15, 16, 17, 18, 19 ,20, 21, 22, 23, 24
private val deltas = intArrayOf(0, 5,10,15,20,25,30, 35,40,45,50,55,60,120,180,240,300,360,420,480,540,600,900,1800,2700)

class SettingDialog : AppCompatDialogFragment() {

    private val defaultDayOfWeek = GregorianCalendar(Locale.getDefault()).firstDayOfWeek

    private var firstDay = defaultDayOfWeek
    private var ignoreLessThan = SETTING_DEFAULT_LESSTHAN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called.")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SettingsDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "OnCreateView Starts:")
        return inflater.inflate(R.layout.setting_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "OnViewCreated starts:")
        super.onViewCreated(view, savedInstanceState)
        dialog?.setTitle(R.string.action_settings)
        ignoreSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 12) {
                    IgnoreSecondTitle.text = getString(
                        R.string.settingsIgnoreSecondsTitle,
                        deltas[progress],
                        resources.getQuantityString(R.plurals.settingsLittleUnits, deltas[progress])
                    )
                } else {
                    val minutes = deltas[progress] / 60
                    IgnoreSecondTitle.text = getString(
                        R.string.settingsIgnoreSecondsTitle,
                        minutes,
                        resources.getQuantityString(R.plurals.settingsBigUnits, minutes)
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // unused function
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // unused function
            }
        })
        okButton.setOnClickListener {
            saveDay()
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestore starts:")
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState == null) {
            readValue()

            fristDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY)
            val seekBar = deltas.binarySearch(ignoreLessThan)
            if (seekBar < 0) {
                Log.d(TAG, "seekbar is $seekBar")
                throw IndexOutOfBoundsException("index not found $ignoreLessThan, ")
            }
            ignoreSeekBar.max = deltas.size - 1
            ignoreSeekBar.progress = seekBar

            if (ignoreLessThan < 60) {
                IgnoreSecondTitle.text = getString(
                    R.string.settingsIgnoreSecondsTitle,
                    ignoreLessThan,
                    resources.getQuantityString(R.plurals.settingsLittleUnits, ignoreLessThan)
                )
            } else {
                val minute = ignoreLessThan / 60
                IgnoreSecondTitle.text = getString(
                    R.string.settingsIgnoreSecondsTitle,
                    minute,
                    resources.getQuantityString(R.plurals.settingsBigUnits, minute)
                )
            }
            Log.d(TAG, "ignoreLess Than is ${IgnoreSecondTitle.text}")
        }

    }

    private fun readValue() {
        Log.d(TAG, "readValue starts: ")
        with(context?.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)) {
            firstDay = this?.getInt(SETTING_FIRST_DAY, defaultDayOfWeek)!!
            ignoreLessThan = this.getInt(SETTING_IGNORE_LESSTHAN, SETTING_DEFAULT_LESSTHAN_VALUE)

        }
        // my course code up this code i am created
//        with(getDefaultSharedPreferences(context)){
//            firstDay = getInt(SETTING_FIRST_DAY,defaultDayOfWeek)
//            ignoreLessThan = getInt(SETTING_IGNORE_LESSTHAN, SETTING_DEFAULT_LESSTHAN_VALUE)
//        }
        Log.d(TAG, "read value ends: first day $firstDay ignoreLessThan $ignoreLessThan")
//
    }


    private fun saveDay() {
        val newFirstDay = fristDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        val newIgnoreLess = deltas[ignoreSeekBar.progress]
        Log.d(TAG, "save Day starts:")

        with(context?.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)?.edit()) {
            if (newFirstDay != firstDay) {
                this?.putInt(SETTING_FIRST_DAY, newFirstDay)
            }
            if (newIgnoreLess != ignoreLessThan) {
                this?.putInt(SETTING_IGNORE_LESSTHAN, newIgnoreLess)
            }
            this?.apply()
        }

        // this bellow old version code.

//        with(getDefaultSharedPreferences(context).edit()){
//            if(newFirstDay != firstDay){
//                putInt(SETTING_FIRST_DAY,newFirstDay)
//            }
//            if(newIgnoreLess != ignoreLessThan){
//                putInt(SETTING_IGNORE_LESS_THAN,newIgnoreLess)
//            }
//            apply()
//        }
        Log.d(TAG, "saveDay value ends: newFirst day $newFirstDay newIgnoreLessThan $newIgnoreLess")

    }
}