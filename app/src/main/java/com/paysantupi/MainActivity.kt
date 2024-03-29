package com.paysantupi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.paysantupi.databinding.ActivityMainBinding
import com.paysantupipg.model.ResponseTransactionModel
import com.paysantupipg.model.TransactionDetails
import com.paysantupipg.utils.StartTransaction
import com.siwfthubupi.listener.PaymentStatusListener

class MainActivity : AppCompatActivity() {
    var TAG = this.javaClass.simpleName

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonPay.setOnClickListener {
            try {
                StartTransaction().setTransaction(
                    this, key = "1|3wlYVqRDyeRm60VgqSeeQy3wWRjCPZont04kSBnr079c5c66",
                    merEmail = "abc@gmail.com",
                    merPhoneNo = "9489488499",
                    merAmount = "1",
                    merCurrency = "INR",
                    merResponseUrl = "https://google.com",
                    merchantOrderId = "abcbdj",
                    merWebhookUrl = "https://google.com",
                    paymentType = "upi",
                    upiFlow = "Intent",object : PaymentStatusListener{
                        override fun onTransactionDetails(transactionDetails: TransactionDetails) {
                        }

                        override fun onTransactionCancelled() {

                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


}