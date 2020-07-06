package com.aditya.paymentgateway

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.midtrans.sdk.corekit.callback.CardRegistrationCallback
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.PaymentMethod
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BankType
import com.midtrans.sdk.corekit.models.CardRegistrationResponse
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.CreditCard
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder


class MainActivity : AppCompatActivity(),TransactionFinishedCallback{
    private val TAG = "TAG_MAIN_ACTIVITY"
    private lateinit var buttonUiKit: Button
    private  lateinit var buttonDirectCreditCard:android.widget.Button
    private  lateinit var buttonDirectBcaVa:android.widget.Button
    private  lateinit var buttonDirectMandiriVa:android.widget.Button
    private lateinit var buttonDirectBniVa: Button
    private  lateinit var buttonDirectAtmBersamaVa:android.widget.Button
    private  lateinit var buttonDirectPermataVa:android.widget.Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMidtransSdk()
        bindViews()
        initActionButtons()
    }
    private fun initTransactionRequest(): TransactionRequest? {
        // Create new Transaction Request
        val transactionRequestNew =
            TransactionRequest(System.currentTimeMillis().toString() + "", 2000.0)

        //set customer details
        transactionRequestNew.setCustomerDetails(initCustomerDetails()!!)


        // set item details
        val itemDetails =
            ItemDetails("1", 2000.0, 1, "Trekking Shoes")

        // Add item details into item detail list.
        val itemDetailsArrayList: ArrayList<ItemDetails> =
            ArrayList()
        itemDetailsArrayList.add(itemDetails)
        transactionRequestNew.itemDetails = itemDetailsArrayList


        // Create creditcard options for payment
        val creditCard = CreditCard()
        creditCard.isSaveCard =
            false // when using one/two click set to true and if normal set to  false

//        this methode deprecated use setAuthentication instead
//        creditCard.setSecure(true); // when using one click must be true, for normal and two click (optional)
        creditCard.authentication = CreditCard.AUTHENTICATION_TYPE_3DS

        // noted !! : channel migs is needed if bank type is BCA, BRI or MyBank
//        creditCard.setChannel(CreditCard.MIGS); //set channel migs
        creditCard.bank = BankType.BCA //set spesific acquiring bank
        transactionRequestNew.creditCard = creditCard
        return transactionRequestNew
    }

    private fun initCustomerDetails(): CustomerDetails? {

        //define customer detail (mandatory for coreflow)
        val mCustomerDetails =
            CustomerDetails()
        mCustomerDetails.phone = "085310102020"
        mCustomerDetails.firstName = "user fullname"
        mCustomerDetails.email = "mail@mail.com"
        return mCustomerDetails
    }

    private fun initMidtransSdk() {
        val client_key: String = BuildConfig.CLIENT_KEY_MID
        val base_url = BuildConfig.BASE_URL
        SdkUIFlowBuilder.init()
            .setClientKey(client_key) // client_key is mandatory
            .setContext(this) // context is mandatory
            .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl(base_url) //set merchant url
            .enableLog(true) // enable sdk log
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // will replace theme on snap theme on MAP
            .buildSDK()
    }

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> Toast.makeText(
                    this,
                    "Transaction Finished. ID: " + result.response.transactionId,
                    Toast.LENGTH_LONG
                ).show()
                TransactionResult.STATUS_PENDING -> Toast.makeText(
                    this,
                    "Transaction Pending. ID: " + result.response.transactionId,
                    Toast.LENGTH_LONG
                ).show()
                TransactionResult.STATUS_FAILED -> Toast.makeText(
                    this,
                    "Transaction Failed. ID: " + result.response
                        .transactionId + ". Message: " + result.response
                        .statusMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
            result.response.validationMessages
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun bindViews() {
        buttonUiKit = findViewById(R.id.button_start_ui_kit)
        buttonDirectCreditCard = findViewById(R.id.button_direct_credit_card)
        buttonDirectBcaVa = findViewById(R.id.button_direct_bca_va)
        buttonDirectMandiriVa = findViewById(R.id.button_direct_mandiri_va)
        buttonDirectBniVa = findViewById(R.id.button_direct_bni_va)
        buttonDirectPermataVa = findViewById(R.id.button_direct_permata_va)
        buttonDirectAtmBersamaVa = findViewById(R.id.button_direct_atm_bersama_va)
    }

    private fun initActionButtons() {
        buttonUiKit.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this@MainActivity)
        }
        buttonDirectCreditCard.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance()
                .UiCardRegistration(this@MainActivity, object : CardRegistrationCallback {
                    override fun onSuccess(cardRegistrationResponse: CardRegistrationResponse) {
                        Toast.makeText(
                            this@MainActivity,
                            "register card token success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(
                        cardRegistrationResponse: CardRegistrationResponse,
                        s: String
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            "register card token Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(throwable: Throwable) {}
                })
        }
        buttonDirectBcaVa.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance()
                .startPaymentUiFlow(this@MainActivity, PaymentMethod.BANK_TRANSFER_BCA)
        }
        buttonDirectBniVa.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance()
                .startPaymentUiFlow(this@MainActivity, PaymentMethod.BANK_TRANSFER_BNI)
        }
        buttonDirectMandiriVa.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance()
                .startPaymentUiFlow(this@MainActivity, PaymentMethod.BANK_TRANSFER_MANDIRI)
        }
        buttonDirectPermataVa.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance()
                .startPaymentUiFlow(this@MainActivity, PaymentMethod.BANK_TRANSFER_PERMATA)
        }
        buttonDirectAtmBersamaVa.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
                MidtransSDK.getInstance()
                    .startPaymentUiFlow(this@MainActivity, PaymentMethod.BCA_KLIKPAY)
            }
        })
    }
}