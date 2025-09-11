package com.crowncement.TsSalesTracking.UI.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.UI.viewmodel.RetailerViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.RetailerViewModelFactory
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Adapter.OfferListAdapter
import com.crowncement.TsSalesTracking.data.Model.getPickDocWPartResponseChild
import com.crowncement.TsSalesTracking.databinding.FragRunningOfferListBinding
import com.crowncement.TsSalesTracking.databinding.PdfviewerBinding
import android.webkit.CookieManager

class Frag_Running_OfferList : Fragment() {

    private var _binding: FragRunningOfferListBinding? = null
    private val binding get() = _binding!!
   // lateinit var commonViewModel: CommonViewModel

    lateinit var loadingAnim: Dialog

    lateinit var retailerViewModel: RetailerViewModel
    private val PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url="

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragRunningOfferListBinding.inflate(inflater, container, false)


        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )
        RetailerViewModelFactory()
        setupViewModel()

        binding.shimmerOfferContainer.visibility = View.VISIBLE
        binding.shimmerOfferContainer.startShimmer()

        getOfferList("CrownTitan", "promdocpdfc")



    }

        override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    private fun setupViewModel() {

        retailerViewModel =
            ViewModelProviders.of(this.requireActivity(), RetailerViewModelFactory())
                .get(RetailerViewModel::class.java)
    }

    private fun getOfferList(app_proj: String, doctype: String) {

        retailerViewModel.getPickDocWPart(app_proj, doctype)!!.observe(requireActivity()) {
            when (it.status) {

                Status.LOADING -> {

                }

                Status.SUCCESS -> {

                    it.responseData?.let { res ->
                        binding.shimmerOfferContainer.visibility = View.GONE
                        binding.shimmerOfferContainer.stopShimmer()

                        showOfferList(binding.rvOffer, res.data)
                    }
                }

                Status.ERROR -> {
                    binding.shimmerOfferContainer.visibility = View.GONE
                    binding.shimmerOfferContainer.stopShimmer()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun showOfferList(rv: RecyclerView, items: ArrayList<getPickDocWPartResponseChild>) {

        if (items.size > 0) {
            rv.visibility = View.VISIBLE
            binding.txtOfferNoData.visibility = View.GONE

            val adapter = OfferListAdapter(items)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.layoutManager = manager
            rv.adapter = adapter

            adapter.setOnItemClickListener(object :
                OfferListAdapter.OnAdapterItemClickListener {
                override fun clickAccept(v: View?, position: Int) {

                    Log.d("PDF_URL", EndPoint.IMAGE_BASE_URL + items[position].filePath.toString())

                    val title = items[position].filePath!!.split("/").last().split(".")[0].split("_")[0]
                    Log.e("title", title )
                    showPDF(EndPoint.IMAGE_BASE_URL + items[position].filePath.toString(),title)
                }

            })

        } else {
            rv.visibility = View.GONE
            binding.txtOfferNoData.visibility = View.VISIBLE
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun showPDF(pdfURL: String,title:String) {


        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        val bind: PdfviewerBinding = PdfviewerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bind.getRoot())
      //  val v: View = layoutInflater.inflate(R.layout.pdf_viewer, null)
        // val dialog = Dialog(v.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
     //   val dialog = Dialog(v.context, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
       ////// val imageButton: ImageView = dialog.findViewById(R.id.imageButton)
      //  val webView: WebView = dialog.findViewById(R.id.webView)

      //  val txtPromotionalTitle: TextView = dialog.findViewById(R.id.txtTitle)
        bind.txtTitle.text =title

        loadingAnim = Utility.baseLoadingAnimation(
            requireActivity(),
            Dialog(requireActivity()),
            "P l e a s e    w a i t"
        )

        bind.webView.webViewClient = WebViewClient()
        bind.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        bind.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        bind.webView.settings.useWideViewPort = true
        bind.webView.requestFocusFromTouch()
        CookieManager.getInstance().setAcceptCookie(true)

        dialog.setContentView(bind.root)
        dialog.setCancelable(true)
        dialog.show()


        val settings = bind.webView.settings

        // Enable java script in web view.
        settings.javaScriptEnabled = true

        // Enable DOM storage API.
        settings.domStorageEnabled = true

        // Enable zooming in web view.
        settings.setSupportZoom(true)

        // Allow pinch to zoom.
        settings.builtInZoomControls = true

        // Disable the default zoom controls on the page.
        settings.displayZoomControls = true

        // Enable responsive layout.
        settings.useWideViewPort = true

        // Zoom out if the content width is greater than the width of the viewport.
        settings.loadWithOverviewMode = false


        // Set web view client.
        bind.webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(webView: WebView?, newProgress: Int) {
                if (newProgress < 100) {

                    loadingAnim.show()
                }
                if (newProgress == 100) {

                    // There is a bug with Google Docs that sometimes you get blank screen
                    // instead of a PDF file. To avoid just reload when you get it.
                    if (webView?.contentHeight == 0) {
                        Toast.makeText(activity, "Reloading...", Toast.LENGTH_SHORT).show()
                        webView.reload()
                    }

                    loadingAnim.dismiss()
                }
            }
        }

        val urlString: String =  PDF_VIEWER_URL+pdfURL

        bind.webView.loadUrl(urlString)

        bind.imageButton.setOnClickListener {

            dialog.dismiss()

        }

    }
}