/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.mridx.fcmhelper.R
import kotlinx.android.synthetic.main.report_layout.*

class SendReport : AppCompatActivity() {

    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_layout)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Send Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sendReport.setOnClickListener { saveReport() }

        db = FirebaseFirestore.getInstance()


    }

    private fun saveReport() {
        when {
            fullName.text!!.isEmpty() -> return
            bugName.text!!.isEmpty() -> return
            bugDescription.text!!.isEmpty() -> return
        }

        val bug : HashMap<String, Any> = HashMap()
        bug["fullname"] = fullName.text.toString()
        bug["bugname"] = bugName.text.toString()
        bug["bugdescription"] = bugDescription.text.toString()

        db!!.collection("bugs").add(bug).addOnSuccessListener { documentReference ->
            FirebaseMessaging.getInstance().subscribeToTopic(documentReference.id)
        }
        showSnackbar()
        clearForm()
    }


    private fun clearForm() {
        fullName.text?.clear()
        bugName.text?.clear()
        bugDescription.text?.clear()
    }

    private fun showSnackbar() {
        Snackbar.make(mainReportLayout, "Submitted ! Thank you for reporting.", Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home)
            onBackPressed()

        return true
    }
}