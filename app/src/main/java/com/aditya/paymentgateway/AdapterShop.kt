package com.aditya.paymentgateway

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_shop.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class AdapterShop(private val modelShops: MutableList<ModelShop>) : RecyclerView.Adapter<AdapterShop.Holder>(){

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bindData(modelShop: ModelShop){
            Picasso.get().load(modelShop.imgUrl).into(itemView.imageView)

            val kursRp:DecimalFormat = DecimalFormat.getCurrencyInstance() as DecimalFormat
            val formatRp:DecimalFormatSymbols = DecimalFormatSymbols()
            formatRp.currencySymbol = "Rp. "
            formatRp.monetaryDecimalSeparator = ','
            formatRp.groupingSeparator = '.'
            kursRp.decimalFormatSymbols = formatRp

            itemView.textView.text = modelShop.name
            itemView.textView2.text = kursRp.format(modelShop.price)
            itemView.layout_item_shop.setOnClickListener {
                val intent = Intent(itemView.context,DetailActivity::class.java)
                intent.putExtra(DetailActivity.TAG_DATA_DETAIL_SHOP,modelShop)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    = Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_shop,parent,false))

    override fun getItemCount(): Int =modelShops.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(modelShops[position])
    }
}