package com.crowncement.TsSalesTracking.UI.Fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.crowncement.TsSalesTracking.UI.viewmodel.CommonViewModel
import com.crowncement.TsSalesTracking.UI.viewmodelfactory.CommonViewModelFactory
import com.crowncement.TsSalesTracking.common.HolidayDesign
import com.crowncement.TsSalesTracking.common.Status
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Adapter.HolidayAdapter
import com.crowncement.TsSalesTracking.data.Model.HolidayResponseChild
import com.crowncement.TsSalesTracking.databinding.FragCalenderBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import java.util.Calendar


class Frag_Calender : Fragment() {

    private var _binding: FragCalenderBinding? = null
    private val binding get() = _binding!!

    var user = ""

    lateinit var loadingAnim: Dialog

    lateinit var commonViewModel: CommonViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragCalenderBinding.inflate(inflater, container, false)


/*
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(requireActivity(), Dialog(requireActivity()), "Checking Holidays")
        loadingAnim.show()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        prepareCalendar()
        onClickListener()

 */



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use binding safely
        setupViewModel()
        loadingAnim = Utility.baseLoadingAnimation(requireActivity(), Dialog(requireActivity()), "Checking Holidays")
        loadingAnim.show()

        user = Utility.getValueByKey(requireActivity(), "username")!!
        prepareCalendar()
        onClickListener()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear ViewBinding reference
        _binding = null
    }




    private fun onClickListener() {

        binding.holidayView.setOnDateChangedListener { widget, date, selected ->


            val aDate = binding.holidayView.selectedDate.year.toString() + "-" +
                    (binding.holidayView.selectedDate.month + 1).toString() + "-" +
                    binding.holidayView.selectedDate.day.toString()



            Log.d("", "selectedDate: $aDate")


        }

        binding.holidayView.setOnMonthChangedListener { widget, dates ->

            loadingAnim.show()
            val mon: Int = dates.month + 1
            getHolidayList(user, dates.year.toString(), mon.toString())

        }
    }


    private fun prepareCalendar() {
        val month = Utility.getCurrentDate("MM")!!.toInt()
        val year = Utility.getCurrentDate("yyyy")!!.toInt()


        val lastDayOfMonth: Int = getDaysInMonth(year, month)

        // Customize the calendar view
        binding.holidayView.state().edit()
            .setMinimumDate(CalendarDay.from(year, 0, 1))
            //   .setMaximumDate(CalendarDay.from(year, 11, lastDayOfMonth))
            .setMaximumDate(CalendarDay.from(2050, 11, 31))
            .setFirstDayOfWeek(Calendar.SATURDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        val mon = Utility.getCurrentDate("MM")!!.toInt()



        getHolidayList(user, Utility.getCurrentDate("yyyy")!!, mon.toString())

    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        val mon: Int = month - 1
        calendar.set(year, mon, 1) // month is 0-based in Calendar class
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun setupViewModel() {

        commonViewModel = ViewModelProviders.of(
            this.requireActivity(),
            CommonViewModelFactory()
        )[CommonViewModel::class.java]
    }

    private fun getHolidayList(emp: String, year: String, month: String) {
        commonViewModel.getHolidayList(emp, year, month)
            ?.observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.responseData?.let { res ->
                            if (res.code == "200" && res.data.size > 0) {

                                loadItemRv(res.data)

                                for (item in res.data) {

                                    val date = item.HolidayDate!!.split("-")[0] + "/" +
                                            (item.HolidayDate!!.split("-")[1].toInt() - 1) + "/" +
                                            item.HolidayDate!!.split("-")[2].toInt()

                                    Log.d("holidays date", date)

                                    val mydate = CalendarDay.from(
                                        item.HolidayDate!!.split("-")[0].toInt(),
                                        item.HolidayDate!!.split("-")[1].toInt() - 1,
                                        item.HolidayDate!!.split("-")[2].toInt()
                                    ) // year, month, date
                                    binding.holidayView.invalidateDecorators()
                                    binding.holidayView.addDecorators(
                                        HolidayDesign(
                                            activity,
                                            mydate
                                        )
                                    )
                                }

                                loadingAnim.dismiss()
                            } else {

                                loadingAnim.dismiss()
                                Toast.makeText(requireContext(), res.message, Toast.LENGTH_LONG).show()
                                binding.rvHoliday.visibility = View.GONE
                                binding.noCard.visibility = View.VISIBLE
                            }
                        }
                    }

                    Status.LOADING -> {

                    }

                    Status.ERROR -> {
                        loadingAnim.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun loadItemRv(itemList: ArrayList<HolidayResponseChild>) {
        if (itemList.size > 0) {

            binding.rvHoliday.visibility = View.VISIBLE
            binding.noCard.visibility = View.GONE
            val itemAdapter = HolidayAdapter(itemList)
            val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvHoliday.layoutManager = manager
            binding.rvHoliday.adapter = itemAdapter

            itemAdapter.setOnItemClickListener(object : HolidayAdapter.OnAdapterItemClickListener {

                override fun onSelectItem(v: View?, position: Int) {

                }
            })


        } else {
            binding.rvHoliday.visibility = View.GONE
            binding.noCard.visibility = View.VISIBLE
        }

    }


}