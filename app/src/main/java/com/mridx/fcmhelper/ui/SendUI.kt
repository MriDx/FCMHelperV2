/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.database.Apis
import com.mridx.fcmhelper.dataholder.Apps
import kotlinx.android.synthetic.main.edittext_layout.view.*
import kotlinx.android.synthetic.main.send_ui.*
import org.json.JSONException
import org.json.JSONObject

class SendUI : AppCompatActivity() {

    lateinit var app: Apps
    val key =
        "key=AAAA--m-QaQ:APA91bGO1gdIYUpDc-2uoRfHvgrcficyyTU4PfM39L4kHRpBK3EOZNQ_TagJpvQ48-wv6HLbU_JQwz4WeWRcRAiAY56gdC22MXx-bUcYUeW2hmU36dAENZGQ3S1I6H05uOSgfu5XHuRE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send_ui)


        val bundle = intent.extras
        app = bundle?.get("app") as Apps
        setSupportActionBar(toolbar)
        supportActionBar?.title = app.appName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        msgSend.setOnClickListener { performSendClick() }

    }

    private fun performSendClick() {
        when {
            msgTo.text!!.isEmpty() -> return
            msgTitle.text!!.isEmpty() -> return
            msgBody.text!!.isEmpty() -> return
            else -> sendMsg()
        }

    }


    private fun sendMsg() {
        val requestQueue = Volley.newRequestQueue(this)
        val request = object : JsonObjectRequest(
            Method.POST,
            Apis().baseUrl,
            generateRequestObject(),
            Response.Listener { response: JSONObject -> showResponse(response) },
            Response.ErrorListener { error -> showError(error) }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headerMap: MutableMap<String, String> =
                    HashMap()
                headerMap["Content-Type"] = "application/json"
                headerMap["Authorization"] = "key=${app.serverKey}"
                return headerMap
            }
        }
        requestQueue.add(request)
    }


    private fun generateRequestObject(): JSONObject? {

        val jsonData = JSONObject()
        val jsonParams = JSONObject()

        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            jsonData.put(child.key.text.toString(), child.value.text.toString())
        }

        try {
            jsonParams.put("to", msgTo.text.toString())
            jsonData.put("title", msgTitle.text.toString())
            jsonData.put("body", msgBody.text.toString())
            jsonParams.put("data", jsonData)
            jsonParams.put("notification", jsonData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonParams
    }

    private fun showResponse(response: JSONObject) {
        Log.d("kaku", response.toString())
        if (response.has("message_id")) {
            msgResponse.text = "Message id - " + response.getString("message_id")
            msgResponse.visibility = View.VISIBLE
            showSnackbar()
            return
        }

    }

    private fun showSnackbar() {
        Snackbar.make(main_layout, "Message Sent Successfully !", Snackbar.LENGTH_SHORT)
            .setAction("OK", null)
            .show()
    }

    private fun showError(error: VolleyError?) {
        Log.d("kaku", error.toString())
    }

    fun addView(v: View?) {
        val inflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val addview: View = inflater.inflate(R.layout.edittext_layout, null)
        linearLayout.addView(addview)
    }

    fun removeView(v: View) {
        linearLayout.removeView(v.parent as View)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home)
            onBackPressed()

        return true
    }
}