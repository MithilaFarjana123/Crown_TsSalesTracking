package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.crowncement.TsSalesTracking.R

class OptionAdapter (
    private val itemList: ArrayList<String>,
    private val context: Context
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var txtOption: TextView
    private lateinit var imgCheck: ImageView

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
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.option_gridview_item, null)
        }


        txtOption = convertView!!.findViewById(R.id.txtOption)
        txtOption.text=itemList[position]



        return convertView
    }

}