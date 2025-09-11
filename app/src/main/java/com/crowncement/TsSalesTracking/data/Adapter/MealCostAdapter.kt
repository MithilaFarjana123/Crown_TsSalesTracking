package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.MealCost
import com.crowncement.TsSalesTracking.databinding.MealCostItemBinding


class MealCostAdapter (private val mealList: ArrayList<MealCost>) :

    RecyclerView.Adapter<MealCostAdapter.ViewHolder>() {

    var listSize: ArrayList<MealCost> = java.util.ArrayList()

    private val TAG = "MealCostAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnSelectSubMenu(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = MealCostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context
/*
        if(position%2==1){
            holder.itemView.setBackgroundColor(Color.parseColor("#eafaf1"))
        }

 */

        val meal = mealList[position]

        val mealDate: String = Utility.changeDateFormat(meal.lnDate,"yyyy-MM-dd","EEE, dd MMM yy").toString()
         //   holder.itemView.txtStatus.setTextColor(Color.parseColor("#F44336"))
        holder.binding.txtMealDate.text=mealDate

      //  holder.itemView.txtMealStatus.text =meal.Status
        if(meal.lnStat.equals("Confirmed")){

            holder.binding.txtMealStatus.setImageResource(R.drawable.correct_meal)
        }
        else{
            holder.binding.txtMealStatus.setImageResource(R.drawable.baseline_report_problem)
        }


        holder.binding.txtMeal.text =meal.lnQty.toString()
        holder.binding.txtRate.text =meal.lnRate.toString()
        holder.binding.txtOverhead.text =meal.lnOvh.toString()

    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    class ViewHolder(val binding: MealCostItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use ViewBinding to set up the click listener
            binding.root.setOnClickListener {
                listener.OnSelectSubMenu(binding.root, adapterPosition)
            }
        }
    }

}