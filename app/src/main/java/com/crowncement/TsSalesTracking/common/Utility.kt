package com.crowncement.TsSalesTracking.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.Act_Offline_Attendance
import com.crowncement.TsSalesTracking.UI.Activity.Act_Offline_QuickVisit
import com.crowncement.TsSalesTracking.UI.Activity.Act_Splash_Screen
import com.crowncement.TsSalesTracking.data.Model.Att
import com.crowncement.TsSalesTracking.data.Model.BankImage
import com.crowncement.TsSalesTracking.data.Model.Data
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.tapadoo.alerter.Alerter
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Utility {

    private const val TAG = "Utility"

    fun getCurrentDate(format: String?): String? {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat(format, Locale.getDefault())
        return df.format(c)
    }

    fun getDayCountBetweenDates(mDate1: String, mDate2: String): Int {
        val mDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val mDate11 = mDateFormat.parse(mDate1)
        val mDate22 = mDateFormat.parse(mDate2)
        val mDifference = kotlin.math.abs(mDate11.time - mDate22.time)
        val mDifferenceDates = mDifference / (24 * 60 * 60 * 1000)
        return mDifferenceDates.toInt()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDatesBetween(from: String, to: String?): ArrayList<String> {
        val dates: ArrayList<String> = ArrayList()
        if (from != to) {
            val f = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val t = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            var date: LocalDate = f
            while (!date.isAfter(t)) {
                dates.add(date.toString())
                date = date.plusDays(1)
            }
        } else {
            dates.add(from)
        }
        return dates
    }

    fun loadImage(imageView: ImageView, context: Context, imgString: String) {
        val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val decodeImage = sharedPref.getString(imgString, "DEFAULT")

        if (decodeImage != "DEFAULT") {
            val imageBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getDecoder().decode(decodeImage!!.toByteArray())
            } else {
                android.util.Base64.decode(decodeImage!!.toByteArray(), android.util.Base64.DEFAULT)
            }

            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageView.setImageBitmap(decodedImage)
        }
    }


    fun getValueByKey(activity: Activity, key: String?): String? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        return prefs.getString(key, "")
    }

    fun getAttendancePeriod(): String {

        val previousMonth = getCurrentDate("MM")!!.toInt() - 1

        var startDate: String = ""
        startDate = if (previousMonth.toString().length == 1) {
            getCurrentDate("yyyy") + "-0" + previousMonth.toString() + "-26"
        } else {
            getCurrentDate("yyyy") + "-" + previousMonth.toString() + "-26"
        }


        val endDate = getCurrentDate("yyyy") + "-" + getCurrentDate("MM") + "-25"

        return "$startDate,$endDate"

    }


    fun removePreference(context: Context, key: String?) {
        val preferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(key)
        editor.commit()
    }

    fun baseLoadingAnimation(activity: Activity, dialog: Dialog, mess: String): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mView: View = inflater.inflate(R.layout.loading_dialog, null, false)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val message = mView.findViewById<TextView>(R.id.txtDefaultMessage)
        message.text = mess
        dialog.setContentView(mView)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        Objects.requireNonNull(window)!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window.setGravity(Gravity.CENTER)
        //dialog.show();
        return dialog
    }

    fun getUserInfo(activity: Activity): Data? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("UserInfo", "")
        return gson.fromJson(json, Data::class.java)
    }

    fun getBaseMessage(activity: Activity, title: String, mess: String, icon: Int, status: Int) {

        when (status) {
            0 -> {
                Alerter.create(activity)
                    .setTitle(title)
                    .setText(mess)
                    .setIcon(icon)
                    .setBackgroundColorRes(R.color.orange)
                    // .setIconColorFilter(0) // Optional - Removes white tint
                    // .setIconSize(38) // Optional - default is 38dp
                    .show()
            }
            1 -> {
                Alerter.create(activity)
                    .setTitle(title)
                    .setText(mess)
                    .setIcon(icon)
                    .setBackgroundColorRes(R.color.crown_icon_color)
                    // .setIconColorFilter(0) // Optional - Removes white tint
                    // .setIconSize() // Optional - default is 38dp
                    .show()
            }
            else -> {
                Alerter.create(activity)
                    .setTitle(title)
                    .setText(mess)
                    .setIcon(icon)
                    .setBackgroundColorRes(R.color.reject)
                    //.setIconColorFilter(0) // Optional - Removes white tint
                    // .setIconSize(38) // Optional - default is 38dp
                    .show()
            }
        }
    }

    fun changeDateFormat(date: String?, oldFormat: String?, newFormat: String?): String? {
        val newDateString: String
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(oldFormat)
        var d: Date? = null
        try {
            d = sdf.parse(date)
        } catch (e: ParseException) {
            Log.e(TAG, "ChangeDateFormat: " + e.message)
        }
        sdf.applyPattern(newFormat)
        newDateString = sdf.format(Objects.requireNonNull(d))
        return newDateString
    }

    fun formatAmount(number: Double): String? {
        val formatter = DecimalFormat("#,##,###")
        return formatter.format(number)
    }

    fun storeKeyValue(activity: Activity, key: String?, value: String?) {
        val editor =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
        Log.d(key, value!!)
    }


