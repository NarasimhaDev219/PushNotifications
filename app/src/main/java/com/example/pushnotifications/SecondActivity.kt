package com.example.pushnotifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val numberOfCookies  = intent.getStringExtra("cookie")
        Log.e("TAG", "onCreate: getStringExtra "+numberOfCookies.toString()  )
        //textView.text = numberOfCookies
        throw RuntimeException("Test Crash")
        val cookies : Int = Integer.valueOf(numberOfCookies)
        if (cookies  < 50){
            Toast.makeText(this,"You get small bonus",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"You get HUGE bonus",Toast.LENGTH_LONG).show()
        }

    }
}