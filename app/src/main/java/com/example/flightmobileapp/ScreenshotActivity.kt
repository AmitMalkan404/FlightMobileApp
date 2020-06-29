package com.example.flightmobileapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private lateinit var adapter: MyRecyclerViewAdapter
//    private var delaySendCommand : Boolean = false
//    private var lostConnWithSim : Boolean = false
    private var delaySendCommand : Int = 0
    private var delayGetCommand : Int = 0
    private var aileron : Float = 0.0F
    private var elevator: Float = 0.0F
    private var throttle: Float = 0.0F
    private var rudder: Float = 0.0F
    private var ScreenShotFlag : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screenshot)
        // Amits:
        val rudderText = findViewById<TextView>(R.id.rudderText)
        val rudderBar = findViewById<SeekBar>(R.id.rudder)
        val throttleText = findViewById<TextView>(R.id.throttleText)
        val throttleBar = findViewById<SeekBar>(R.id.throttle)
        val joyStickX = findViewById<TextView>(R.id.joyStickX)
        val joyStickY = findViewById<TextView>(R.id.joyStickY)
        setJoystick()
        rudderBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                val rudder1 = p1.toDouble()
                if(kotlin.math.abs((rudder1/100) - rudder) > 0.02){
                    rudder = (rudder1/100).toFloat()
                    rudderText.text = rudder.toString()
                    //rudder*=100
                    CoroutineScope(Dispatchers.IO).launch {
                        sendCommand()
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    sendCommand()
//                }
            }
        })
        throttleBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                val throttle1 = p1.toDouble()
                if(kotlin.math.abs((throttle1/100) - throttle) > 0.01) {
                    throttle = (throttle1/100).toFloat()
                    throttleText.text = throttle.toString()
                    //throttle*=100
                    CoroutineScope(Dispatchers.IO).launch {
                        sendCommand()
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    sendCommand()
//                }
            }
        })
        //getScreenShot()
    }
    private fun setJoystick() {
        var changeFlag = false
        val joystick = joystickView.right
        joystickView.setOnMoveListener{
                angle, strength ->
            val jsX = joystickView.normalizedX.toDouble()
            val jsY = joystickView.normalizedY.toDouble()
            joyStickX.text = ((jsX - 50)/50).toString()
            val y = (-(jsY - 50)/50)
            if (y == 0.0){
                joyStickY.text = "0.0"
            }
            else{
                joyStickY.text = y.toString()
            }
            val toSetElevator = (kotlin.math.sin(Math.toRadians(angle.toDouble())) * strength / 100).toFloat()
            val toSetAileron = (kotlin.math.cos(Math.toRadians(angle.toDouble())) * strength / 100).toFloat()

            if(kotlin.math.abs(toSetElevator - elevator) > 0.02) {
                changeFlag = true
                elevator = toSetElevator
            }
            if(kotlin.math.abs(toSetAileron - aileron) > 0.02) {
                changeFlag = true
                aileron = toSetAileron
            }
//            if((toSetElevator > 1.02 * elevator) || (toSetElevator < 0.98 * elevator)) {
//                changeFlag = true
//                elevator = toSetElevator
//            }
//            if((toSetAileron > 1.02 * aileron) || (toSetAileron < 0.98 * aileron)) {
//                changeFlag = true
//                aileron = toSetAileron
//            }

            if(changeFlag) {
                //sends the changes
//                CoroutineScope(Dispatchers.IO).launch {
//                    sendCommand()
//                    //delay(250)
//                }
                CoroutineScope(Dispatchers.IO).launch {
                    sendCommand()
                }
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
        ScreenShotFlag = false
    }
    override fun onResume() {
        super.onResume()
        ScreenShotFlag = true
        //getScreenShot()
        CoroutineScope(Dispatchers.IO).launch {
            getScreenShot()
        }
    }
    fun getScreenShot() {

        var isDelay = false
        var lostConn = false
        val intent = getIntent()
        val myUrl = intent.getStringExtra("myUrl")
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(myUrl).addConverterFactory(GsonConverterFactory.create(gson)).build()
        val api = retrofit.create(Api::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            //var int : Int = 0
            while (ScreenShotFlag) {
                //int+= 1
                //println(name)
                delay(1000)
                //println(int)
                var body = api.getImg().enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            delayGetCommand = 0
                            //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                            val I = response?.body()?.byteStream()
                            val B = BitmapFactory.decodeStream(I)
                            runOnUiThread {
                                image.setImageBitmap(B)
                            }
                        }else{
                            delayGetCommand+=1
                            if(delayGetCommand == 10){
                                val toast = Toast.makeText(applicationContext, "Delay Getting Screenshot",Toast.LENGTH_LONG)
                                //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                                toast.show()
                            }
                            if(delayGetCommand == 30){
                                val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect(get)",Toast.LENGTH_LONG)
                                //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                                toast.show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        delayGetCommand+=1
                        if(delayGetCommand == 20){
                            val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect(get)",Toast.LENGTH_LONG)
                            //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                            toast.show()
                        }
                    }
                })
            }
        }
    }
