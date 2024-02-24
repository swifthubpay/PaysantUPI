package com.siwfthubupi.listener

import com.paysantupipg.model.ResponseTransactionModel
import com.paysantupipg.model.TransactionDetails


interface PaymentStatusListener {
	fun onTransactionDetails(transactionDetails: TransactionDetails)
	fun onTransactionCancelled()
}