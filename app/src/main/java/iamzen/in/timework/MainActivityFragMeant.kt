package iamzen.`in`.timework

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main_acitivity_frag_ment.*
import kotlinx.coroutines.DelicateCoroutinesApi

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [@Fragment] subclass.
 * Use the [@MainActivityFragMeant.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val TAG = "MainActivityFragment"
private const val DIALOG_ID_DELETED = 1
private const val DIALOG_TASK_ID = "task_id"
private const val DIALOG_TASK_POSITION = "task_position"

@DelicateCoroutinesApi
class MainActivityFragMeant : Fragment(),
    CursorRecyclerViewAdapter.WorkingButton,
    AppDialog.DialogEvents {

    @DelicateCoroutinesApi
//    private val mViewModel by lazy{ ViewModelProvider(this).get(TimeWorkViewModel::class.java)}
    private val mViewModel: TimeWorkViewModel by activityViewModels()
    private val mAdapter = CursorRecyclerViewAdapter(null, this)
//    var mCheckTimingStart = true

//    var count = 0
//    private var timer: CountDownTimer = object : CountDownTimer(20000, 1000) {
//        @SuppressLint("StringFormatMatches", "StringFormatInvalid")
//        override fun onTick(millisUntilFinished: Long) {
//            mCheckTimingStart = false
//            Log.d(TAG, "timing start ${millisUntilFinished / 1000} $mCheckTimingStart")
//            count = (millisUntilFinished / 1000).toInt()
//            DisplayTitle.text = getString(R.string.TimingRecordShow, count)
//
//        }
//
//
//        @SuppressLint("StringFormatInvalid")
//        override fun onFinish() {
//            Log.d(TAG, "timing end")
//            DisplayTitle.text = getString(R.string.MainTitle)
//            startTiming.setImageResource(R.drawable.ic_baseline_not_started_24)
//        }
//    } // countDown end bracelets


    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreated starts")
        mViewModel.cursor.observe(this, { cursor -> mAdapter.swapCursor(cursor)?.close() })
        mViewModel.timing.observe(this, { timing ->
             if (timing != null) {
                DisplayTitle.text = getString(R.string.TimingRecordShow,timing)
            } else {
                 DisplayTitle.text = getString(R.string.MainTitle)
            }
        })


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView starts")
        return inflater.inflate(R.layout.fragment_main_acitivity_frag_ment, container, false)

    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: called")
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "listOfItem starts")

        List_Of_Item.layoutManager = LinearLayoutManager(context)
        List_Of_Item.adapter = mAdapter

        // swipe delete is created in this app
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    Log.d(TAG, "ItemTouchHelper onMove is called")
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    Log.d(TAG, "ItemTouchHelper onSwipe is called")
                    if (direction == ItemTouchHelper.LEFT) {
                        val task = (viewHolder as TaskViewHolder).task
                        if (task.id == mViewModel.editTaskId) {
                            mAdapter.notifyItemChanged(viewHolder.adapterPosition)
                            Toast.makeText(
                                context,
                                "You can`t not deleted task task currently edited.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            deleteTask(task, viewHolder.adapterPosition)
                        }
                    }
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(List_Of_Item)

    }

    interface ManageWorkingButton {
        fun editTaskButton(task: Task)

    }

    // working Button interface is implement
    override fun editTask(task: Task) {
        (activity as ManageWorkingButton?)?.editTaskButton(task)
    }

    fun deleteTask(task: Task, position: Int) {
        Log.d(TAG, "deleteTask called ")

        val args = Bundle().apply {
            putInt(DIALOG_ID, DIALOG_ID_DELETED)
            putString(
                DIALOG_MESSAGE,
                getString(R.string.dialog_message_delete, task.id, task.Name)
            )
            putInt(DIALOG_POSITIVE, R.string.delete_dialog)
            putLong(
                DIALOG_TASK_ID,
                task.id
            ) // pass  the id in the arguments .so we can retrieve id than what task callback.
            putInt(DIALOG_TASK_POSITION, position)
        }

        val dialog = AppDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, null)

    }

    override fun startTimingTab(task: Task) {
        Log.d(TAG, "longClick called")
        mViewModel.timingTask(task)
    }


    override fun setOnPositiveRid(dialogId: Int, args: Bundle) {
        Log.d(TAG, "setOnPositiveRid called dialog id is $dialogId")

        if (dialogId == DIALOG_ID_DELETED) {
            val taskId = args.getLong(DIALOG_TASK_ID)
            if (BuildConfig.DEBUG && taskId == 0L) throw AssertionError("task id is 0")
            mViewModel.deleteTask(taskId)
        }
    }

    override fun setOnNegativeRid(dialogId: Int, args: Bundle) {
        Log.d(TAG, "setOnNegativeRid is called")
        if (dialogId == DIALOG_ID_DELETED) {
            val position = args.getInt(DIALOG_TASK_POSITION)
            Log.d(TAG, "item is  not deleted position is $position ")
            mAdapter.notifyItemChanged(position)
        }
    }

    //TODO: Deleting function before releasing app.

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
}

