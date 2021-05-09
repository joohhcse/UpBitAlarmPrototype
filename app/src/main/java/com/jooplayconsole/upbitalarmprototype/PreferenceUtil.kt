package com.jooplayconsole.upbitalarmprototype

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
            context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    /*
    *   Alarm 1 Example > "key" : "value"
    *   > coinName :    "pf_coinName1" : "XRP"
    *   > coinPrice :   "pf_coinPrice1" : "1855"
    *   > coinCondition :   "pf_coinCondition1" : "up" (or "down")
    * */


    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

}