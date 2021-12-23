package iamzen.`in`.timework

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentActivity.findFragmentById(id:Int): Fragment?{
    return supportFragmentManager.findFragmentById(id)
}

fun FragmentActivity.showConfirmationDialog(id: Int,
                                            message:String,
                                            positive:Int = R.string.ok,
                                            negative:Int = R.string.cancel){

    val args = Bundle().apply{
        putInt(DIALOG_ID,id)
        putString(DIALOG_MESSAGE,message)
        putInt(DIALOG_POSITIVE,positive)
        putInt(DIALOG_NEGATIVE,negative)
    }

    val dialog = AppDialog()
    dialog.arguments = args
    dialog.show(this.supportFragmentManager,null)

}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction){
    beginTransaction().func().commit()
}

fun FragmentActivity.addFragment(fragment:Fragment, frameId:Int){
    supportFragmentManager.inTransaction{add(frameId,fragment)}
}

fun FragmentActivity.replaceFragment(fragment:Fragment, frameId:Int){
    supportFragmentManager.inTransaction{replace(frameId,fragment)}
}

fun FragmentActivity.removeFragment(fragment:Fragment){
    supportFragmentManager.inTransaction{remove(fragment)}
}
