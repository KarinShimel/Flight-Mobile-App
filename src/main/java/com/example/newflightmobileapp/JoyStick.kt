package com.example.newflightmobileapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newflightmobileapp.JoyStickView.JoystickListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_joy_stick_.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


var lastAilerone: Float = (0.0).toFloat()
var lastElevator: Float = (0.0).toFloat()
var lastThroutle: Float = (0.0).toFloat()
var lastRudder: Float = (0.0).toFloat()

class JoyStick : AppCompatActivity(), JoystickListener {
    var url: String? = null
    var shouldStop: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joy_stick_)
        val extras = intent.extras // Getting the url inserted from the intent
        url = extras!!.getString("URL")
        shouldStop = false
        gettingScreenshot() // Get the screenshot
        setBackActionButton()
        val seek = findViewById<SeekBar>(R.id.seekBar)
        seek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                if ((lastThroutle * 0.98 > seek.progress) || (lastThroutle * 1.02 < seek.progress)) {
                    seek.progress
                    lastThroutle = (seek.progress).toFloat()
                    sendCommand(Command(lastAilerone.toDouble(), lastRudder.toDouble()/100,
                            lastElevator.toDouble(), lastThroutle.toDouble()/100))
                } } })
        val seek1 = findViewById<SeekBar>(R.id.seekBar2)
        seek1?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                if ((lastRudder * 0.98 > seek1.progress) || (lastRudder * 1.02 < seek1.progress)) {
                    seek1.progress
                    lastRudder = (seek1.progress).toFloat()
                    sendCommand(Command(lastAilerone.toDouble(), lastRudder.toDouble()/100,
                            lastElevator.toDouble(), lastThroutle.toDouble()/100))
                } } })
    }

    fun setBackActionButton(){
        // Back button
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Control Panel"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    // Using retrofit to get the image from the server
    private fun gettingScreenshot() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(ApiService::class.java)
        // Getting the picture
        CoroutineScope(Dispatchers.IO).launch {
            while (!shouldStop) {
                delay(250)
                val body = api.getScreenshot().enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        d("test", "onResponse")
                        val bytes = response?.body()?.bytes()
                        val bitmap =
                            bytes?.size?.let { BitmapFactory.decodeByteArray(bytes, 0, it) }
                        if (bitmap != null) { imageView2.setImageBitmap(bitmap) }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@JoyStick,
                            "Error Getting Data from Server", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    override fun onJoystickMoved(
        xPercent: Float,
        yPercent: Float,
        id: Int
    ) {
        if ((lastAilerone * 0.98 > xPercent) || (lastAilerone * 1.02 < xPercent)) {
            if ((lastElevator * 0.98 > yPercent) || (lastElevator * 1.02 < yPercent)) {
                lastAilerone = xPercent
                lastElevator = yPercent
                sendCommand(
                    Command(
                        lastAilerone.toDouble(), (lastRudder.toDouble()/100),
                        lastElevator.toDouble(), (lastThroutle.toDouble()/100)
                    )
                )

            }
        }
        val i = 0;
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        shouldStop = true;
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun sendCommand(command: Command) {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(ApiService::class.java)
        val body = api.addCommand(command).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() !in 400..598) { // we got a valid code

                } else { // Server returned error code
                    Toast.makeText(
                        this@JoyStick,
                        "Error in response code", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(t.toString(),t.message)
                Toast.makeText(
                    this@JoyStick,
                    "Error Getting Data from Server", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}

