package iamzen.`in`.timework

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_of_item.*

private const val TAG = "CursorRecyclerViewAdt"

class TaskViewHolder( override val containerView: View) : RecyclerView.ViewHolder(containerView) ,
     LayoutContainer{

}


class CursorRecyclerViewAdapter(private var cursor: Cursor?) :
    RecyclerView.Adapter<TaskViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG ,"onCreateViewHolder starts")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_item, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(TAG ,"onBindViewHolder starts")

        val cursor = cursor
        if (cursor == null || cursor.count == 0) {
            Log.d(TAG, "cursor is a null or 0 ")
            holder.titleOfTask.setText( R.string.fristTimeRun)
            holder.DescriptionOfTask.setText(R.string.fristTimeDescription)
            holder.editTaskButton.visibility = View.GONE
            holder.deleteTaskButton.visibility = View.GONE
        } else {
            Log.d(TAG,"else block call")
            if (!cursor.moveToPosition(position)) {
                Log.d(TAG ,"illegalStateException called")
                throw IllegalStateException("cursor is not a move next position $position ")
            }

            val task = Task(
                cursor.getString(cursor.getColumnIndex(TaskContract.Collum.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TaskContract.Collum.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TaskContract.Collum.TASK_SHORT_ORDER))

            )

            Log.d(TAG,"task created")
            task.id = cursor.getLong(cursor.getColumnIndex(TaskContract.Collum.TASK_ID))

            holder.titleOfTask.text = task.Name
            holder.DescriptionOfTask.text = task.Description
            holder.editTaskButton.visibility = View.VISIBLE // Todo: add A onClick
            holder.deleteTaskButton.visibility = View.VISIBLE // Todo: add A onClick
            

        }
    }


    override fun getItemCount(): Int {
        Log.d(TAG,"getItemCount starts")
        val cursor = cursor
        Log.d(TAG,cursor.toString())

        val count = if (cursor == null || cursor.count == 0) {
            1
        } else{
            cursor.count
        }
        Log.d(TAG, "returning count $count ")
        return count
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapCursor(newCursor: Cursor?): Cursor? {
        Log.d(TAG,"swap Cursor called")
        if (newCursor === cursor) {
            Log.d(TAG,"newCursor is a null")
            return null
        }

        val numItem = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if(newCursor != null){
            Log.d(TAG,"newCursor not a null $newCursor")
            notifyDataSetChanged()
        }else{
            notifyItemRangeRemoved(0,numItem)
        }
        return oldCursor
    }
}