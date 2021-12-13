package iamzen.`in`.timework

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_edit.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val TAG = "AddEditFragment"
class AddEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var task: Task? = null
    private var listener:OnSaveClicked? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"OnCreate starts")
        super.onCreate(savedInstanceState)
        task = arguments?.getParcelable(ARG_TASK)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView Starts")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null){
            val task = task
            if(task != null){
                Log.d(TAG,"existing view modified create task is ${task.id}")
                addEditName.setText(task.Name)
                addEditDescription.setText(task.Description)
                addEditShortOrder.setText(task.ShortOrder.toString())
            } else{
                Log.d(TAG,"no argument have new item add")

            }
        }
    }


    // add a new task in a databases Or Exiting task will be modified
    private fun saveTask(){
        Log.d(TAG,"saveTask called")
        val shortOrder = if(addEditShortOrder.text.isNotEmpty()){
            Integer.parseInt(addEditShortOrder.text.toString())
        } else  0
        
        val task = task
        val value = ContentValues()
        if(task != null){
            Log.d(TAG,"exiting item will me modified")
            if(addEditName.text.toString() != task.Name){
                value.put(TaskContract.Collum.TASK_NAME,addEditName.text.toString())
            }
            if(addEditDescription.text.toString() != task.Description){
                value.put(TaskContract.Collum.TASK_DESCRIPTION,addEditDescription.text.toString())
            }
            if(shortOrder != task.ShortOrder){
                value.put(TaskContract.Collum.TASK_ID, shortOrder)
            }
            if (value.size() != 0){
                activity?.contentResolver?.update(TaskContract.buildUriFromId(task.id),value,null,null)
            }
        } else{
            Log.d(TAG,"new Item Add in databases")
            if(addEditName.text.isNotEmpty()){
                value.put(TaskContract.Collum.TASK_NAME,addEditName.text.toString())
                if(addEditDescription.text.isNotEmpty()){
                    value.put(TaskContract.Collum.TASK_DESCRIPTION,addEditDescription.text.toString())
                    }
                value.put(TaskContract.Collum.TASK_SHORT_ORDER, shortOrder)

            }
            activity?.contentResolver?.insert(TaskContract.CONTENT_URI,value)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG,"onActivityCreated starts")

        if(listener is AppCompatActivity){
            val actionBar = (listener as AppCompatActivity?)?.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
        addEditSubmit.setOnClickListener{
            saveTask()
            listener?.onSaveClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"OnAttach starts")
        if (context is OnSaveClicked){
            listener = context
        } else throw RuntimeException(context.toString() + "must implement OnSaveClicked")
    }

    interface OnSaveClicked{

        fun onSaveClicked()
    }

    override fun onDetach() {
        Log.d(TAG,"onDetach starts")
        super.onDetach()
        listener = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task  class will be changed or new instance create or null.
         * @return A new instance of fragment AddEditFragment.
         */
        @JvmStatic
        fun newInstance(task:Task?) =
            AddEditFragment().apply {
                Log.d(TAG,"newInstance starts")
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
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


}