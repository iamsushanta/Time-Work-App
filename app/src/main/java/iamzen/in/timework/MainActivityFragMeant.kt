package iamzen.`in`.timework

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main_acitivity_frag_ment.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainActivityFragMent.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TAG = "MainActivityFragment"
private const val DIALOG_ID_DELETED = 1
private const val DIALOG_TASK_ID = "task_id"

class MainActivityFragMeant : Fragment(),
    CursorRecyclerViewAdapter.WorkingButton,
    AppDialog.DialogEvents{


    private val mViewModel by lazy{ ViewModelProviders.of(this).get(TimeWorkViewModel::class.java)}
    private val mAdapter = CursorRecyclerViewAdapter(null,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreated starts")
        mViewModel.cursor.observe(this, { cursor -> mAdapter.swapCursor(cursor)?.close()})
        mViewModel.timing.observe(this, Observer<String>{

                timings -> DisplayTitle.text = if (timings != null){
                    Log.d(TAG,"Timings starts is called")
                    getString(R.string.TimingRecordShow,timings)
        } else{
            Log.d(TAG,"Timing is not starts ")
            getString(R.string.MainTitle)
        }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView starts")
        return inflater.inflate(R.layout.fragment_main_acitivity_frag_ment,container,false)

    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: called")
        super.onAttach(context)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"listOfItem starts")

        List_Of_Item.layoutManager = LinearLayoutManager(context)
        List_Of_Item.adapter = mAdapter

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: called")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored: called")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
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

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: called")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: called")
        super.onDetach()
    }

    // working Button interface is implement
    override fun editTask(task: Task) {
        (activity as ManageWorkingButton?)?.editTaskButton(task)
    }

    override fun deleteTask(task: Task) {
        Log.d(TAG,"deleteTask called ")
        val args = Bundle().apply {
            putInt(DIALOG_ID,DIALOG_ID_DELETED)
            putString(DIALOG_MESSAGE,getString(R.string.dialog_message_delete,task.id,task.Name))
            putInt(DIALOG_POSITIVE,R.string.delete_dialog)
            putLong(DIALOG_TASK_ID,task.id) // pass  the id in the arguments .so we can retrieve id than what task callback.
            }

        val dialog = AppDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager,null)
    }

    override fun longClick(task: Task) {
        Log.d(TAG,"longClick called")
        mViewModel.timingTask(task)
    }

    interface ManageWorkingButton{
        fun editTaskButton(task: Task)

    }

    override fun setOnPositiveRid(dialogId: Int, args: Bundle) {
        Log.d(TAG,"setOnPositiveRid called dialog id is $dialogId")

        if (dialogId == DIALOG_ID_DELETED){
            val taskId = args.getLong(DIALOG_TASK_ID)
            if(BuildConfig.DEBUG && taskId == 0L) throw AssertionError("task id is 0")
            mViewModel.deleteTask(taskId)
        }
    }

//    override fun setOnNegativeRid(dialogId: Int, args: Bundle) {
//
//        Log.d(TAG,"setONNegativeRid called")
//    }
//
//    override fun setOnCancel(dialogId: Int) {
//
//        Log.d(TAG,"setOnCancel called")
//    }
}