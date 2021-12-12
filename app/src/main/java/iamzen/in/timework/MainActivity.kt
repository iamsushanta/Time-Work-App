package iamzen.`in`.timework

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import iamzen.`in`.timework.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contein_main.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() ,AddEditFragment.OnSaveClicked{

    private var mTwoPane = false
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate starts")

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)


        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG,"mTwoPane is $mTwoPane")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)

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
            supportFragmentManager.beginTransaction().remove(fragment)
        }

        task_details_container.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
        mainFragment.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG,"onSaveClicked starts")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        removeEditFragment(fragment)
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
            R.id.menumain_addTask -> taskEdiRequest(null)
//            R.id.menumain_setting -> true
             android.R.id.home -> {
                 Log.d(TAG,"home button is clicked ")
                 val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
                 removeEditFragment(fragment)
             }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Log.d(TAG,"onBackPressed starts")

        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if(fragment == null || mTwoPane){
            super.onBackPressed()
        }else{
            removeEditFragment(fragment)
        }
    }

    private fun taskEdiRequest(task:Task?){
        val newInstance = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction().replace(R.id.task_details_container,newInstance).commit()

        showFragment()
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
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }



}