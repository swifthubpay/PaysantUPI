package com.paysantupipg.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseTransactionModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
): Serializable {
    data class Data(
        @SerializedName("amount")
        val amount: String,
        @SerializedName("currency")
        val currency: String,
        @SerializedName("intentUrl")
        val intentUrl: String,
        @SerializedName("merchant_order_id")
        val merchantOrderId: String,
        @SerializedName("order_id")
        val orderId: String
    ): Serializable
}