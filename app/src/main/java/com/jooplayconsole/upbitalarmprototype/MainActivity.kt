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
import kotlinx.android.synthetic.main.dlg_set_alarm1.view.*
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

    private lateinit var mSpCoinCond1: Spinner
    private lateinit var mSpCoinCond2: Spinner
    private lateinit var mSpCoinCond3: Spinner

//    private var mSpCoinCond1: Spinner? = null
//    private var mSpCoinCond2: Spinner? = null
//    private var mSpCoinCond3: Spinner? = null

    private lateinit var dlgSetAlarm1Binding : SaveCoinAlarmDlg1
    private lateinit var dlgSetAlarm2Binding : SaveCoinAlarmDlg2
    private lateinit var dlgSetAlarm3Binding : SaveCoinAlarmDlg3

    private lateinit var alertDialogBuilder1 : AlertDialog.Builder  //alertDialog
    private lateinit var alertDlg1 : AlertDialog        //dialog
    private lateinit var alertDialogBuilder2 : AlertDialog.Builder  //alertDialog
    private lateinit var alertDlg2 : AlertDialog        //dialog
    private lateinit var alertDialogBuilder3 : AlertDialog.Builder  //alertDialog
    private lateinit var alertDlg3 : AlertDialog        //dialog

    //exit by double tap
    private var FINISH_INTERVAL_TIME: Long = 1500
    private var backPressedTime: Long = 0
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //
    val num=1
    val num2 =2
    val upbitUrlHead = "https://api.upbit.com/v1/candles/minutes/1?market=KRW-"
    val upbitUrlTail = "&count=1"
    val m_dlg_alarm_title = "코인 알람 설정하기"

    var m_saveCoinName1: String = ""
    var m_saveCoinName2: String = ""
    var m_saveCoinName3: String = ""
    var m_saveCoinPrice1: String = "0"
    var m_saveCoinPrice2: String = "0"
    var m_saveCoinPrice3: String = "0"
    var m_saveCoinCondition1: String = ""
    var m_saveCoinCondition2: String = ""
    var m_saveCoinCondition3: String = ""

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

        /* 1st Coin Alarm Setting */
        val dlg1 : View = layoutInflater.inflate(R.layout.dlg_set_alarm1, null)
        mSpCoinName1 = dlg1.findViewById(R.id.coinSpinner_name1)
        val coinNameArrData = resources.getStringArray(R.array.coin_array)
        val adapterCoinNm1 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
        mSpCoinName1.adapter = adapterCoinNm1
        adapterCoinNm1.notifyDataSetChanged()

        mSpCoinCond1 = dlg1.findViewById(R.id.coinSpinner_condition1)
        val coinCondArrData = resources.getStringArray(R.array.coin_condition)
        val adapterCoinCond1 = ArrayAdapter.createFromResource(this, R.array.coin_condition, android.R.layout.simple_spinner_item)
        mSpCoinCond1.adapter = adapterCoinCond1
        adapterCoinCond1.notifyDataSetChanged()

        alertDialogBuilder1 = AlertDialog.Builder(this)
        alertDialogBuilder1.setView(dlg1)
        alertDlg1 = alertDialogBuilder1.create()
        dlg1.text_title.text = m_dlg_alarm_title
        //text_title.setTextColor(R.color.black)
        dlg1.btn_positive.text = "설정"
        dlg1.btn_negative.text = "닫기"
        dlg1.text_description_coin_name.text = "코인이름"
        dlg1.text_description_alarm_price.text = "코인가격"
        dlg1.text_description_condition.text = "조건"

        mSpCoinName1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("mSpCoinName1 > ", ""+mSpCoinName1.getItemAtPosition(position))       //
                var strNm1 = mSpCoinName1.getItemAtPosition(position).toString()
                var idxInit1 = strNm1.indexOf("_")
                var coinNm1 = strNm1.substring(idxInit1+1, strNm1.length)
                m_saveCoinName1 = coinNm1
                Log.d("mSpCoinName1 > ", "$coinNm1")
                networking(upbitUrlHead + coinNm1 + upbitUrlTail)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mSpCoinCond1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var strCondition = mSpCoinCond1.getItemAtPosition(position).toString()
                m_saveCoinCondition1 = strCondition
                Log.d("mSpCoinCond1 > ", "$strCondition")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        /* 1st Coin Alarm Setting End*/
        //hh.joo 20210509 end

        //alarm add 1   //  btn_add_1   textView1   btn_clear_1
        btn_add_1.setOnClickListener {
            Log.d("btn_add_1", "btn_add_1 > #1")
            alertDlg1.show()
            Log.d("btn_add_1", "btn_add_1 > #finish")
        }

        dlg1.btn_positive.setOnClickListener {
            Log.d("미선택 > ", "$m_saveCoinName1") //선택안해도 1번으로 선택되어져 있음
            MyApp.prefs.setString("pf_coinName1", m_saveCoinName1)
            m_saveCoinPrice1 = dlg1.coinEdit_price1.text.toString()
            MyApp.prefs.setString("pf_coinCondition1", m_saveCoinCondition1)

            if(m_saveCoinPrice1.equals("")) {
                m_saveCoinPrice1 = "0"
                MyApp.prefs.setString("pf_coinPrice1", m_saveCoinPrice1)
            }
            else {
                dlg1.coinEdit_price1.setText(m_saveCoinPrice1)
                MyApp.prefs.setString("pf_coinPrice1", m_saveCoinPrice1)
            }

            textView1.text = "코인심볼:$m_saveCoinName1 / 코인가격:$m_saveCoinPrice1 / 조건:$m_saveCoinCondition1"
            alertDlg1.dismiss()
        }
        dlg1.btn_negative.setOnClickListener {

            var strLog = MyApp.prefs.getString("pf_coinName1", "empty") //test
            Log.d("pref_Test > ", strLog)   //test
            alertDlg1.dismiss()

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

        //Thread exam1
//        val runnable: Runnable = object : Runnable {
//            override fun run() {
//                Log.d("Runnable", "runnable start()!")
//            }
//        }
//        val thread: Thread = Thread(runnable)

        //Thread exam2
//        Thread(object : Runnable {
//            override fun run() {
//
//            }
//        }).start()

        //Thread exam3
        Thread(Runnable {
            runOnUiThread {     //ui 관련 조작 Thread안에서    //Main Thread 안에서 돌아간다

            }
        }).start()

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