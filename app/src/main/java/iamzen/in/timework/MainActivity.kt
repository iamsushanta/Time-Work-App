package iamzen.`in`.timework

import android.content.ContentValues
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


//        Add a new data in databases
//        testInsert("WHY COMPANY ","WORLD'S NO 1 VALUABLE COMPANY",3)
//        testInsert("zen ","hy my name is zen",2)
//        testInsert("sushanta ","Hy my name is sushanta",1)

//        update All row in databases
//        testUpdate("zen","my Name is zen I am specialist of Artificial Intelligence ")

        // update one row in databases
        testUpdateTwo(2,"sushanta","Hy I am avneet kaur",3,null,null)
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

    private fun testInsert(name: String,description:String,shortOrder:Int){
        val value = ContentValues().apply {
            put(TaskContract.Collum.TASK_NAME,name)
            put(TaskContract.Collum.TASK_DESCRIPTION,description)
            put(TaskContract.Collum.TASK_SHORT_ORDER,shortOrder)
        }

        val valueInsert = contentResolver.insert(TaskContract.CONTENT_URI,value)

        Log.d(TAG,"insert is successfully $valueInsert")

    }

    // all row delete in function when you call modified
    private fun testDelete(name:String, description: String? = null, shortOrder:Int? = null){
        val value = ContentValues().apply {
            put(TaskContract.Collum.TASK_NAME,name)
            put(TaskContract.Collum.TASK_DESCRIPTION,description)
            put(TaskContract.Collum.TASK_SHORT_ORDER,shortOrder)
        }

        val valueUpdate = contentResolver.update(TaskContract.CONTENT_URI,value,null,null)
    }

    // All item will be update
    private fun testUpdate(name:String, description: String? = null, shortOrder:Int? = null){
        val value = ContentValues().apply {
            put(TaskContract.Collum.TASK_NAME,name)
            put(TaskContract.Collum.TASK_DESCRIPTION,description)
            put(TaskContract.Collum.TASK_SHORT_ORDER,shortOrder)
        }

        val valueUpdate = contentResolver.update(TaskContract.CONTENT_URI,value,null,null)
    }

    // single item will update
    private fun testUpdateTwo(rowNumber:Long,name:String,
                              description: String? = null,
                              shortOrder:Int? = null,
                              selection:String? = null,
                              selectionArgs:Array<String>? = null
    ){
        val value = ContentValues().apply {
            put(TaskContract.Collum.TASK_NAME,name)
            put(TaskContract.Collum.TASK_DESCRIPTION,description)
            put(TaskContract.Collum.TASK_SHORT_ORDER,shortOrder)
        }

        val whichRow = TaskContract.buildUriFromId(rowNumber)

        // also which row under which value update
//        val selection = "${TaskContract.Collum.TASK_SHORT_ORDER} = ?;"
//        val selectionArgs = arrayOf("3")
        val valueUpdate = contentResolver.update(whichRow
            ,value
            , selection
            , selectionArgs)
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