//    fun getScreenShot() {
//        var isDelay = false
//        var lostConn = false
//        val intent = getIntent()
//        val myUrl = intent.getStringExtra("myUrl")
//        val gson = GsonBuilder().setLenient().create()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(myUrl).addConverterFactory(GsonConverterFactory.create(gson)).build()
//        val api = retrofit.create(Api::class.java)
//        CoroutineScope(Dispatchers.IO).launch {
//            //var int : Int = 0
//            while (ScreenShotFlag) {
//                //int+= 1
//                //println(name)
//                delay(250)
//                //println(int)
//                var body = api.getImg().enqueue(object : Callback<ResponseBody> {
//                    override fun onResponse(
//                        call: Call<ResponseBody>,
//                        response: Response<ResponseBody>
//                    ) {
//                        lostConn = false
//                        if (response.isSuccessful) {
//                            isDelay = false
//                            //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
//                            val I = response?.body()?.byteStream()
//                            val B = BitmapFactory.decodeStream(I)
//                            runOnUiThread {
//                                image.setImageBitmap(B)
//                            }
//                        }else{
//                            if(!isDelay){
//                                isDelay = true
//                                val toast = Toast.makeText(applicationContext, "Delay Getting Screenshot",Toast.LENGTH_LONG)
//                                //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
//                                toast.show()
//                            }
//                        }
//                    }
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        isDelay = false
//                        if(!lostConn){
//                            lostConn = true
//                            val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect(get)",Toast.LENGTH_LONG)
//                            //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
//                            toast.show()
//                        }
//                    }
//                })
//            }
//        }
//    }
    fun sendCommand() {
        val intent = getIntent()
        val myUrl = intent.getStringExtra("myUrl")
        //println("throttle:$throttle, rudder:$rudder, aileron:$aileron, elevator:$elevator")
        val command = Command(aileron, elevator, rudder, throttle)
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(myUrl)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
        val api = retrofit.create(Api::class.java)
        var body = api.sendCommand(command).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() !in 400..598) {
                    delaySendCommand=0
                    // ok response
                } else {
                    delaySendCommand+=1
                    if(delaySendCommand==10){
                        val toast = Toast.makeText(applicationContext, "Delay getting feedback from simulator",Toast.LENGTH_SHORT)
                        //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                        toast.show()
                        //val string = "Server error: " + response.code().toString() + ", " + response.message()
                        //println(string)
                    }
                    if(delaySendCommand==50){
                        //connectFlag = false
                        //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                        val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect(command)",Toast.LENGTH_SHORT)
                        //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                        toast.show()
                        //println(t.message)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                delaySendCommand+=1
                if(delaySendCommand==50){
                    //connectFlag = false
                    //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                    val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect(command)",Toast.LENGTH_SHORT)
                    //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
                    toast.show()
                    //println(t.message)
                }
            }
        })
    }
//    fun sendCommand() {
//        val intent = getIntent()
//        val myUrl = intent.getStringExtra("myUrl")
//        //println("throttle:$throttle, rudder:$rudder, aileron:$aileron, elevator:$elevator")
//        val command = Command(aileron, elevator, rudder, throttle)
//        val gson = GsonBuilder().setLenient().create()
//        val retrofit = Retrofit.Builder().baseUrl(myUrl)
//            .addConverterFactory(GsonConverterFactory.create(gson)).build()
//        val api = retrofit.create(Api::class.java)
//        var body = api.sendCommand(command).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                lostConnWithSim = false
//                if (response.code() !in 400..598) {
//                    delaySendCommand = false
//                    // ok response
//                } else {
//                    if(!delaySendCommand){
//                        delaySendCommand = true
//                        val toast = Toast.makeText(applicationContext, "Delay getting feedback from simulator",Toast.LENGTH_SHORT)
//                        //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
//                        toast.show()
//                        //val string = "Server error: " + response.code().toString() + ", " + response.message()
//                        //println(string)
//                    }
//                }
//            }
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                if(!lostConnWithSim){
//                    lostConnWithSim = true
//                    //connectFlag = false
//                    //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
//                    val toast = Toast.makeText(applicationContext, "Lost connection with simulator\nPlease Reconnect(command)",Toast.LENGTH_SHORT)
//                    //toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 430,20)
//                    toast.show()
//                    //println(t.message)
//                }
//            }
//        })
//    }
}