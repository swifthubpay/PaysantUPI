package com.rest

import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialog
import com.paysantupipg.R
import com.paysantupipg.rest.ApiResponseInterface
import com.utils.Toast
import com.utils.dismissDialog
import com.utils.getProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.text.ParseException
import java.util.concurrent.TimeoutException

@RequiresApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("ParcelCreator")
class ApiRequest<T>(private val activity: Activity,
                    objectType: T,
                    private val TYPE: Int,
                    private val isShowProgressDialog: Boolean,
                    private val apiResponseInterface: ApiResponseInterface
) : Callback<T>,
    Parcelable {

    private var mProgressDialog: AppCompatDialog? = null
    private var retryCount = 0
    private var call: Call<T>? = null

    init {
        showProgress()
        call = objectType as Call<T>
        call!!.enqueue(this)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showProgress() {
        if (isShowProgressDialog) {
            mProgressDialog = getProgressDialog(activity)
        }
    }

    private fun dismissProgress() {
        if (isShowProgressDialog) {
            dismissDialog(activity, mProgressDialog!!)
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        dismissProgress()
       //Log.e(TAG, "URL = " + call.request().url().toString())
        if (response.isSuccessful) {

           // Log.e(TAG, "RESPONSE =" + response.body())
            apiResponseInterface.getApiResponse(ApiResponseManager(response.body(), TYPE))

        } else {


            val error = ErrorUtils.parseError(response)
            //Log.e(TAG, "ERROR STATUS = " + error.status().toString() + " \n ERROR MESSAGE =" + error.message() + " \n ERROR CODE =" + error.status_code.toString())

            if (error.status() == 401) {

                /*SessionManager.instance.logout()
                val intent = Intent(activity, Act_Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
                activity.finishAffinity()*/

                when {
                    response.message() != "" -> Toast(response.message(), false, activity)
                    error.message() != "" -> Toast(response.message(), false, activity)
                    else -> Toast(activity.resources.getString(R.string.toast_something_wrong), false, activity)
                }

            } else {
                apiResponseInterface.onFailure(ApiResponseManager(error.message(), TYPE), error.message(), false)
            }

        }
    }

    override fun onFailure(call: Call<T>, error: Throwable) {

        error.printStackTrace()
       // Log.e(TAG, "ERROR = " + error.message!!)

        if (retryCount++ < TOTAL_RETRIES) {
           // Log.v(TAG, "RETRYING... ($retryCount OUT OF $TOTAL_RETRIES)")
            retry()
            return
        }
        dismissProgress()
//        Log.e(TAG,"else0" + error)
        when (error) {

            is NetworkErrorException -> {
                apiResponseInterface.onFailure(ApiResponseManager(error.message, TYPE),
                    activity.resources.getString(R.string.toast_time_out), true)
                Toast(activity.resources.getString(R.string.toast_time_out), false, activity)
            }
            is TimeoutException -> {
                apiResponseInterface.onFailure(ApiResponseManager(error.message, TYPE),
                    activity.resources.getString(R.string.toast_time_out), true)
                Toast(activity.resources.getString(R.string.toast_time_out), false, activity)
            }
            is SocketTimeoutException -> {
                apiResponseInterface.onFailure(ApiResponseManager(error.message, TYPE),
                    activity.resources.getString(R.string.toast_time_out), true)
                Toast(activity.resources.getString(R.string.toast_try_after_sometimes), false, activity)
            }
            is ParseException -> {
                apiResponseInterface.onFailure(ApiResponseManager(error.message, TYPE),
                    activity.resources.getString(R.string.toast_time_out), true)
                Toast(activity.resources.getString(R.string.toast_something_wrong), false, activity)
            }
            else -> {
               // Log.e(TAG, "else")
                apiResponseInterface.onFailure(ApiResponseManager(error.message, TYPE),
                    activity.resources.getString(R.string.toast_something_wrong), true)
                Toast(activity.resources.getString(R.string.toast_something_wrong), false, activity)
            }
        }

    }

    private fun retry() {
        call!!.clone().enqueue(this)
    }

    companion object {
        private val TAG = "REQUEST_RESPONSE"
        private val TOTAL_RETRIES = 3
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(TYPE)
        parcel.writeByte(if (isShowProgressDialog) 1 else 0)
        parcel.writeInt(retryCount)
    }

    override fun describeContents(): Int {
        return 0
    }
}