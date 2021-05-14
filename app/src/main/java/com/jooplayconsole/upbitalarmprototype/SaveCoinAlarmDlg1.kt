package com.jooplayconsole.upbitalarmprototype

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jooplayconsole.upbitalarmprototype.databinding.DlgSetAlarm1Binding
import kotlinx.android.synthetic.main.dlg_set_alarm1.*

//import android.widget.ArrayAdapter as ArrayAdapter


class SaveCoinAlarmDlg1(context: Context, var title: String, var content: String) : Dialog(context) {

    private lateinit var dlgSetAlarm1Binding : SaveCoinAlarmDlg1

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_set_alarm1)

//        var dlg = layoutInflater.inflate(R.layout.dlg_set_alarm)
//        val coinPrice = dlg.findViewByid<EditText>(R.id.coinEdit_price)

        text_title.text = title
//        text_title.setTextColor(R.color.black)
        btn_positive.text = "설정"
        btn_negative.text = "닫기"
        text_description_coin_name.text = "코인이름"
        text_description_alarm_price.text = "코인가격"
        text_description_condition.text = "조건"


        val coinNameArrData = context.resources.getStringArray(R.array.coin_array)
//        ArrayAdapter.createFromResource(context, android.R.layout.simple_spinner_item, )

//        val spinner: Spinner = findViewById(R.id.coinSpinner_name)
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.coin_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner
//            spinner.adapter = adapter
//        }
        //override function
//        val adapter1 = object : ArrayAdapter<String>(context, R.layout.dlg_set_alarm1) {
//            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//                return super.getView(position, convertView, parent)
//            }
//        }
//        adapter1.addAll(coinNameArrData.toMutableList())    //아이템을 추가
//        coinSpinner_name.adapter = adapter1 //어댑터 연결


        btn_positive.setOnClickListener {
//            var coinIdx : Spinner = findViewById(R.id.coinSpinner_name) as Spinner
//            val textView: TextView = findViewById(R.id.coinEdit_price)
//            var condIdx = findViewById(R.id.coinSpinner_condition) as Spinner
//            val price = textView.toString().toDouble()

//            Log.d("SaveCoinAlarmDlg1", "coinNameIdx > $coinIdx")
//            Log.d("SaveCoinAlarmDlg1", "conditionIdx > $condIdx")
//            Log.d("SaveCoinAlarmDlg1", "price > $price")

            Toast.makeText(context, "설정 되었습니다", Toast.LENGTH_SHORT).show()
            dismiss()   //Dialog class method
        }

        btn_negative.setOnClickListener {
//            Toast.makeText(context, "취소됬습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

}