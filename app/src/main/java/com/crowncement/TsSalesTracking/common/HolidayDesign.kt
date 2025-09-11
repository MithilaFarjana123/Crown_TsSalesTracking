package com.crowncement.TsSalesTracking.common

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import androidx.core.content.ContextCompat
import com.crowncement.TsSalesTracking.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class HolidayDesign (context: Activity?, currentDay: CalendarDay) : DayViewDecorator {
    private val drawable: Drawable?
    var myDay = currentDay
    private val boldTypeface: Typeface
    override fun shouldDecorate(day: CalendarDay): Boolean {

        return day == myDay

    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
        view.addSpan( ForegroundColorSpan(Color.parseColor("#FFFFFF")))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.addSpan(TypefaceSpan(boldTypeface))
        }
    }

    init {
        // You can set background for Decorator via drawable here
        boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        drawable = ContextCompat.getDrawable(context!!, R.drawable.circle_full)
    }
}