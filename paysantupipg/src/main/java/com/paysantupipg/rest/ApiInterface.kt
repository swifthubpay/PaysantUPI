package com.paysantupipg.rest
import com.paysantupipg.model.ResponseTransactionModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiInterface {

    @POST("seamless/transaction")
    @FormUrlEncoded
    fun startTransaction(
    @Header("Authorization")key:String,
        @Field("merEmail") merEmail: String,
        @Field("merPhoneNo") merPhoneNo: String,
        @Field("merAmount") merAmount: String,
        @Field("merCurrency") merCurrency: String,
        @Field("merResponseUrl") merResponseUrl: String,
        @Field("merchant_order_id") merchantOrderId: String,
        @Field("merWebhookUrl") merWebhookUrl: String,
        @Field("payment_type") paymentType: String,
        @Field("upi_flow") upiFlow: String,

    ): Call<ResponseTransactionModel>


}