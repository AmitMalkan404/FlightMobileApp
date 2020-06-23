package com.example.flightmobileapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.flightmobileapp.databinding.ActivityMainBinding
import com.example.flightmobileapp.viewmodel.UrlViewModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_screenshot.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScreenshotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var urlViewModel: UrlViewModel
    private lateinit var adapter: MyRecyclerViewAdapter;
    private var aileron : Float = 0.0F
    private var elevator: Float = 0.0F
    private var throttle: Float = 0.0F
    private var rudder: Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screenshot)
        // Amits:
        val seekBar1Text = findViewById<TextView>(R.id.rudderText)
        val seekBar1 = findViewById<SeekBar>(R.id.rudder)
        val joyStickX = findViewById<TextView>(R.id.joyStickX)
        val joyStickY = findViewById<TextView>(R.id.joyStickY)
        setJoystick()
        seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                val rudder1 = p1.toFloat()
                rudder = (rudder1 / 100)
                seekBar1Text.text = rudder.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
            }
        })
        getScreenShot()
        // Amit Ends.
//        lets_go_button.setOnClickListener{
//            getScreenShot()
//        }
    }

    private fun setJoystick() {
        var changeFlag = false
        val joystick = joystickView.right
        joystickView.setOnMoveListener { angle, strength ->
            val jsX = joystickView.normalizedX.toFloat()
            val jsY = joystickView.normalizedY.toFloat()
            joyStickX.text = ((jsX - 50) / 100).toString()
            joyStickY.text = ((jsY - 50) / 100).toString()


            val toSetElavtor = (kotlin.math.cos(Math.toRadians(angle.toDouble())) * strength / 100).toFloat()
            val toSetAileron = (kotlin.math.sin(Math.toRadians(angle.toDouble())) * strength / 100).toFloat()

            if ((toSetElavtor > 1.01 * elevator) || (toSetElavtor < 0.99 * elevator)) {
                changeFlag = true
                elevator = toSetElavtor
            }
            if ((toSetAileron > 1.01 * aileron) || (toSetAileron < 0.99 * aileron)) {
                changeFlag = true
                aileron = toSetAileron
            }

            if (changeFlag) {
//                //sends the changes
                sendCommand()
                //sends the changes
//                CoroutineScope(Dispatchers.IO).launch {
//                    sendCommand()
//                }
                changeFlag = false
            }
        }
    }

    private fun setSeekBar(seekBar: SeekBar) {

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }


    fun getScreenShot() {

        val intent = getIntent()
        val myUrl = intent.getStringExtra("myUrl")
        val name = myUrl
        val gson = GsonBuilder()
            .setLenient()
            .create()
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:37819/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(name)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                println(name)
                delay(250)
                var body = api.getImg().enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                            val I = response?.body()?.byteStream()
                            val B = BitmapFactory.decodeStream(I)
                            runOnUiThread {
                                image.setImageBitmap(B)
                            }
                        }else{
                            val toast = Toast.makeText(applicationContext, "Delay Getting Screenshot",Toast.LENGTH_SHORT)
                            //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                            toast.show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect",Toast.LENGTH_SHORT)
                        //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                        toast.show()
                    }
                })
            }
        }
    }

//    fun sendCommand() {
//        val intent = getIntent()
//        val myUrl = intent.getStringExtra("myUrl")
//        val name = myUrl
//        println("aileron:$aileron, rudder:$rudder, elevator:$elevator, throttle:$throttle")
//        val json =
//            "{\"aileron\": $aileron,\n \"elevator\": $elevator,\n \"rudder\": $rudder,\n \"throttle\": $throttle\n}"
//        val requestBody = RequestBody.create(MediaType.parse("application/json"),json)
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
////            val retrofit = Retrofit.Builder()
////                .baseUrl("http://127.0.0.1:50931/")
////                .addConverterFactory(GsonConverterFactory.create(gson))
////                .build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(name)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//        val api = retrofit.create(Api::class.java)
//        println(name)
//        var body = api.sendCommand(requestBody).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//                if (response.code() !in 400..598) {
//                    // ok response
//                    var string = "Server ok: " + response.code().toString() + ", " +
//                            response.message()
//                    println(string)
//                } else {
//                    var string = "Server error: " + response.code().toString() + ", " +
//                            response.message()
//                    println(string)
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                //connectFlag = false
//                //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
//                Toast.makeText(applicationContext, "Server failed", Toast.LENGTH_LONG).show()
//                println(t.message)
//            }
//        })
//    }

    fun sendCommand() {
        val intent = getIntent()
        val myUrl = intent.getStringExtra("myUrl")
        val name = myUrl
        println("throttle:$throttle, rudder:$rudder, aileron:$aileron, elevator:$elevator")
        val command = Command(
            aileron / 100.0, elevator / 100.0,
            rudder / 100.0, throttle / 100.0
        )
        val gson = GsonBuilder()
            .setLenient()
            .create()
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:4659/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(name)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        var body = api.sendCommand(command).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() !in 400..598) {
                    // ok response
                } else {
                    val toast = Toast.makeText(applicationContext, "Delay getting feedback from simulator",Toast.LENGTH_SHORT)
                    //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                    toast.show()
                    var string = "Server error: " + response.code().toString() + ", " +
                            response.message()
                    println(string)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //connectFlag = false
                //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect",Toast.LENGTH_SHORT)
                //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                toast.show()
                println(t.message)
            }
        })
    }
}