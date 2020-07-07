package com.aditya.paymentgateway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class DetailActivity : AppCompatActivity() {

    private lateinit var modelShop: ModelShop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        modelShop = intent.getSerializableExtra(TAG_DATA_DETAIL_SHOP) as ModelShop
    }

    companion object{
        const val TAG_DATA_DETAIL_SHOP = "TAG_DATA_DETAIL_SHOP"
    }
}