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
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dlg_set_alarm1.*
import kotlinx.android.synthetic.main.dlg_set_alarm1.view.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.jvm.Throws
import kotlin.random.Random

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

//    private var repeatJob1 = null
    private lateinit var repeatJob1 : Job
    private lateinit var repeatJob2 : Job
    private lateinit var repeatJob3 : Job

    var m_saveCoinName1: String = ""
    var m_saveCoinName2: String = ""
    var m_saveCoinName3: String = ""
    var m_saveCoinPrice1: String = "0"
    var m_saveCoinPrice2: String = "0"
    var m_saveCoinPrice3: String = "0"
    var m_saveCoinCondition1: String = ""
    var m_saveCoinCondition2: String = ""
    var m_saveCoinCondition3: String = ""

    var ringtone: Ringtone? = null
    var vibrator: Vibrator? = null



    override fun getResources(): Resources {
        return super.getResources()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ringtone setting
        val packageName: String = "com.jooplayconsole.upbitalarmprototype"
        val uriRingtone = Uri.parse("android.resource://"+ packageName + "/" + R.raw.ringtone1)
        ringtone = RingtoneManager.getRingtone(this, uriRingtone)

        Log.d("[init2]", "coinNm2:"+MyApp.prefs.getString("pf_coinName2", "empty"))
        Log.d("[init2]", "coinPrice2:"+MyApp.prefs.getString("pf_coinPrice2", "empty"))
        Log.d("[init2]", "coinCondition2:"+MyApp.prefs.getString("pf_coinCondition2", "empty"))

        Log.d("[init3]", "coinNm3:"+MyApp.prefs.getString("pf_coinName3", "empty"))
        Log.d("[init3]", "coinPrice3:"+MyApp.prefs.getString("pf_coinPrice3", "empty"))
        Log.d("[init3]", "coinCondition3:"+MyApp.prefs.getString("pf_coinCondition3", "empty"))

        //init sharedPreference var
        if(MyApp.prefs.getString("pf_coinName1", "empty") != "empty") {
            m_saveCoinName1 = MyApp.prefs.getString("pf_coinName1", "empty")
            m_saveCoinPrice1 = MyApp.prefs.getString("pf_coinPrice1", "empty")

            if(MyApp.prefs.getString("pf_coinCondition1", "empty").equals("이상일 때")) {
                textView1.text = "$m_saveCoinName1 : ₩$m_saveCoinPrice1↑"
            }
            else {
                textView1.text = "$m_saveCoinName1 : ₩$m_saveCoinPrice1↓"
            }
        }

        if(MyApp.prefs.getString("pf_coinName2", "empty") != "empty") {
            m_saveCoinName2 = MyApp.prefs.getString("pf_coinName2", "empty")
            m_saveCoinPrice2 = MyApp.prefs.getString("pf_coinPrice2", "empty")

            if(MyApp.prefs.getString("pf_coinCondition2", "empty").equals("이상일 때")) {
                textView2.text = "$m_saveCoinName2 : ₩$m_saveCoinPrice2↑"
            }
            else {
                textView2.text = "$m_saveCoinName2 : ₩$m_saveCoinPrice2↓"
            }
        }

        if(MyApp.prefs.getString("pf_coinName3", "empty") != "empty") {
            m_saveCoinName3 = MyApp.prefs.getString("pf_coinName3", "empty")
            m_saveCoinPrice3 = MyApp.prefs.getString("pf_coinPrice3", "empty")

            if(MyApp.prefs.getString("pf_coinCondition3", "empty").equals("이상일 때")) {
                textView3.text = "$m_saveCoinName3 : ₩$m_saveCoinPrice3↑"
            }
            else {
                textView3.text = "$m_saveCoinName3 : ₩$m_saveCoinPrice3↓"
            }
        }

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
//                networking(upbitUrlHead + coinNm1 + upbitUrlTail)   //test code
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
        //hh.joo 20210509

        //alarm add 1   //  btn_add_1   textView1   btn_clear_1
        btn_add_1.setOnClickListener {
            Log.d("btn_add_1", "btn_add_1 > #1")
            alertDlg1.show()
            Log.d("btn_add_1", "btn_add_1 > #finish")
        }

        //설정
        dlg1.btn_positive.setOnClickListener {
            Log.d("미선택 > ", "$m_saveCoinName1") //선택안해도 1번으로 선택되어져 있음
            MyApp.prefs.setString("pf_coinName1", m_saveCoinName1)
            m_saveCoinPrice1 = dlg1.coinEdit_price.text.toString()
            MyApp.prefs.setString("pf_coinCondition1", m_saveCoinCondition1)

            if(m_saveCoinPrice1.equals("")) {
                m_saveCoinPrice1 = "0"
                MyApp.prefs.setString("pf_coinPrice1", m_saveCoinPrice1)
            }
            else {
                dlg1.coinEdit_price.setText(m_saveCoinPrice1)
                MyApp.prefs.setString("pf_coinPrice1", m_saveCoinPrice1)
            }

//            textView1.text = "$m_saveCoinName1/₩$m_saveCoinPrice1/$m_saveCoinCondition1"
            if(m_saveCoinCondition1.equals("이상일 때")) {
                textView1.text = "$m_saveCoinName1 : ₩$m_saveCoinPrice1↑"
            }
            else {
                textView1.text = "$m_saveCoinName1 : ₩$m_saveCoinPrice1↓"
            }

            repeatJob1 = repeatHttpUpbitPost(upbitUrlHead + m_saveCoinName1 + upbitUrlTail)

            alertDlg1.dismiss()
        }

        //취소
        dlg1.btn_negative.setOnClickListener {
            alertDlg1.dismiss()
        }

        //clear
        btn_clear_1.setOnClickListener {
            Log.d("btn_claer_1 > ", "clicked!")
            textView1.text = "코인알람을 설정해 주세요."
            MyApp.prefs.setString("pf_coinName1", "empty")
            MyApp.prefs.setString("pf_coinCondition1", "empty")
            MyApp.prefs.setString("pf_coinPrice1", "empty")

            if(::repeatJob1.isInitialized) {
                repeatJob1.cancel()
            }

            ringtone?.run {
                stop()
            }
        }
        /* 1st Coin Alarm Setting End*/

        /* 2nd Coin Alarm Setting */
        val dlg2 : View = layoutInflater.inflate(R.layout.dlg_set_alarm2, null)
        mSpCoinName2 = dlg2.findViewById(R.id.coinSpinner_name2)
        val adapterCoinNm2 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
        mSpCoinName2.adapter = adapterCoinNm2
        adapterCoinNm2.notifyDataSetChanged()

        mSpCoinCond2 = dlg2.findViewById(R.id.coinSpinner_condition2)
        val adapterCoinCond2 = ArrayAdapter.createFromResource(this, R.array.coin_condition, android.R.layout.simple_spinner_item)
        mSpCoinCond2.adapter = adapterCoinCond2
        adapterCoinCond2.notifyDataSetChanged()

        alertDialogBuilder2 = AlertDialog.Builder(this)
        alertDialogBuilder2.setView(dlg2)
        alertDlg2 = alertDialogBuilder2.create()
        dlg2.text_title.text = m_dlg_alarm_title
        //text_title.setTextColor(R.color.black)
        dlg2.btn_positive.text = "설정"
        dlg2.btn_negative.text = "닫기"
        dlg2.text_description_coin_name.text = "코인이름"
        dlg2.text_description_alarm_price.text = "코인가격"
        dlg2.text_description_condition.text = "조건"

        mSpCoinName2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var strNm2 = mSpCoinName2.getItemAtPosition(position).toString()
                var idxInit2 = strNm2.indexOf("_")
                var coinNm2 = strNm2.substring(idxInit2+1, strNm2.length)
                m_saveCoinName2 = coinNm2
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mSpCoinCond2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var strCondition = mSpCoinCond2.getItemAtPosition(position).toString()
                m_saveCoinCondition2 = strCondition
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        btn_add_2.setOnClickListener {
            alertDlg2.show()
        }
        dlg2.btn_positive.setOnClickListener {
            MyApp.prefs.setString("pf_coinName2", m_saveCoinName2)
            m_saveCoinPrice2 = dlg2.coinEdit_price.text.toString()
            MyApp.prefs.setString("pf_coinCondition2", m_saveCoinCondition2)

            if(m_saveCoinPrice2.equals("")) {
                m_saveCoinPrice2 = "0"
                MyApp.prefs.setString("pf_coinPrice2", m_saveCoinPrice2)
            }
            else {
                dlg2.coinEdit_price.setText(m_saveCoinPrice2)
                MyApp.prefs.setString("pf_coinPrice2", m_saveCoinPrice2)
            }

            if(m_saveCoinCondition2.equals("이상일 때")) {
                textView2.text = "$m_saveCoinName2 : ₩$m_saveCoinPrice2↑"
            }
            else {
                textView2.text = "$m_saveCoinName2 : ₩$m_saveCoinPrice2↓"
            }

            repeatJob2 = repeatHttpUpbitPost2(upbitUrlHead + m_saveCoinName2 + upbitUrlTail)

            alertDlg2.dismiss()
        }
        dlg2.btn_negative.setOnClickListener {
            alertDlg2.dismiss()
        }
        btn_clear_2.setOnClickListener {
            textView2.text = "코인알람을 설정해 주세요."
            MyApp.prefs.setString("pf_coinName2", "empty")
            MyApp.prefs.setString("pf_coinCondition2", "empty")
            MyApp.prefs.setString("pf_coinPrice2", "empty")

            if(::repeatJob2.isInitialized) {
                repeatJob2.cancel()
            }

            ringtone?.run {
                stop()
            }
        }
        /* 2nd Coin Alarm Setting End*/


        /* 3nd Coin Alarm Setting*/
        val dlg3 : View = layoutInflater.inflate(R.layout.dlg_set_alarm3, null)
        mSpCoinName3 = dlg3.findViewById(R.id.coinSpinner_name3)
        val adapterCoinNm3 = ArrayAdapter.createFromResource(this, R.array.coin_array, android.R.layout.simple_spinner_item)
        mSpCoinName3.adapter = adapterCoinNm3
        adapterCoinNm3.notifyDataSetChanged()

        mSpCoinCond3 = dlg3.findViewById(R.id.coinSpinner_condition3)
        val adapterCoinCond3 = ArrayAdapter.createFromResource(this, R.array.coin_condition, android.R.layout.simple_spinner_item)
        mSpCoinCond3.adapter = adapterCoinCond3
        adapterCoinCond3.notifyDataSetChanged()

        alertDialogBuilder3 = AlertDialog.Builder(this)
        alertDialogBuilder3.setView(dlg3)
        alertDlg3 = alertDialogBuilder3.create()
        dlg3.text_title.text = m_dlg_alarm_title
        //text_title.setTextColor(R.color.black)
        dlg3.btn_positive.text = "설정"
        dlg3.btn_negative.text = "닫기"
        dlg3.text_description_coin_name.text = "코인이름"
        dlg3.text_description_alarm_price.text = "코인가격"
        dlg3.text_description_condition.text = "조건"

        mSpCoinName3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var strNm3 = mSpCoinName3.getItemAtPosition(position).toString()
                var idxInit3 = strNm3.indexOf("_")
                var coinNm3 = strNm3.substring(idxInit3+1, strNm3.length)
                m_saveCoinName3 = coinNm3
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mSpCoinCond3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var strCondition = mSpCoinCond3.getItemAtPosition(position).toString()
                m_saveCoinCondition3 = strCondition
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        btn_add_3.setOnClickListener {
            alertDlg3.show()
        }
        dlg3.btn_positive.setOnClickListener {
            MyApp.prefs.setString("pf_coinName3", m_saveCoinName3)
            m_saveCoinPrice3 = dlg3.coinEdit_price.text.toString()
            MyApp.prefs.setString("pf_coinCondition3", m_saveCoinCondition3)

            if(m_saveCoinPrice3.equals("")) {
                m_saveCoinPrice3 = "0"
                MyApp.prefs.setString("pf_coinPrice3", m_saveCoinPrice3)
            }
            else {
                dlg3.coinEdit_price.setText(m_saveCoinPrice3)
                MyApp.prefs.setString("pf_coinPrice3", m_saveCoinPrice3)
            }

            if(m_saveCoinCondition3.equals("이상일 때")) {
                textView3.text = "$m_saveCoinName3 : ₩$m_saveCoinPrice3↑"
            }
            else {
                textView3.text = "$m_saveCoinName3 : ₩$m_saveCoinPrice3↓"
            }

            repeatJob3 = repeatHttpUpbitPost3(upbitUrlHead + m_saveCoinName3 + upbitUrlTail)

            alertDlg3.dismiss()
        }
        dlg3.btn_negative.setOnClickListener {
            alertDlg3.dismiss()
        }
        btn_clear_3.setOnClickListener {
            textView3.text = "코인알람을 설정해 주세요."
            MyApp.prefs.setString("pf_coinName3", "empty")
            MyApp.prefs.setString("pf_coinCondition3", "empty")
            MyApp.prefs.setString("pf_coinPrice3", "empty")

            if(::repeatJob3.isInitialized) {
                repeatJob3.cancel()
            }

            ringtone?.run {
                stop()
            }
        }
        /* 3nd Coin Alarm Setting End*/

        ////////////////////////////////////////////////////////////////////////////////////////////

        //ringtone
//        val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        val packageName = "com.jooplayconsole.upbitalarmprototype"
//        val uriRingtone = Uri.parse("android.resource://"+ packageName + "/" + R.raw.ringtone1)
//        ringtone = RingtoneManager.getRingtone(this, uriRingtone)

        //btn_test1
        btn_test1.setOnClickListener {

            ringtone?.run {
                if (!isPlaying) play()
                else stop()
            }

        }

        btn_test2.setOnClickListener {

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
//            Thread(Runnable {
//                Thread.sleep(2000)
//                Log.d("Runnable", "runnable start()!")
//                runOnUiThread {     //ui 관련 조작 Thread안에서    //Main Thread 안에서 돌아간다
//
//                }
//            }).start()

    }

    // CoroutineScope example
//    val job = Job()
//    val uiContext = job + Dispatchers.Main
//    val bgContext = job + Dispatchers.Default
//
//    val bgScope = CoroutineScope(bgContext)
//    val uiScope = CoroutineScope(uiContext)
//
//    fun fetch() {
//        uiScope.launch {
//            withContext(bgScope.coroutineContext) {
//                doNetworking("https://api.upbit.com/v1/candles/minutes/1?market=KRW-BTC&count=1")
//            }
//        }
//    }

    fun repeatHttpUpbitPost(url: String) : Job {
        return GlobalScope.launch {
            while(true) {
                var curCoinPrice = httpUpbitPost(url)
                var chkCoinPrice : Int = curCoinPrice.toInt()
                delay(2000L)
                Log.d("[repeatHttpUpbitPost]", "########################### $curCoinPrice")

                //1
                if(!MyApp.prefs.getString("pf_coinName1", "empty").equals("empty")) {
                    var pfCoinName1 = MyApp.prefs.getString("pf_coinName1", "empty")
                    var pfCoinCondition1 = MyApp.prefs.getString("pf_coinCondition1", "empty")
                    var pfCoinPrice1 = MyApp.prefs.getString("pf_coinPrice1", "empty").toInt()

                    if(pfCoinCondition1.equals("이상일 때")) {
                        if(chkCoinPrice >= pfCoinPrice1) {   //up condition
                            showNotification("업비트 파워알람", "$pfCoinName1 : $pfCoinPrice1 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob1.isInitialized) {
                                repeatJob1.cancel()
                            }
                        }
                    }
                    else {
                        if(chkCoinPrice <= pfCoinPrice1) {   //down condition
                            showNotification("업비트 파워알람", "$pfCoinName1 : $pfCoinPrice1 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob1.isInitialized) {
                                repeatJob1.cancel()
                            }
                        }
                    }
                }
                //2
                if(!MyApp.prefs.getString("pf_coinName2", "empty").equals("empty")) {
                    var pfCoinName2 = MyApp.prefs.getString("pf_coinName2", "empty")
                    var pfCoinCondition2 = MyApp.prefs.getString("pf_coinCondition2", "empty")
                    var pfCoinPrice2 = MyApp.prefs.getString("pf_coinPrice2", "empty").toInt()

                    if(pfCoinCondition2.equals("이상일 때")) {
                        if(chkCoinPrice >= pfCoinPrice2) {   //up condition
                            showNotification("업비트 파워알람", "$pfCoinName2 : $pfCoinPrice2 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob2.isInitialized) {
                                repeatJob2.cancel()
                            }
                        }
                    }
                    else {
                        if(chkCoinPrice <= pfCoinPrice2) {   //down condition
                            showNotification("업비트 파워알람", "$pfCoinName2 : $pfCoinPrice2 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob2.isInitialized) {
                                repeatJob2.cancel()
                            }
                        }
                    }
                }
                //3
                if(!MyApp.prefs.getString("pf_coinName3", "empty").equals("empty")) {
                    var pfCoinName3 = MyApp.prefs.getString("pf_coinName3", "empty")
                    var pfCoinCondition3 = MyApp.prefs.getString("pf_coinCondition3", "empty")
                    var pfCoinPrice3 = MyApp.prefs.getString("pf_coinPrice3", "empty").toInt()

                    if(pfCoinCondition3.equals("이상일 때")) {
                        if(chkCoinPrice >= pfCoinPrice3) {   //up condition
                            showNotification("업비트 파워알람", "$pfCoinName3 : $pfCoinPrice3 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob3.isInitialized) {
                                repeatJob3.cancel()
                            }
                        }
                    }
                    else {
                        if(chkCoinPrice <= pfCoinPrice3) {   //down condition
                            showNotification("업비트 파워알람", "$pfCoinName3 : $pfCoinPrice3 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob3.isInitialized) {
                                repeatJob3.cancel()
                            }
                        }
                    }
                }

            }
        }
    }

    fun repeatHttpUpbitPost2(url: String) : Job {
        return GlobalScope.launch {
            while(true) {
                var curCoinPrice = httpUpbitPost(url)
                var chkCoinPrice : Int = curCoinPrice.toInt()
                delay(2000L)

                //2
                if(!MyApp.prefs.getString("pf_coinName2", "empty").equals("empty")) {
                    var pfCoinName2 = MyApp.prefs.getString("pf_coinName2", "empty")
                    var pfCoinCondition2 = MyApp.prefs.getString("pf_coinCondition2", "empty")
                    var pfCoinPrice2 = MyApp.prefs.getString("pf_coinPrice2", "empty").toInt()

                    if(pfCoinCondition2.equals("이상일 때")) {
                        if(chkCoinPrice >= pfCoinPrice2) {   //up condition
                            showNotification("업비트 파워알람", "$pfCoinName2 : $pfCoinPrice2 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob2.isInitialized) {
                                repeatJob2.cancel()
                            }
                        }
                    }
                    else {
                        if(chkCoinPrice <= pfCoinPrice2) {   //down condition
                            showNotification("업비트 파워알람", "$pfCoinName2 : $pfCoinPrice2 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob2.isInitialized) {
                                repeatJob2.cancel()
                            }
                        }
                    }
                }
            }
        }
    }

    fun repeatHttpUpbitPost3(url: String) : Job {
        return GlobalScope.launch {
            while(true) {
                var curCoinPrice = httpUpbitPost(url)
                var chkCoinPrice : Int = curCoinPrice.toInt()
                delay(2000L)

                //3
                if(!MyApp.prefs.getString("pf_coinName3", "empty").equals("empty")) {
                    var pfCoinName3 = MyApp.prefs.getString("pf_coinName3", "empty")
                    var pfCoinCondition3 = MyApp.prefs.getString("pf_coinCondition3", "empty")
                    var pfCoinPrice3 = MyApp.prefs.getString("pf_coinPrice3", "empty").toInt()

                    if(pfCoinCondition3.equals("이상일 때")) {
                        if(chkCoinPrice >= pfCoinPrice3) {   //up condition
                            showNotification("업비트 파워알람", "$pfCoinName3 : $pfCoinPrice3 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob3.isInitialized) {
                                repeatJob3.cancel()
                            }
                        }
                    }
                    else {
                        if(chkCoinPrice <= pfCoinPrice3) {   //down condition
                            showNotification("업비트 파워알람", "$pfCoinName3 : $pfCoinPrice3 !!")
                            ringtone?.run {
                                play()
                            }
                            if(::repeatJob3.isInitialized) {
                                repeatJob3.cancel()
                            }
                        }
                    }
                }

            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    suspend fun httpUpbitPost(url: String) : Int {
        var tradePrice: Int = 0

        withContext(Dispatchers.IO) {
            try {
                val url = URL(url)
//                val url = URL("https://api.upbit.com/v1/candles/minutes/1?market=KRW-BTC&count=1")    //for test

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

//                    val jsonObj = JSONArray(jsonStr)
//                    val curPrice = jsonObj.getString(0)
                    val jArray = JSONArray(jsonStr)

                    for (i in 0 until jArray.length()) {
                        val obj = jArray.getJSONObject(i)
                        val market = obj.getString("market")
                        tradePrice = obj.getInt("trade_price")
                        Log.d("[MainActivity]", "market($i) > $market")
                        Log.d("[MainActivity]", "market($i) > $tradePrice")
                    }

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

        return tradePrice  //return coin price
    }


    suspend fun doNetworking(url: String) = withContext(Dispatchers.IO) {
        /*perform network IO here*/
        try {
            val url = URL(url)
//            val url = URL("https://api.upbit.com/v1/candles/minutes/1?market=KRW-BTC&count=1")

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

    private fun checkNetworkConnection(): Boolean {
        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connMgr.activeNetworkInfo
        val isConnected: Boolean = if(networkInfo != null) networkInfo.isConnected() else false
        if (networkInfo != null && isConnected) {
            // show "Connected" & type of network "WIFI or MOBILE"
            //tvIsConnected.text = "Connected " + networkInfo.typeName
            // change background color to red
            //tvIsConnected.setBackgroundColor(-0x8333da)
            Toast.makeText(this, "Connected!!", Toast.LENGTH_SHORT).show()

        } else {
            // show "Not Connected"
            //tvIsConnected.text = "Not Connected"
            // change background color to green
            //tvIsConnected.setBackgroundColor(-0x10000)
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show()
        }

        return isConnected
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

    //Alarm 호출 test code
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

    fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = this.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    private fun notiCoinAlarm() {
        Log.d("notiCoinAlarm", "#1")
        createNotificationChannel()
        Log.d("notiCoinAlarm", "#2")

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        Log.d("notiCoinAlarm", "#3")

        val builder = NotificationCompat.Builder(this, "chUpbitNoti")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("업비트 파워알람")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = getString()
            //val descriptionText = getString("업비트 코인알림")
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("upbitNoti", "upbit", importance).apply {
                description = "업비트 코인알림"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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