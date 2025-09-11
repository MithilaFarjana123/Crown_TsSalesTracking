package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.getPickDocWPartResponseChild
import com.crowncement.TsSalesTracking.databinding.OfferitemBinding


class OfferListAdapter(private val offerList: ArrayList<getPickDocWPartResponseChild>) :

    RecyclerView.Adapter<OfferListAdapter.ViewHolder>() {

    var listSize: ArrayList<getPickDocWPartResponseChild> = java.util.ArrayList()

    private val TAG = "OfferListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {

        fun clickAccept(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = OfferitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val offerItem = offerList[position]

        //192.168.2.220:8787/da/docs/x880022/CrownFeat/promdocpdf/brand_20240824.pdf
        val offerDate = offerItem.filePath!!.split("/").last().split(".")[0].split("_")[1]
        val title = offerItem.filePath!!.split("/").last().split(".")[0].split("_")[0]

        showFieldValue(holder.binding.txtOfferTitle, title)
        //OfferDate
        showFieldValue(
            holder.binding.txtOfferDate,
            "Published on: " + Utility.changeDateFormat(offerDate, "yyyyMMdd", "dd MMM yyyy")
        )


        val PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url="
        loadPDF(holder, PDF_VIEWER_URL + EndPoint.IMAGE_BASE_URL + offerItem.filePath)
    }

    private fun showFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    private fun loadPDF(holder: ViewHolder, pdfURL: String) {
        holder.binding.webView.webViewClient = WebViewClient()
        holder.binding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        holder.binding.webView.settings.mixedContentMode =
            WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        CookieManager.getInstance().setAcceptCookie(true)

        // Set web view client.
        holder.binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView?, newProgress: Int) {
                if (newProgress < 100) {

                    holder.binding.progressBar.visibility = View.VISIBLE
                }
                if (newProgress == 100) {
                    if (webView?.contentHeight == 0) {

                        webView.reload()
                    }

                    holder.binding.progressBar.visibility = View.GONE
                }
            }
        }

        val urlString: String = pdfURL

        holder.binding.webView.loadUrl(urlString)

        //holder.itemView.webView.alpha=0.6f

    }



//        class ViewHolder(ItemView: View, listener: OnAdapterItemClickListener) :
//        RecyclerView.ViewHolder(ItemView) {
        class ViewHolder(val binding: OfferitemBinding, listener: OfferListAdapter.OnAdapterItemClickListener) : RecyclerView.ViewHolder(binding.root) {




                init {

                binding.cardLayout.setOnClickListener {
                    listener.clickAccept(itemView, adapterPosition)
            }
        }
    }

}