//todo


    fun commonToast(activity: Activity, title: String, message: String, status: Int) {
        when (status) {
            0 -> {
                MotionToast.createColorToast(
                    activity,
                    title,
                    message,
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(activity, www.sanju.motiontoast.R.font.helveticabold)
                )

            }
            1 -> {
                MotionToast.createColorToast(
                    activity,
                    title,
                    message,
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(activity, www.sanju.motiontoast.R.font.helveticabold)
                )
            }
            2 -> {
                MotionToast.createColorToast(
                    activity,
                    title,
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(activity, www.sanju.motiontoast.R.font.helveticabold)
                )
            }
            else -> {
                MotionToast.createColorToast(
                    activity,
                    title,
                    message,
                    MotionToastStyle.INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(activity, www.sanju.motiontoast.R.font.montserrat_bold)
                )
            }
        }
    }


    fun sendActivity(from: Activity, className: String) {
        try {
            val cls = Class.forName("com.crowncement.TsSalesTracking.UI.Activity.$className")
            val intent = Intent(from, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            from.startActivity(intent)
            from.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "SendActivity: " + e.message)
        }
    }

    fun displayNotification(context: Context, title: String, body: String) {

        val intent = Intent(context, Act_Splash_Screen::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mBuilder = NotificationCompat.Builder(context, "CrownTitan")
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val mNotificationMgr = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mNotificationMgr.notify(1, mBuilder.build())

    }


    fun compareTimeFrame(startTime: String?, endTime: String?): Boolean {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm")
        val currentTime: String = getCurrentDate("HH:mm")!!
        try {
            val date1 = sdf.parse(startTime)
            val date2 = sdf.parse(endTime)
            val cu = sdf.parse(currentTime)
            return if (cu.after(date1) && cu.before(date2)) {
                Log.d(TAG, "CompareTimeFrame: Spinner Loading Time")
                true
            } else {
                Log.d(TAG, "CompareTimeFrame: Invalid Spinner Loading Time")
                false
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e(TAG, "ERROR- CompareTimeFrame: " + e.message)
        }
        return false
    }

    fun getAttInfo(activity: Activity): Att? {
        val prefs = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("AttInfo", "")
        return gson.fromJson(json, Att::class.java)
    }

    fun saveAttInfo(info: Att?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("AttInfo", json)
        mPrefs.commit()
    }




    fun noInternetDialog(activity: Activity) {
        val v: View = activity.layoutInflater.inflate(R.layout.layout_no_internet, null)
        val dialog = Dialog(activity, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        val btnRefresh = v.findViewById<Button>(R.id.btnRefresh)
        val btnOfflineAttendance = v.findViewById<Button>(R.id.btnOfflineAttendance)
        val btnQuickVisit = v.findViewById<Button>(R.id.btnQuickVisit)
        //val mPlayer: MediaPlayer = MediaPlayer.create(activity, R.raw.check_internet_connection)
        // mPlayer.start()
        dialog.setContentView(v)
        dialog.setCancelable(false)
        dialog.show()

        btnRefresh.setOnClickListener {
            //val intent = Intent(activity, Act_Splash_Screen::class.java)
            //  activity.startActivity(intent)
            dialog.dismiss()

        }

        btnOfflineAttendance.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(activity, Act_Offline_Attendance::class.java)
            activity.overridePendingTransition(0, 0)
            activity.startActivity(intent)
        }

        btnQuickVisit.setOnClickListener {
            val intent = Intent(activity, Act_Offline_QuickVisit::class.java)
            activity.overridePendingTransition(0, 0)
            activity.startActivity(intent)
        }
    }


    fun setAppLanguage(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        storeKeyValue(activity, "language", languageCode)
        sendActivity(activity, "Act_Dashboard")

        // Helper.SendActivity(activity,"Act_Splash_Screen")

    }


    fun generateQRCode(text: String, size: Int): Bitmap? {
        try {
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val bitMatrix: BitMatrix =
                MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            Log.e(TAG, "generateQRCode: ${e.message.toString()}")
        }
        return null
    }


    fun saveUserInfo(info: Data?, activity: Activity) {
        val mPrefs =
            activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(info)
        mPrefs.putString("UserInfo", json)
        mPrefs.commit()
    }


    fun getBankImage(bankName: String): String {
        val bankList: ArrayList<BankImage> = ArrayList()
        var result = ""

        bankList.add(BankImage("AB Bank Ltd.", "https://www.banksbd.org/assets/images/logos/1.jpg"))
        bankList.add(
            BankImage(
                "Agrani Bank Ltd. ",
                "https://www.banksbd.org/assets/images/logos/2.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Al-Arafah Islami Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/3.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Bangladesh Commerce Bank",
                "https://www.banksbd.org/assets/images/logos/4.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Bangladesh Development Bank",
                "https://www.banksbd.org/assets/images/logos/5.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Bank Alfalah Limited",
                "https://www.banksbd.org/assets/images/logos/6.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Bank Asia Limited",
                "https://www.banksbd.org/assets/images/logos/7.jpg"
            )
        )
        bankList.add(BankImage("BASIC Bank", "https://www.banksbd.org/assets/images/logos/8.jpg"))
        bankList.add(
            BankImage(
                "BRAC Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/9.jpg"
            )
        )
        bankList.add(BankImage("City Bank", "https://www.banksbd.org/assets/images/logos/10.jpg"))
        bankList.add(
            BankImage(
                "Commercial Bank of Ceylon PLC",
                "https://www.banksbd.org/assets/images/logos/11.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Dhaka Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/12.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Dutch Bangla Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/13.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Eastern Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/14.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Exim Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/15.jpg"
            )
        )
        bankList.add(BankImage("Padma Bank", "https://www.banksbd.org/assets/images/logos/16.jpg"))
        bankList.add(
            BankImage(
                "First Security Islami Bank",
                "https://www.banksbd.org/assets/images/logos/17.jpg"
            )
        )
        bankList.add(BankImage("Habib Bank", "https://www.banksbd.org/assets/images/logos/18.jpg"))
        bankList.add(
            BankImage(
                " The HSBC Ltd.",
                "https://www.banksbd.org/assets/images/logos/19.jpg"
            )
        )
        bankList.add(
            BankImage(
                "ICB Islamic Bank",
                "https://www.banksbd.org/assets/images/logos/20.jpg"
            )
        )
        bankList.add(
            BankImage(
                "IFIC Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/21.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Islami Bank Bangladesh Limited",
                "https://www.banksbd.org/assets/images/logos/22.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Jamuna Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/23.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Janata Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/24.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Bangladesh Krishi Bank",
                "https://www.banksbd.org/assets/images/logos/25.jpg"
            )
        )
        bankList.add(BankImage("Meghna Bank", "https://www.banksbd.org/assets/images/logos/26.jpg"))
        bankList.add(
            BankImage(
                "Mercantile Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/27.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Midland Bank",
                "https://www.banksbd.org/assets/images/logos/28.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Mutual Trust Bank Limited",
                "https://www.banksbd.org/assets/images/logos/29.jpg"
            )
        )
        bankList.add(
            BankImage(
                "National Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/30.jpg"
            )
        )
        bankList.add(
            BankImage(
                "National Bank of Pakistan",
                "https://www.banksbd.org/assets/images/logos/31.jpg"
            )
        )
        bankList.add(
            BankImage(
                "NCC Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/32.jpg"
            )
        )
        bankList.add(
            BankImage(
                "NRB Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/33.jpg"
            )
        )
        bankList.add(
            BankImage(
                "One Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/34.jpg"
            )
        )
        bankList.add(
            BankImage(
                "The Premier Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/35.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Prime Bank Limited",
                "https://www.banksbd.org/assets/images/logos/36.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Pubali Bank Ltd.",
                "https://www.banksbd.org/assets/images/logos/37.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Rajshahi Krishi Unnayan Bank",
                "https://www.banksbd.org/assets/images/logos/38.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Rupali Bank Limited",
                "https://www.banksbd.org/assets/images/logos/39.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Shahjalal Islami Bank",
                "https://www.banksbd.org/assets/images/logos/40.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Social Islami Bank",
                "https://www.banksbd.org/assets/images/logos/41.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Sonali Bank Limited",
                "https://www.banksbd.org/assets/images/logos/42.jpg"
            )
        )
        bankList.add(BankImage("SBAC Bank", "https://www.banksbd.org/assets/images/logos/43.jpg"))
        bankList.add(
            BankImage(
                "Southeast Bank Ltd",
                "https://www.banksbd.org/assets/images/logos/44.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Standard Bank Limited",
                "https://www.banksbd.org/assets/images/logos/45.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Standard Chartered Bank",
                "https://www.banksbd.org/assets/images/logos/46.jpg"
            )
        )
        bankList.add(BankImage("Trust Bank", "https://www.banksbd.org/assets/images/logos/47.jpg"))
        bankList.add(BankImage("Union Bank", "https://www.banksbd.org/assets/images/logos/48.jpg"))
        bankList.add(
            BankImage(
                "United Commercial Bank Limited",
                "https://www.banksbd.org/assets/images/logos/49.jpg"
            )
        )
        bankList.add(BankImage("Uttara Bank", "https://www.banksbd.org/assets/images/logos/50.jpg"))
        bankList.add(
            BankImage(
                "NRB Commercial Bank",
                "https://www.banksbd.org/assets/images/logos/51.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Global Islami Bank",
                "https://www.banksbd.org/assets/images/logos/52.jpg"
            )
        )
        bankList.add(BankImage("Woori Bank", "https://www.banksbd.org/assets/images/logos/53.jpg"))
        bankList.add(
            BankImage(
                "State Bank of India",
                "https://www.banksbd.org/assets/images/logos/54.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Modhumoti Bank",
                "https://www.banksbd.org/assets/images/logos/55.jpg"
            )
        )
        bankList.add(BankImage("Citibank", "https://www.banksbd.org/assets/images/logos/56.jpg"))
        bankList.add(
            BankImage(
                "Shimanto Bank",
                "https://www.banksbd.org/assets/images/logos/57.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Bengal Commercial Bank",
                "https://www.banksbd.org/assets/images/logos/58.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Community Bank",
                "https://www.banksbd.org/assets/images/logos/59.jpg"
            )
        )
        bankList.add(
            BankImage(
                "Probashi Kallyan Bank",
                "https://www.banksbd.org/assets/images/logos/60.jpg"
            )
        )

        for (item in bankList) {
            // if (item.BankName!!.contains(bankName, ignoreCase = true)) {
            if (item.BankName!!.contains(bankName)) {
                result = item.BankImage.toString()
                break
            }
        }
        return result
    }


    fun findDifference(start_date: String?, end_date: String?): String? {
        var res = ""
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        try {
            val d1 = sdf.parse(start_date)
            val d2 = sdf.parse(end_date)
            val difference_In_Time = Objects.requireNonNull(d2).time - (d1?.time ?: 0)
            val difference_In_Seconds = difference_In_Time / 1000 % 60
            val difference_In_Minutes = difference_In_Time / (1000 * 60) % 60
            val difference_In_Hours = difference_In_Time / (1000 * 60 * 60) % 24
            val difference_In_Years = difference_In_Time / (1000L * 60 * 60 * 24 * 365)
            val difference_In_Days = difference_In_Time / (1000 * 60 * 60 * 24) % 365
            val difference_In_Months = difference_In_Days / 30

            if (difference_In_Years > 1 && difference_In_Months > 1) {
                res = "Almost $difference_In_Years Years, $difference_In_Months months"
            } else if (difference_In_Years > 1 && difference_In_Months <= 1) {
                res = "Almost $difference_In_Years Years, $difference_In_Months month"
            } else if (difference_In_Years <= 1 && difference_In_Months > 1) {
                res = "Almost $difference_In_Years Year, $difference_In_Months months"
            } else {
                res = "Almost $difference_In_Years Year, $difference_In_Months month"
            }
        } catch (e: ParseException) {
            //  Log.e(Common.TAG, "FindDifference: " + e.message)
        }
        return res
    }


    fun getDeviceTimeZone(c: Context):Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.Global.getInt(c.contentResolver, Settings.Global.AUTO_TIME_ZONE, 0) == 1

        } else {
            Settings.System.getInt(c.contentResolver, Settings.System.AUTO_TIME_ZONE, 0) == 1
        }
    }


    //todo store arraylist
