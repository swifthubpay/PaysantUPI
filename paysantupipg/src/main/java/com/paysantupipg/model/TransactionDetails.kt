package com.paysantupipg.model

data class TransactionDetails(
	val transactionStatus: String?,
	val amount: String?,
	val merchantOrderId: String?,
	val orderId: String
)

enum class TransactionStatus {
	FAILURE, SUCCESS, SUBMITTED
}