package iamzen.`in`.timework

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_durations.*
import java.util.*

private const val TAG = "DurationRVAdapter"
class DurationViewHolder(override val containerView: View):
    RecyclerView.ViewHolder(containerView),
    LayoutContainer

class DurationRVAdapter(context: Context,private var cursor: Cursor?):RecyclerView.Adapter<DurationViewHolder>() {

    private val dateFormat = DateFormat.getDateFormat(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DurationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_durations,parent,false)

        return DurationViewHolder(view)

    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: DurationViewHolder, position: Int) {

        val cursor = cursor
        Log.d(TAG,"onBindViewHolder called")

        if(cursor != null && cursor.count != 0) {
            if(!cursor.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }
            val name = cursor.getString(cursor.getColumnIndex(DurationsContract.Collum.NAME))
            val description = cursor.getString(cursor.getColumnIndex(DurationsContract.Collum.DESCRIPTION))
            val startTime = cursor.getLong(cursor.getColumnIndex(DurationsContract.Collum.START_TIME))
            val totalDuration = cursor.getLong(cursor.getColumnIndex(DurationsContract.Collum.DURATION))

            val userDate = dateFormat.format(startTime * 1000) // database stores seconds, we need milliseconds

            val totalTime = formatDuration(totalDuration)

            holder.td_name.text = name
            holder.td_description?.text = description // Description is not present in portrait
            holder.td_start_date.text = userDate
            holder.td_durations.text = totalTime
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG,"getItemCount called ${cursor?.count ?: 0}")
        return cursor?.count ?: 0
    }

    private fun formatDuration(duration: Long): String {
        // duration is in seconds, convert to hours:minutes:seconds
        // (allowing for >24 hours - so we can't use a time data type);
        val hours = duration / 3600
        val remainder = duration - hours * 3600
        val minutes = remainder / 60
        //        val seconds = remainder - minutes * 60
        val seconds = remainder % 60

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }

        val numItems = itemCount

        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged()
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems)
        }
        return oldCursor
    }



}

