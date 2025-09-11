package com.crowncement.TsSalesTracking.services.fcm


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.UI.Activity.MainActivity
import com.crowncement.TsSalesTracking.common.StaticConfiguration.firebaseNotificationChannel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    var broadcaster : LocalBroadcastManager? = null
    lateinit var  sendTo:String

    override fun onCreate() {
        broadcaster  = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        remoteMessage.notification?.let {
            Log.d(TAG, "Message notification body: ${it.body}")

//            sendNotification(
//                it.body.toString(),
//                it.title.toString(),
//                remoteMessage.data["image_path"].toString()

//            )


            // val url = URL(remoteMessage.data["image_path"].toString())
            //  val url = URL(remoteMessage.data["user_image"].toString())
            sendTo=remoteMessage.data["send_to"].toString()

            if(remoteMessage.notification!!.imageUrl.toString()!="") {
                val url = URL(remoteMessage.notification!!.imageUrl.toString())
                val img = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                sendNotification1(it.title.toString(), it.body.toString(), img)
            }
            else{
                val url = URL("https://cdn-icons-png.flaticon.com/512/2190/2190552.png")
                val icon = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                sendNotification1(it.title.toString(), it.body.toString(), icon)
            }


            //val intent = Intent("MyData")
            val intent = Intent("Act_Dashboard")
            intent.putExtra("FRAGMENT", remoteMessage.data["send_to"].toString())
            Log.e(TAG, "CLICKKKKKKKKKKKKK: "+remoteMessage.data["send_to"] )
            broadcaster!!.sendBroadcast(intent)

        }

    }

    override fun onNewToken(token: String) {

        Log.d(TAG, token)
    }

    private fun sendNotification1(title: String?, body: String, bitmap: Bitmap) {

        // val rightIcon = BitmapFactory.decodeResource(this.resources, R.drawable.crown_cement_icon)
        // For large image
        //val style = NotificationCompat.BigPictureStyle()
        //style.bigPicture(null)
        val defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("send_to", sendTo)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val NOTIFICATION_CHANNEL_ID = "101"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notification",
                NotificationManager.IMPORTANCE_MAX
            )

            //Configure Notification Channel
            notificationChannel.description = body
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.crown_cement_icon)
                .setContentTitle(title)
                .setAutoCancel(true)
                // .setSound(defaultSound)
                .setSound(soundUri)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                // .setStyle(style)
                // Right Icon
                //  .setLargeIcon(rightIcon)
                .setLargeIcon(bitmap)
                .setWhen(System.currentTimeMillis())
                // .setPriority(Notification.PRIORITY_MAX)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(firebaseNotificationChannel, notificationBuilder.build())
    }


//    private fun showNotification(title: String?, body: String?, image: String) {
//        val intent = Intent(this, Act_Splash_Screen::class.java)
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        // val largeIcon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.thanks)
//
//        if (image.isNotEmpty()) {
//            val url = URL(image)
//            val imgValue = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//            val pendingIntent =
//                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val notificationBuilder = NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//                .setSound(soundUri)
//                .setLargeIcon(imgValue)
//                .setStyle(
//                    NotificationCompat.BigPictureStyle()
//                        .bigPicture(imgValue)
//                        .bigLargeIcon(null)
//                )
//
//
//                .setContentIntent(pendingIntent)
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(0, notificationBuilder.build())
//
//        } else {
//            val pendingIntent =
//                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val notificationBuilder = NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//                .setSound(soundUri)
//                .setStyle(
//                    NotificationCompat.BigPictureStyle()
//                        .bigLargeIcon(null)
//                )
//                .setContentIntent(pendingIntent)
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(0, notificationBuilder.build())
//        }
//
//
//    }

}