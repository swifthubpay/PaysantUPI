package com.paysantupipg.ui

import AppNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.paysantupipg.R
import com.paysantupipg.model.ResponseTransactionModel
import com.paysantupipg.model.TransactionDetails
import com.paysantupipg.utils.Singleton
import com.siwfthubupi.listener.PaymentStatusListener
import com.utils.Toast

class PaymentActivity : AppCompatActivity() {
    private lateinit var payment: ResponseTransactionModel

    companion object {
        const val TAG = "StartTransaction"
        const val PAYMENT_REQUEST = 4400
        const val EXTRA_KEY_PAYMENT = "payment"
        fun setPaymentStatusListener(mListener: PaymentStatusListener) {
            Singleton.listener = mListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        initUPI()

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val response = data.getStringExtra("response")
                    // Log.d(TAG, "UPI response--->" + response)
                    if (response == null) {
                        callbackTransactionCancelled()
                    } else {
                        val transactionDetails = getTransactionDetails(response)
                        callbackTransactionDetails(transactionDetails)
                    }
                }
            } else {
                // Log.e(TAG, "Intent Data is null. User cancelled")
                callbackTransactionCancelled()
            }
            finish()

        }

    }


    fun initUPI() {
        payment = (intent.getSerializableExtra(EXTRA_KEY_PAYMENT) as ResponseTransactionModel?)
            ?: throw IllegalStateException("Unable to parse payment details")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse(payment.data.intentUrl)
        val chooser = Intent.createChooser(intent, "Pay using")
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(chooser, PAYMENT_REQUEST)
        } else {
            Toast("Not any UPI apps are installed", false, this@PaymentActivity)
            throwOnAppNotFound()
        }
    }

    @JvmSynthetic
    internal fun callbackTransactionCancelled() {
        Singleton.listener?.onTransactionCancelled()
    }

    @JvmSynthetic
    internal fun callbackTransactionDetails(transactionDetails: TransactionDetails) {
        Singleton.listener?.onTransactionDetails(transactionDetails)
    }

    @JvmSynthetic
    internal fun throwOnAppNotFound() {
        Log.e(TAG, "No UPI app found on device.")
        throw AppNotFoundException("Not founded--?")
    }

    @JvmSynthetic
    internal fun getTransactionDetails(response: String): TransactionDetails {
        return with(getMapFromQuery(response)) {
            TransactionDetails(
                amount = payment.data.amount,
                merchantOrderId = payment.data.merchantOrderId,
                orderId = payment.data.orderId,
                transactionStatus = get("Status")
            )

        }
    }

    @JvmSynthetic
    internal fun getMapFromQuery(queryString: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val keyValuePairs = queryString
            .split("&")
            .map { param ->
                param.split("=").let { Pair(it[0], it[1]) }
            }
        map.putAll(keyValuePairs)
        return map
    }

}