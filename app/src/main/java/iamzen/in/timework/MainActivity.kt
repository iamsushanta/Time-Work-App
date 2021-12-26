package iamzen.`in`.timework

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contein_main.*

private const val TAG = "MainActivity"
const val DIALOG_ID_CANCEL = 1
class MainActivity : AppCompatActivity() ,AddEditFragment.OnSaveClicked,MainActivityFragMeant.ManageWorkingButton,
         AppDialog.DialogEvents{

    private var mTwoPane = false

    private var aboutDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate starts")
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG,"mTwoPane is $mTwoPane")
        val fragment = findFragmentById(R.id.task_details_container)

        if(fragment != null){
            showFragment()
        }else{
            task_details_container.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
            mainFragment.visibility = View.VISIBLE
        }
    }



    private fun showFragment(){
        Log.d(TAG,"showFragment starts")
        task_details_container.visibility = View.VISIBLE
        mainFragment.visibility = if (mTwoPane) View.VISIBLE else View.GONE
    }

    private fun removeEditFragment(fragment: Fragment? = null){
        Log.d(TAG,"removeEditFragment starts")
        if(fragment != null){
            Log.d(TAG,"fragment not a null")
//            supportFragmentManager.beginTransaction().remove(fragment)
            removeFragment(fragment)
        }

        task_details_container.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
        mainFragment.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG,"onSaveClicked starts")
        removeEditFragment(findFragmentById(R.id.task_details_container))
    }

    //
    

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         when (item.itemId) {
             R.id.menumain_aboutApp -> showAboutDialog()
            R.id.menumain_addTask -> taskEdiRequest(null)
            R.id.menumain_setting -> {
                val settingDialog = SettingDialog()
                settingDialog.show(supportFragmentManager,null)

            }
             android.R.id.home -> {
                 Log.d(TAG,"home button is clicked ")
                 val fragment = findFragmentById(R.id.task_details_container)
//                 removeEditFragment(fragment)
                 if ((fragment is AddEditFragment) && fragment.isDirty()){
                     showConfirmationDialog(DIALOG_ID_CANCEL,
                         getString(R.string.cancel_diegg_message),
                         R.string.Dialog_id_del_show,
                         R.string.Dialog_id_del_negative_show)
                 } else {
                     removeEditFragment(fragment)
                 }
             }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Log.d(TAG,"onBackPressed starts")

        val fragment = findFragmentById(R.id.task_details_container)
        if(fragment == null || mTwoPane){
            super.onBackPressed()
        }else{
            if ((fragment is AddEditFragment) && fragment.isDirty()){
                showConfirmationDialog(DIALOG_ID_CANCEL,
                    getString(R.string.cancel_diegg_message),
                    R.string.Dialog_id_del_show,
                    R.string.Dialog_id_del_negative_show)
            } else {
                removeEditFragment(fragment)
            }
        }
    }

    override fun setOnPositiveRid(dialogId: Int, args: Bundle) {
        Log.d(TAG,"setOnPositiveRid called")
        if(dialogId == DIALOG_ID_CANCEL){
            val fragment = findFragmentById(R.id.task_details_container)
            removeEditFragment(fragment)
        }

    }



    override fun editTaskButton(task: Task) {
        Log.d(TAG,"editTaskButton clicked")
        taskEdiRequest(task)
    }



    private fun taskEdiRequest(task:Task?){
//        val newInstance = AddEditFragment.newInstance(task)
//        supportFragmentManager.beginTransaction().replace(R.id.task_details_container,newInstance).commit()

        replaceFragment(AddEditFragment.newInstance(task),R.id.task_details_container)
        showFragment()
    }

    // show about dialog in function
    private fun showAboutDialog(){
        val massageView = layoutInflater.inflate(R.layout.about,null,false)
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.app_name)
        builder.setIcon(R.drawable.ic_launcher_foreground)

        builder.setPositiveButton(R.string.ok){dialog,which ->
            if(aboutDialog != null && aboutDialog?.isShowing == true) run {
                aboutDialog?.dismiss()
            }
        }
        aboutDialog = builder.setView(massageView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)

        val aboutVersion = massageView.findViewById(R.id.about_version) as TextView
        aboutVersion.text = BuildConfig.VERSION_NAME
        val about_url:TextView? = massageView.findViewById(R.id.about_url)
        about_url?.setOnClickListener {v ->
            val intent = Intent(Intent.ACTION_VIEW)
            val s = (v as TextView).text.toString()
            intent.data = Uri.parse(s)
            try {
                startActivity(intent)
            } catch(e: ActivityNotFoundException){
                Toast.makeText(this@MainActivity,R.string.webLinkClickError,Toast.LENGTH_LONG).show()

            }
        }
        aboutDialog?.show()

    }

    // Login mainActivity lifecycle
    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState: called")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState: called")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
        if(aboutDialog?.isShowing == true){
            aboutDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }



}