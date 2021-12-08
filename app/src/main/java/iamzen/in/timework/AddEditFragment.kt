package iamzen.`in`.timework

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class AddEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var task: Task? = null
    private var listener:OnSaveClicked? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = arguments?.getParcelable(ARG_TASK)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addEditSubmit.setOnClickListener{
            listener?.onSaveClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSaveClicked){
            listener = context
        } else throw RuntimeException(context.toString() + "must implement OnSaveClicked")
    }

    interface OnSaveClicked{
        fun onSaveClicked()
    }

    override fun onDetach() {

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
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }
}