package iamzen.`in`.timework

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment

private const val TAG = "AppDialog"
const val DIALOG_ID = "id"
const val DIALOG_MESSAGE = "message"
const val DIALOG_POSITIVE = "positive_rid"
const val DIALOG_NEGATIVE = "negative_rid"

class AppDialog: AppCompatDialogFragment() {

    private var dialogEvents:DialogEvents? = null

    internal interface DialogEvents{
        fun setOnPositiveRid(dialogId:Int,args: Bundle)
        fun setOnNegativeRid(dialogId:Int,args: Bundle)
//        fun setOnDialogCancelled(dialogId:Int)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG,"OnAttach starts context is $context")
        super.onAttach(context)

        dialogEvents = try {
            parentFragment as DialogEvents
        } catch(e:TypeCastException) {
            try{
                context as DialogEvents

            } catch(e:ClassCastException){
                throw ClassCastException("Activity context $context must be implement AppDialog.DialogEvents")
            }
        }catch(e:ClassCastException){
            throw ClassCastException("Fragment context $context must be implement AppDialog.DialogEvents")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG,"onCreateDialog called")
        val builder = AlertDialog.Builder(context!!)

        val arguments = arguments
        val dialogId:Int
        val dialogMessage:String?
        var dialogPositive:Int
        var dialogNegative:Int

        if(arguments != null){
            dialogId = arguments.getInt(DIALOG_ID)
            dialogMessage = arguments.getString(DIALOG_MESSAGE)
            if (dialogId == 0 || dialogMessage == null){
                throw IllegalArgumentException("Dialog id and or massage not have in bundle")
            }

            dialogPositive = arguments.getInt(DIALOG_POSITIVE)
            if(dialogPositive == 0){
                dialogPositive = R.string.ok
            }
            dialogNegative = arguments.getInt(DIALOG_NEGATIVE)
            if (dialogNegative == 0){
                dialogNegative = R.string.cancel
            }
        } else throw IllegalArgumentException("Must pass Dialog id And message in bundle")


        return builder.setMessage(dialogMessage)
            .setPositiveButton(dialogPositive){dialogInterface,which ->
                    dialogEvents?.setOnPositiveRid(dialogId,arguments)
                }
            .setNegativeButton(dialogNegative){dialogInterface ,which ->
            dialogEvents?.setOnNegativeRid(dialogId,arguments)
        }.create()



    }

    override fun onDetach() {
        Log.d(TAG,"onDetach is called")
        super.onDetach()
        dialogEvents = null
    }
}