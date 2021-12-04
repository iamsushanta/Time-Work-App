package iamzen.`in`.timework

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import iamzen.`in`.timework.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)



        val cursor = contentResolver.query(TaskContract.CONTENT_URI,null,null,null,null)
        Log.d(TAG,"cursor is a $cursor ")
        Log.d(TAG,"**************************")
        cursor?.use {
            while(it.moveToNext()){
                    val id = it.getLong(0)
                    val name = it.getString(1)
                    val description = it.getString(2)
                    val shortOrder = it.getString(3)
                    val result = "id $id name $name description $description shortOrder $shortOrder"
                    Log.d(TAG,"result is $result")

            }

            }
        Log.d(TAG,"******************")





    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}