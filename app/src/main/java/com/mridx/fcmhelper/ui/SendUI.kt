/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
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
import com.mridx.fcmhelper.database.Helper
import com.mridx.fcmhelper.dataholder.Apps
import kotlinx.android.synthetic.main.app.*
import kotlinx.android.synthetic.main.edittext_layout.view.*
import kotlinx.android.synthetic.main.send_ui.*
import org.json.JSONException
import org.json.JSONObject

class SendUI : AppCompatActivity() {

    var SETTINGS_REQ_CODE = 2001
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
        mainProgressbar.visibility = View.VISIBLE
        msgSend.text = ""
        msgSend.isEnabled = false
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
        msgSend.text = "Send"
        msgSend.isEnabled = true
        mainProgressbar.visibility = View.GONE
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
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.updateSKey -> startSettings()
            R.id.deleteApp -> performAppDelete()
        }

        return true
    }

    private fun performAppDelete() {
        Helper(this).deleteApp(app.id)
        Snackbar.make(main_layout, "App has been deleted !", Snackbar.LENGTH_SHORT)
            .show()
        Handler().postDelayed({ finish() }, 1000 * 2)
    }

    private fun startSettings() {
        val intent = Intent(this, AppSettings::class.java)
        val bundle = Bundle()
        bundle.putSerializable("app", app)
        intent.putExtras(bundle)
        startActivityForResult(intent, SETTINGS_REQ_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQ_CODE) {
            val bundle = data?.extras
            app = bundle?.get("app") as Apps
        }

    }
}