package com.paysantupipg.utils

import android.app.Activity
import android.content.Intent
import com.paysantupipg.R
import com.paysantupipg.model.ResponseTransactionModel
import com.paysantupipg.rest.ApiResponseInterface
import com.paysantupipg.ui.PaymentActivity
import com.rest.ApiInitialize
import com.rest.ApiRequest
import com.rest.ApiResponseManager
import com.siwfthubupi.listener.PaymentStatusListener
import com.utils.Toast
import com.utils.isNetWork

class StartTransaction {
    var TAG = this.javaClass.simpleName
    var bearer: String = "Bearer "

    fun setTransaction(
        mActivity: Activity,
        key: String,
        merEmail: String,
        merPhoneNo: String,
        merAmount: String,
        merCurrency: String,
        merResponseUrl: String,
        merchantOrderId: String,
        merWebhookUrl: String,
        paymentType: String,
        upiFlow: String,
        listener: PaymentStatusListener

    ) {
        if (isNetWork(mActivity)) {
            ApiRequest(
                mActivity,
                ApiInitialize.initialize(ApiInitialize.MAIN_URL_API)
                    .startTransaction(
                        key = bearer + key,
                        merEmail = merEmail,
                        merPhoneNo = merPhoneNo,
                        merAmount = merAmount,
                        merCurrency = merCurrency,
                        merResponseUrl = merResponseUrl,
                        merchantOrderId = merchantOrderId,
                        merWebhookUrl = merWebhookUrl,
                        paymentType = paymentType,
                        upiFlow = upiFlow,
                    ),
                100,
                true,
                object : ApiResponseInterface {
                    override fun getApiResponse(apiResponseManager: ApiResponseManager<*>) {
                        PaymentActivity.setPaymentStatusListener(listener)
                        when (apiResponseManager.type) {
                            100 -> {
                                val model: ResponseTransactionModel
                                model = apiResponseManager.response as ResponseTransactionModel
                                // Log.e("TAG", "Transaction details :- ${model}")
                                if (model.status == "7") {
                                    try {
                                        val payIntent =
                                            Intent(mActivity, PaymentActivity::class.java).apply {
                                                putExtra(PaymentActivity.EXTRA_KEY_PAYMENT, model)
                                            }
                                        mActivity.startActivity(payIntent)

                                    } catch (e: Exception) {
                                        if (e.message!!.contains("Unable to find explicit activity class")) {
                                            Toast(
                                                "Not any UPI apps are installed",
                                                false,
                                                mActivity
                                            )
                                        }
                                        e.printStackTrace()
                                    }
                                } else {
                                    Toast(model.message, false, mActivity)
                                }
                            }
                        }
                    }

                    override fun onFailure(
                        apiResponseManager: ApiResponseManager<*>,
                        error_message: String,
                        error: Boolean
                    ) {
                        if (!error) {
                            Toast(error_message, false, mActivity)
                        }
                    }
                }
            )
        } else {
            Toast(mActivity.resources.getString(R.string.internet_not_available), false, mActivity)
        }
    }

}