package iamzen.`in`.timework

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import iamzen.`in`.timework.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contein_main.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() ,AddEditFragment.OnSaveClicked{

    private var mPane = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)





       


    }

    private fun removeEditFragment(fragment: Fragment? = null){
        if(fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment)
        }

        task_details_container.visibility = if(mPane) View.VISIBLE else View.GONE
        mainFragment.visibility = View.VISIBLE
    }

    override fun onSaveClicked() {
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun taskEdiRequest(task:Task?){
        val newInstance = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction().replace(R.id.task_details_container,newInstance).commit()

    }


}