/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.database.Helper
import com.mridx.fcmhelper.dataholder.Apps
import kotlinx.android.synthetic.main.app_settings.*

class AppSettings : AppCompatActivity() {

    var askToSave: Boolean = false
    var helper: Helper? = null
    var app: Apps? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_settings)

        val bundle = intent.extras
        app = bundle?.get("app") as Apps


        keyView.setText(app?.serverKey)

        keyView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                askToSave = true
            }

        })

        updateBtn.setOnClickListener { performUpdate() }

    }

    private fun performUpdate() {
        hideKeyboard(keyView)
        if (!askToSave) {
            Snackbar.make(mainLayout, "No changes to save !", Snackbar.LENGTH_LONG).show()
            return
        }

        if (helper == null)
            helper = Helper(this)

        helper!!.updateKey(app!!.id, keyView.text.toString())
        app?.serverKey = keyView.text.toString()
        askToSave = false
        Snackbar.make(mainLayout, "Server key changed !", Snackbar.LENGTH_LONG).show()

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        /*if (!askToSave)
        //super.onBackPressed()
            setResultAndExit()
        showDialog()*/
        setResultAndExit()
    }

    private fun setResultAndExit() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putSerializable("app", app)
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Update Server Key")
            .setMessage("Sure to exit without saving changes ?")
            .setPositiveButton("Save") { dialog, _ ->
                run {
                    performUpdate()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Exit") { dialog, _ ->
                run {
                    dialog.dismiss()
                    setResultAndExit()
                }
            }
            .show()
    }

    fun hideKeyboard(v: View) {
        try {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        } catch (ignored: Exception) {
        }
    }
}