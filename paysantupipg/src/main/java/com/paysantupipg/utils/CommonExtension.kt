package com.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialog
import com.paysantupipg.utils.ProgressDialog

private var toast: Toast? = null

@SuppressLint("ShowToast")
fun Toast(msg: Any?, isShort: Boolean = true, app: Context) {
    msg?.let {
        if (toast == null) {
            toast = Toast.makeText(app, msg.toString(), Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg.toString())
        }
        toast!!.duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        toast!!.show()
    }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun getProgressDialog(context: Context): AppCompatDialog {

    val isCalncelable = false
    val myCustomProgressDialog = ProgressDialog(context)
    myCustomProgressDialog.setCancelable(isCalncelable)
    myCustomProgressDialog.show()
    return myCustomProgressDialog

}

fun dismissDialog(context: Context, mProgressDialog: AppCompatDialog) {
    try {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    } catch (e: Exception) {
    }

}

fun isNetWork(context: Context): Boolean {
    val flag: Boolean = isNetworkAvailable(context)
    return flag
}

fun isNetworkAvailable(context: Context): Boolean {

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null
}







