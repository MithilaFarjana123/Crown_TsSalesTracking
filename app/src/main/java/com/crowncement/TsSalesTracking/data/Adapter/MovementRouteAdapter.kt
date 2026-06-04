package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.crowncement.TsSalesTracking.R

public class MovementRouteAdapter(
    private val itemList: ArrayList<String>,
    private val context: Context
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var txtRoute: TextView
    private lateinit var txtRouteDisc: TextView

    private lateinit var onClickListener: OnAdapterItemClickListener

    var row_index = -1


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.route_gridview_item, null)
        }

        txtRoute = convertView!!.findViewById(R.id.txtRoute)
        txtRouteDisc = convertView!!.findViewById(R.id.txtRouteDisc)

        txtRoute.text       = itemList[position].split(":")[0]
      //  txtRouteDisc.text   = itemList[position].split(":")[1]

        txtRouteDisc.text   = "All areas of "+itemList[position].split(":")[0]

//        convertView.setOnClickListener {
//            row_index = position
//            notifyDataSetChanged()
//            onClickListener.OnClick(convertView, position)
//        }
//
//        if (row_index == position) {
//            convertView.layoutBody.setBackgroundColor(Color.parseColor("#00914C"))
//            convertView.txtRoute.setTextColor(Color.parseColor("#ffffff"))
//        } else {
//            convertView.layoutBody.setBackgroundColor(Color.parseColor("#ffffff"))
//            convertView.txtRoute.setTextColor(Color.parseColor("#000000"))
//        }


        return convertView
    }

}