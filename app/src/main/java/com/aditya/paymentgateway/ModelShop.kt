package com.aditya.paymentgateway

import com.midtrans.sdk.corekit.models.CustomerDetails
import java.io.Serializable

data class ModelShop(
     val name:String,
     val price: Int,
     val description:String,
     val imgUrl:String
):Serializable