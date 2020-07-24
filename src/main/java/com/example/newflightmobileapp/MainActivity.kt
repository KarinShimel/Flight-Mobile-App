package com.example.newflightmobileapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.newflightmobileapp.roomDataBase.adressAndTime
import com.example.newflightmobileapp.roomDataBase.adressAndTimeDatabase
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Room.databaseBuilder(
            applicationContext,
            adressAndTimeDatabase::class.java, "AdressTable"
        ).build()
        createButtons(db)
        readFromDataBase(db)
    }

    private fun checkForValidServer(urlText: String)  {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(urlText)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(ApiService::class.java)
        // Setting the intent for moving to the next activity
        val intent = Intent(this, JoyStick::class.java)
        intent.putExtra("URL", urlText); // Passing the relevant url

        val body = api.getScreenshot().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() !in 400..598) { // we got a valid code
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity,
                        "Server can't find data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity,
                    "Cant Connect to URL", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun readFromDataBase(db: adressAndTimeDatabase) {
        CoroutineScope(IO).launch {
            val list = db.getAdressAndTimeDao().getAllUrls()
            if (list.isEmpty()) {
                return@launch;
            }
            getFromScope(list)
        }
    }

    fun insertData(adressAndTime: adressAndTime, db: adressAndTimeDatabase) {
        CoroutineScope(IO).launch {
            db.getAdressAndTimeDao().updateNumbers()
            db.getAdressAndTimeDao().addUrl(adressAndTime)
            readFromDataBase(db)
        }
    }

    suspend fun getFromScope(list: List<adressAndTime>) {
        withContext(Main) {
            val button1 = findViewById(R.id.button1) as Button
            val button2 = findViewById(R.id.button2) as Button
            val button3 = findViewById(R.id.button3) as Button
            val button4 = findViewById(R.id.button4) as Button
            val button5 = findViewById(R.id.button5) as Button
            if (list.size == 1) {
                button1.text = list[0].adress
            } else if (list.size == 2) {
                button1.text = list[0].adress
                button2.text = list[1].adress
            } else if (list.size == 3) {
                button1.text = list[0].adress
                button2.text = list[1].adress
                button3.text = list[2].adress
            } else if (list.size == 4) {
                button1.text = list[0].adress
                button2.text = list[1].adress
                button3.text = list[2].adress
                button4.text = list[3].adress
            } else if (list.size >= 5) {
                button1.text = list[0].adress
                button2.text = list[1].adress
                button3.text = list[2].adress
                button4.text = list[3].adress
                button5.text = list[4].adress
            }
        }
    }

    //create buttons and click listeners for each one
    @RequiresApi(Build.VERSION_CODES.O)
    fun createButtons(database: adressAndTimeDatabase) {
        val button1 = findViewById(R.id.button1) as Button
        button1.setOnClickListener {
            if (button1.text.equals("1")) { return@setOnClickListener }
            url.setText(button1.text.toString())
        }
        val button2 = findViewById(R.id.button2) as Button
        button2.setOnClickListener {
            if (button2.text.equals("2")) { return@setOnClickListener }
            url.setText(button2.text.toString())
        }
        val button3 = findViewById(R.id.button3) as Button
        button3.setOnClickListener {
            if (button3.text.equals("3")) { return@setOnClickListener }
            url.setText(button3.text.toString())
        }
        val button4 = findViewById(R.id.button4) as Button
        button4.setOnClickListener {
            if (button4.text.equals("4")) { return@setOnClickListener }
            url.setText(button4.text.toString())
        }
        val button5 = findViewById(R.id.button5) as Button
        button5.setOnClickListener {
            if (button5.text.equals("5")) { return@setOnClickListener }
            url.setText(button5.text.toString())
        }
        // Connect Button
        createConnectButton(database)
    }

    fun createConnectButton(database: adressAndTimeDatabase){
        val connectButton = findViewById(R.id.connectButton) as Button
        connectButton.setOnClickListener {
            val urlText = url.text.toString().trim()
            if (urlText.isEmpty()) {
                url.error = "url adress required"
                url.requestFocus()
                return@setOnClickListener
            }
            // Adding the url to the db and setting the time
            val url = adressAndTime(urlText)
            insertData(url, database)
            // Trying to connect to the server
            try {
                checkForValidServer(urlText)
            } catch (e: Exception) { // If the URL wasn't valid
                Toast.makeText(
                    this@MainActivity,
                    "Invalid URL", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}