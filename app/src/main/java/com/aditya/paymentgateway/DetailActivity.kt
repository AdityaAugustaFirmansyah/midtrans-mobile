package com.aditya.paymentgateway

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DetailActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var modelShop: ModelShop
    private lateinit var dialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initSdk()
        modelShop = intent.getSerializableExtra(TAG_DATA_DETAIL_SHOP) as ModelShop
        bindData()
    }

    @SuppressLint("CheckResult")
    private fun bindData(){
        val kursRp: DecimalFormat = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols()
        formatRp.currencySymbol = "Rp. "
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'
        kursRp.decimalFormatSymbols = formatRp

        dialog = MaterialDialog(this)

        tv_titlle_detail.text = modelShop.name
        tv_desc_detail.text = modelShop.description
        tv_price_detail.text = kursRp.format(modelShop.price)
        Picasso.get().load(modelShop.imgUrl).into(img_detail_product)

        button_pay_detail.setOnClickListener {
            dialog.show {
                input (inputType = InputType.TYPE_CLASS_NUMBER){ _, charSequence ->
//                    MidtransSDK.getInstance().transactionRequest = initTransactionRequest(modelShop.price.toDouble(),charSequence.toString(),modelShop.name)
//                    MidtransSDK.getInstance().startPaymentUiFlow(this@DetailActivity)
                }
            }
        }
    }

    private fun initSdk(){
        SdkUIFlowBuilder.init()

            .setContext(this)
            .setTransactionFinishedCallback(this)

            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255","#B61548","#FFE51255"))
            .buildSDK()
    }

    private fun setUserDetail():CustomerDetails{
        val userDetail = CustomerDetails()
        userDetail.firstName = "Aditya"
        userDetail.email = "aditya.augusta5@gmail.com"
        userDetail.phone = "020842042"
        return userDetail
    }

    private fun initTransactionRequest(double: Double,qty:String,nameProduct:String):TransactionRequest{
        val transactionRequest = TransactionRequest("${System.currentTimeMillis()} ",double)
        transactionRequest.customerDetails = setUserDetail()

        val itemDetails = ItemDetails(qty,double,qty.toInt(),nameProduct)
        transactionRequest.itemDetails = arrayListOf(itemDetails)
        return transactionRequest
    }

    companion object{
        const val TAG_DATA_DETAIL_SHOP = "TAG_DATA_DETAIL_SHOP"
    }

    override fun onTransactionFinished(p0: TransactionResult?) {
        TODO("Not yet implemented")
    }
}