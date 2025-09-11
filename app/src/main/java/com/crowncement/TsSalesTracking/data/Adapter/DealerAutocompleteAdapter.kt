package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.crowncement.TsSalesTracking.data.Model.DealerChild


class DealerAutocompleteAdapter (context: Context, @LayoutRes private val layoutResource: Int, private val dealerList: ArrayList<DealerChild> ) : ArrayAdapter<DealerChild>(context, layoutResource, dealerList),
    Filterable {
    private var mPois: ArrayList<DealerChild> = dealerList

    override fun getCount(): Int {
        return mPois.size
    }

    override fun getItem(p0: Int): DealerChild? {
        return mPois[p0]

    }

    override fun getItemId(p0: Int): Long {

        return mPois[p0].dealerCode!!.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = mPois[position].dealerName
        return view
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults,
            ) {
                mPois = filterResults.values as ArrayList<DealerChild>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    dealerList
                else
                    dealerList.filter {
                        it.dealerName!!.toLowerCase().contains(queryString)
                    }

                return filterResults
            }

        }
    }
}