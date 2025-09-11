package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.GetTSCallTagResponceChild
import com.crowncement.TsSalesTracking.data.Model.SalesVisitSummary
import com.crowncement.TsSalesTracking.databinding.QuickvisitChildBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class SalesVisitSummaryAdapter(
    private val lateAppliedList: ArrayList<GetTSCallTagResponceChild>,
    val manager: FragmentManager
) : RecyclerView.Adapter<SalesVisitSummaryAdapter.ViewHolder>() {
    //, OnMapReadyCallback
    var listSize: ArrayList<SalesVisitSummary> = java.util.ArrayList()

    private val TAG = "SalesVisitSummaryAdapter"
    lateinit var currentLocation: LatLng

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context
    lateinit var gMap: GoogleMap
    lateinit var mapFragment: SupportMapFragment

    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = QuickvisitChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context
        val cou = position + 1
        holder.binding.txtCount.text = cou.toString()+"/"+lateAppliedList.size

        val info: GetTSCallTagResponceChild = lateAppliedList[position]


        holder.binding.txtCusName.text = info.siteAdd

        holder.binding.txtParty.text = info.partyType+" Name: "+  info.partyName



        holder.binding.txtCustomerPhone.text = info.partyMobile

        val visDate: String = Utility.changeDateFormat(
            info.partyMoveTime,
            "yyyy-MM-dd HH:mm:ss",
            "EEE, dd MMM yyyy (hh:mm a)"
        ).toString()

        holder.binding.txtVisitDate.text = visDate

        if (info.moveStat!!.isNotEmpty()) {

            holder.binding.imgCusStatus.setBackgroundResource(com.crowncement.TsSalesTracking.R.drawable.ic_checked_green)

        } else {
            holder.binding.imgCusStatus.setBackgroundResource(com.crowncement.TsSalesTracking.R.drawable.ic_baseline_error_orange)
        }

//         mapFragment = manager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync { googleMap: GoogleMap ->
//
//            MapsInitializer.initialize(mContext.applicationContext)
//            gMap = googleMap
//            loadMap(info.lat!!.toDouble(), info.lon!!.toDouble(), info.CustomerName.toString())
//        }

        val ai: ApplicationInfo = mContext.packageManager.getApplicationInfo(
            mContext.packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()


        if (info.siteImage.toString() != "") {
            Glide
                .with(mContext)
                .load(EndPoint.IMAGE_BASE_URL + info.siteImage)
                .error(R.drawable.login_cover)
                // .centerCrop()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.login_cover)
                .into(holder.binding.imgSiteImage)
        }














//        Glide
//            .with(mContext)
//            .load(
//                "http://maps.google.com/maps/api/staticmap?center=" +
//                        info.lat +
//                        "," +
//                        info.lon +
//                        "&zoom=15&size=200x200&sensor=false" +"&key=$apiKey"+
//                        "&markers=color:red%7Clabel:C%" +
//                        info.lat +
//                        "," +
//                        info.lon
//            )
//            .centerCrop()
//            .into(holder.itemView.map)
    }


    private fun prepareLocation(lat: Double, lon: Double, title: String) {
        val ai: ApplicationInfo = mContext.packageManager.getApplicationInfo(
            mContext.packageName, PackageManager.GET_META_DATA
        )
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        loadMap(lat, lon, title)

    }

    override fun getItemCount(): Int {
        return lateAppliedList.size
    }

    class ViewHolder(val binding: QuickvisitChildBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }

//    override fun onMapReady(p0: GoogleMap) {
//        gMap = p0
//    }

//    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();
//
//    for(int i = 0 ; i < markersArray.size() ; i++) {
//
//        createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLongitude(), markersArray.get(i).getTitle(), markersArray.get(i).getSnippet(), markersArray.get(i).getIconResID());
//    }
//
//    fun   createMarker: Marker(double latitude, double longitude, String title, String snippet, int iconResID) {
//
//        return googleMap.addMarker(new MarkerOptions()
//            .position(new LatLng(latitude, longitude))
//            .anchor(0.5f, 0.5f)
//            .title(title)
//            .snippet(snippet)
//            .icon(BitmapDescriptorFactory.fromResource(iconResID)));
//    }

    private fun loadMap(lat: Double, lon: Double, title: String) {

        currentLocation = LatLng(lat, lon)
        gMap.clear()
        gMap.addMarker(
            MarkerOptions().position(currentLocation).title(title)
        )
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
    }

//    override fun onMapReady(p0: GoogleMap) {
//
//        gMap = p0
//
//    }

}