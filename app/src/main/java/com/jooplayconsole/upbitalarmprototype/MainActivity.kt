package com.jooplayconsole.upbitalarmprototype

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.jooplayconsole.upbitalarmprototype.databinding.DlgSetAlarm1Binding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dlg_set_alarm1.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var mSpCoinName1: Spinner
    private lateinit var mSpCoinName2: Spinner
    private lateinit var mSpCoinName3: Spinner

    private var mSpCoinCond1: Spinner? = null
    private var mSpCoinCond2: Spinner? = null
    private var mSpCoinCond3: Spinner? = null

    private lateinit var dlgSetAlarm1Binding : SaveCoinAlarmDlg1
    private lateinit var dlgSetAlarm2Binding : SaveCoinAlarmDlg2
    private lateinit var dlgSetAlarm3Binding : SaveCoinAlarmDlg3

    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private var FINISH_INTERVAL_TIME: Long = 1500
    private var backPressedTime: Long = 0
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val num=1
    val num2 =2

    override fun getResources(): Resources {
        return super.getResources()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hh.joo 20210509
//        val coinNameArrData = resources.getStringArray(R.array.coin_array)
//        val adapter1 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
//        val spinner1: Spinner = findViewById(R.id.coinSpinner_name1)
//        spinner1.adapter = adapter1
//        Log.d("MainActivity_onCreate", "adapter1 > $adapter1")

        //practice!
//        val dlg1 : View = layoutInflater.inflate(R.layout.dlg_set_alarm1, null)
//        mSpCoinName1 = dlg1.findViewById(R.id.coinSpinner_name1)
//
//        val coinNameArrData = resources.getStringArray(R.array.coin_array)
//        val adapter1 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
//        mSpCoinName1.adapter = adapter1
//        Log.d("MainActivity_onCreate", "adapter1 > $adapter1")
//        mSpCoinName1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Log.d("mSpCoinName1 > ", ""+mSpCoinName1.getItemAtPosition(position))
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//        }

        //practice2 ! ok !
//        alertDialog = AlertDialog.Builder(this)
//        val dlg1 : View = layoutInflater.inflate(R.layout.dlg_set_alarm1, null)
//        mSpCoinName1 = dlg1.findViewById(R.id.coinSpinner_name1)
//        val coinNameArrData = resources.getStringArray(R.array.coin_array)
//        val adapter1 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
//        mSpCoinName1.adapter = adapter1
//        adapter1.notifyDataSetChanged()
//        alertDialog.setView(dlg1)
//        dialog = alertDialog.create()
//        dialog.show()
//
//        Log.d("MainActivity_onCreate", "adapter1 > $adapter1")
//        mSpCoinName1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Log.d("mSpCoinName1 > ", ""+mSpCoinName1.getItemAtPosition(position))
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//        }

        val dlg1 : View = layoutInflater.inflate(R.layout.dlg_set_alarm1, null)
        mSpCoinName1 = dlg1.findViewById(R.id.coinSpinner_name1)
        val coinNameArrData = resources.getStringArray(R.array.coin_array)
        val adapter1 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
        mSpCoinName1.adapter = adapter1
        mSpCoinName1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("mSpCoinName1 > ", ""+mSpCoinName1.getItemAtPosition(position))       //
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //hh.joo 20210509 end

//        mSpCoinName1 = findViewById(R.id.coinSpinner_name) as Spinner
//        coinSpinner_condition1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                var tmp = coinSpinner_condition1.getItemAtPosition(position)
//                Log.d("sp_cond1", "#sp_cond1 > $tmp")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//        }

//        var dlgSetAlarm1Binding = DlgSetAlarm1Binding.inflate(layoutInflater)
//        dlgSetAlarm1Binding.coinSpinnerName
//        dlgSetAlarm1Binding = DlgSetAlarm1Binding.inflate(layoutInflater)
//        dlgSetAlarm1Binding = DlgSetAlarm1Binding.inflate(layoutInflater)

        //alarm add 1   //  btn_add_1   textView1   btn_clear_1
        btn_add_1.setOnClickListener {
            Log.d("btn_add_1", "btn_add_1 > #1")
            //TEST > SUCCESS!!!
            alertDialog = AlertDialog.Builder(this)
            adapter1.notifyDataSetChanged()
            alertDialog.setView(dlg1)
            dialog = alertDialog.create()
            dialog.show()

            Log.d("btn_add_1", "btn_add_1 > #finish")
        }

//        binding..setOnClickListener {
//            val dialog = SaveCoinAlarmDlg(this, "코인 알람 설정하기", "")
//            //showSaveCoinAlarmDlg()
//            showSaveCoinAlarmDlg1()
//        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        //btn_test1
        btn_test1.setOnClickListener {

        }

    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime
        if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
            finishAffinity()
            System.runFinalization()
            System.exit(0)
        } else {
            backPressedTime = tempTime
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        super.onBackPressed()
    }

    //Alarm 호출
    public fun callAlarm() {
        Log.d("CoinAlarmActivity", "CoinAlarmActivity show()!")

        var builder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("CoinAlarm Title")
            .setContentText("CoinAlarm detail text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var intent = Intent(this, MainActivity::class.java)

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(pendingIntent)

        var largeIcon : Bitmap
        largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        builder.setLargeIcon(largeIcon)
        builder.setColor(Color.RED)

        //RingtoneManager.TYPE_ALARM
//        val ringtoneUri : Uri = RingtoneManager.getActualDefaultRingtoneUri(this,
//            RingtoneManager.TYPE_ALARM)     //null : NullPointerException
//        builder.setSound(ringtoneUri)

        val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        builder.setSound(ringtoneUri)

        var vibrate = longArrayOf(0, 100, 200, 300)
        builder.setVibrate(vibrate)
        builder.setAutoCancel(true)

        val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        //오레오 버전 이상에서만
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //// Create the NotificationChannel
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("default", "defaultChannel", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "defaultChannel"

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(1, builder.build())  //return notification object by builder
    }

    //Alarm 해제
    public fun clearAlarm() {
        Log.d("CoinAlarmActivity", "CoinAlarmActivity hide()!")
//        NotificationCompat.from(this).cancel(1)
    }

    fun networking(urlString: String) {
        thread(start=true) {
            // 네트워킹 예외처리를 위한 try ~ catch 문
            try {
                val url = URL(urlString)

                // 서버와의 연결 생성
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    // 데이터 읽기
                    val streamReader = InputStreamReader(urlConnection.inputStream)
                    val buffered = BufferedReader(streamReader)

                    val content = StringBuilder()
                    while(true) {
                        val line = buffered.readLine() ?: break
                        content.append(line)
                    }

                    Log.d("[MainActivity]", "fun networking!!!")
                    Log.d("[MainActivity]", "content > $content")

                    val jsonStr = content.toString()
                    Log.d("[MainActivity:jsonStr]", "jsonString > $jsonStr")

//                    [
//                        {
//                            "market":"KRW-BTC",   1
//                            "candle_date_time_utc":"2021-04-26T13:49:00", 2
//                            "candle_date_time_kst":"2021-04-26T22:49:00", 3
//                            "opening_price":63486000.00000000,    4
//                            "high_price":63486000.00000000,   5
//                            "low_price":63481000.00000000,    6
//                            "trade_price":63484000.00000000,  7
//                            "timestamp":1619444952832,    8
//                            "candle_acc_trade_price":49435153.10161000,   9
//                            "candle_acc_trade_volume":0.77873530, 10
//                            "unit":1
//                        }
//                    ]

//                    val jsonObj = JSONArray(jsonStr)
//                    val curPrice = jsonObj.getString(0)
                    val jArray = JSONArray(jsonStr)
                    for (i in 0 until jArray.length()) {
                        val obj = jArray.getJSONObject(i)
                        val market = obj.getString("market")
                        val tradePrice = obj.getInt("trade_price")
                        Log.d("[MainActivity]", "market($i) > $market")
                        Log.d("[MainActivity]", "market($i) > $tradePrice")
                    }

//TEST
//                    val curPrice = JSONObject(jsonStr).getJSONArray("trade_price")
//                    val curPrice = JSONArray(jsonStr).
//                    Log.d("[MainActivity]", "curPrice > $curPrice")

                    // 스트림과 커넥션 해제
                    buffered.close()
                    urlConnection.disconnect()
                    runOnUiThread {
                        // UI 작업
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showSaveCoinAlarmDlg() {
        val dlg = SaveCoinAlarmDlg1(this, "코인 알람 설정하기", "text_description")
        dlg.show()
    }

    fun showSaveCoinAlarmDlg1() {
//        val dlg1 : View = layoutInflater.inflate(R.layout.dlg_set_alarm1, null)
//        mSpCoinName1 = dlg1.findViewById(R.id.coinSpinner_name1)
    }


}