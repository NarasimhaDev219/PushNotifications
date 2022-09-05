package com.example.pushnotifications.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pushnotifications.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHENNELID = "1"
    private var imgUrl: String = ""
    private var imageBitmap: Bitmap? = null

    override fun onMessageReceived(message: RemoteMessage) {

        //Create Notification channel if Device is using  API 26+
        createNotificationChannel()

        myRegistrationToken()

        if (message != null) {

            //Get Image From firebase
            Log.e("TAG", "imageUrl===: ${message.notification?.imageUrl}")
            if (message.notification?.imageUrl != null) {
                imgUrl = message.notification!!.imageUrl.toString()
                imageBitmap = getBitmapFromUrl(imgUrl)!!
            }
            showNotification(
                message.notification?.title.toString(),
                message.notification?.body.toString()
            )
        }
        // want to get payload with key and value pair of data receiving
        if (message.data.isNotEmpty()){
            val map : Map<String,String> = message.data
            Log.e("TAG", "onMessageReceivMap==== ${map["Key1"].toString()}", )
            Log.e("TAG", "onMessageReceivMap==== ${map["Key2"].toString()}", )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun showNotification(title: String, text: String) {

        val defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val builder = NotificationCompat.Builder(
            this,
            CHENNELID
        )
            .setSmallIcon(R.drawable.ic_chat)
            .setContentTitle(title)
            .setContentText(text)
            .setLargeIcon(imageBitmap)
            .setSound(defaultNotificationSound)
            .setLights(Color.GREEN,500,200)
            .setVibrate(longArrayOf(0,250,250,250))
            .setStyle(
                NotificationCompat.BigPictureStyle().bigPicture(imageBitmap).bigLargeIcon(null)
            )
            .setAutoCancel(true)
            .setColor(resources.getColor(R.color.red))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = NotificationManagerCompat.from(this)
        //Notification id is Unique for each notification you create
        manager.notify(1, builder.build())
    }

    //if we want get image from backend must convert url to Bitmap for Large icon
    private fun getBitmapFromUrl(imgUrl: String): Bitmap? {
        return try {
            val url = URL(imgUrl)
            val httpConnection = url.openConnection() as HttpURLConnection
            httpConnection.doInput = true
            httpConnection.connect()

            val inputStreem = httpConnection.inputStream
            BitmapFactory.decodeStream(inputStreem)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


    //Create Notification channel if Device is using  API 26+
    private fun createNotificationChannel() {

        // Create Notification Channel only API level 26+
        // Notification Channel is a new class and not in a support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel Name"
            val desc = "My Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val chennel = NotificationChannel(CHENNELID, name, importance)
            chennel.vibrationPattern = longArrayOf(0,250,250,250)
            chennel.description = desc

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chennel)
        }
    }

    //This function for creating FCM token for Devices
    private fun myRegistrationToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e("TAG", "Get InstanceID failed: ${it.exception} ")
                    return@addOnCompleteListener
                }

                val token = it.result
                Toast.makeText(applicationContext, "$token", Toast.LENGTH_LONG).show()
                Log.e("TAG", "myRegistrationToken: $token ")

            }
    }
}