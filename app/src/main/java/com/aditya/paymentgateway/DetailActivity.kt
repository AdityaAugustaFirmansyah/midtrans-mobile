package com.aditya.paymentgateway

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
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

        modelShop = intent.getSerializableExtra(TAG_DATA_DETAIL_SHOP) as ModelShop
        bindData()
    }

    @SuppressLint("CheckResult", "ResourceAsColor")
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
                dialog.title(text = "Masukan Jumlahnya")
                input (inputType = InputType.TYPE_CLASS_NUMBER)
                positiveButton {
                    initSdk()
                    MidtransSDK.getInstance().transactionRequest = initTransactionRequest(modelShop.price.toDouble(),dialog.getInputField().text.toString(),modelShop.name)
                    MidtransSDK.getInstance().startPaymentUiFlow(this@DetailActivity)
                }
                negativeButton {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun initSdk(){
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.CLIENT_KEY_MID)
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl(BuildConfig.BASE_URL)
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

    override fun onTransactionFinished(p0: TransactionResult) {
        when(p0.status){
            TransactionResult.STATUS_SUCCESS->{
                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
            }

            TransactionResult.STATUS_FAILED->{
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }

            TransactionResult.STATUS_PENDING->{
                Toast.makeText(this,"Pending",Toast.LENGTH_SHORT).show()
            }
        }
        if (p0.isTransactionCanceled){
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()
        }else{
            if (p0.status == TransactionResult.STATUS_INVALID){
                Toast.makeText(this,"Invalid",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Transaction Finished With Failure",Toast.LENGTH_SHORT).show()
            }
        }
    }
}