package com.example.pushnotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val CHENNELID = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for unsubscribing small_discount part from firebase console
        //FirebaseMessaging.getInstance().unsubscribeFromTopic("small_discount")

        //Create Notification channel if Device is using  API 26+
        createNotificationChannel()

        myRegistrationToken()

        btnBuy.setOnClickListener {
            val cookies = edtCookies.text.toString()

            subscribeToDiscount(Integer.parseInt(cookies))

            intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("cookie", cookies)
            val pendigIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            //Layouts for creating custom notifications
            val collapsedLayout = RemoteViews(packageName,R.layout.collapsed_layout)
            val expandedLayout = RemoteViews(packageName,R.layout.expanded_layout)
            expandedLayout.setImageViewResource(R.id.imgCookies,R.drawable.cookie)
            expandedLayout.setTextViewText(R.id.tvNumberOfCookies,"You successfully bought $cookies Cookies")

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cookie)

            val defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(
                this@MainActivity,
                CHENNELID
            )
                .setSmallIcon(R.drawable.ic_chat)
                .setCustomContentView(collapsedLayout)
                .setCustomBigContentView(expandedLayout)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pendigIntent)
                .setContentTitle("Cookies")
                .setContentText("You just bought $cookies Cookies!")
                .setContentIntent(pendigIntent)
                .setSound(defaultNotificationSound)
                .setLights(Color.GREEN,500,200)
                .setVibrate(longArrayOf(0,250,250,250))
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                // For expanding the large icon use SetStyle
                .setStyle( // for expandable image
                    NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)
                )
                .addAction(R.mipmap.ic_launcher, "Get BONUS!", pendigIntent)
                .setColor(resources.getColor(R.color.red))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            val manager = NotificationManagerCompat.from(this@MainActivity)
            //Notification id is Unique for each notification you create
            manager.notify(2, builder.build())

        }
    }

    fun createNotificationChannel() {

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

   /* .setStyle( // for big expandable text
    NotificationCompat.BigTextStyle()
    .bigText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.")
    )
    .setStyle(// customize
    NotificationCompat.InboxStyle()
    .addLine("Line 1")
    .addLine("Line 2")
    .addLine("Line 3")
    .addLine("Line 4")
    .addLine("Line 5")
    )*/
   private fun myRegistrationToken(){
       FirebaseMessaging.getInstance().token
           .addOnCompleteListener{
               if (!it.isSuccessful){
                   Log.e("TAG", "Get InstanceID failed: ${it.exception} " )
                   return@addOnCompleteListener
               }

               val token = it.result
               Toast.makeText(applicationContext,"$token", Toast.LENGTH_LONG).show()
               Log.e("TAG", "myRegistrationToken: $token " )

           }
   }

    // for getting notifications from multiple devices
    private fun subscribeToDiscount(cookeis: Int){

        if (cookeis <= 50){
            FirebaseMessaging.getInstance().subscribeToTopic("small_discount")
                .addOnCompleteListener{
                    if (!it.isSuccessful){
                        Toast.makeText(applicationContext,"Failed to subscribe to small Discount ", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext,"Successfully subscribed to small Discount ", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic("huge_discount")
                .addOnCompleteListener{
                    if (!it.isSuccessful){
                        Toast.makeText(applicationContext,"Failed to subscribe to huge Discount ", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext,"Successfully subscribed to huge Discount ", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}