//    fun getFilesFromSharedPreferences(context: Context): ArrayList<File>? {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//
//        // Retrieve the StringSet (file paths)
//        val filePathsSet = sharedPreferences.getStringSet("filePaths", null)
//        val fileList = ArrayList<File>()
//
//        filePathsSet?.forEach { path ->
//            val file = File(path)
//            if (file.exists()) {
//                fileList.add(file)
//            }
//        }
//
//        return fileList
//    }
//
//    fun saveFilesToSharedPreferences(context: Context, fileList: ArrayList<File>) {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        // Convert ArrayList<File> to ArrayList<String> (file paths)
//        val filePaths = ArrayList<String>()
//        for (file in fileList) {
//            filePaths.add(file.absolutePath)  // Add the file's absolute path to the list
//        }
//
//        // Store the ArrayList of file paths as a StringSet in SharedPreferences
//        editor.putStringSet("filePaths", filePaths.toSet()) // Converting ArrayList to Set
//        editor.apply()
//    }


    fun saveFilesByKey(context: Context, key: String, fileList: ArrayList<File>) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert ArrayList<File> to ArrayList<String> (only file paths)
        val filePaths = ArrayList<String>()
        for (file in fileList) {
            filePaths.add(file.absolutePath)  // Save each file's absolute path as a string
        }

        // Convert filePaths to JSON string
        val json = Gson().toJson(filePaths)

        // Save JSON string in SharedPreferences with the unique key
        editor.putString(key, json)
        editor.apply()
    }


    fun getFilesByKey(context: Context, key: String): ArrayList<File>? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve JSON string by the key
        val json = sharedPreferences.getString(key, null)

        // If no data is found for the key, return null
        if (json.isNullOrEmpty()) return null

        // Deserialize JSON back to ArrayList<String> of file paths
        val filePathsType = object : TypeToken<ArrayList<String>>() {}.type
        val filePaths = Gson().fromJson<ArrayList<String>>(json, filePathsType)

        // Convert file paths to ArrayList<File>
        val fileList = ArrayList<File>()
        filePaths.forEach { path ->
            val file = File(path)
            if (file.exists()) {
                fileList.add(file)
            }
        }

        return fileList
    }



    //file store
    fun saveFileToSharedPreferences(context: Context, file: File, key: String) {
        val fileContent = file.readText()
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(key).apply()  // Explicitly clear previous
        sharedPreferences.edit().putString(key, fileContent).apply()
    }


    fun getFileFromSharedPreferences(context: Context, key: String, outputFile: File) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val fileContent = sharedPreferences.getString(key, null)
        if (fileContent != null) {
            outputFile.writeText(fileContent)
        }
    }



}