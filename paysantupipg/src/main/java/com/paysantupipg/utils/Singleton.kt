package com.paysantupipg.utils

import com.siwfthubupi.listener.PaymentStatusListener


internal object Singleton {
	@set:JvmSynthetic
	@get:JvmSynthetic
	internal var listener: PaymentStatusListener? = null
}