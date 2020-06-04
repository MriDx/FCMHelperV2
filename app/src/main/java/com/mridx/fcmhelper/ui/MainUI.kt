/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.ui

import android.app.Dialog
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mridx.fcmhelper.R
import com.mridx.fcmhelper.adapter.AppsAdapter
import com.mridx.fcmhelper.database.DBHelper
import com.mridx.fcmhelper.database.Helper
import com.mridx.fcmhelper.dataholder.Apps
import kotlinx.android.synthetic.main.dashboard.*
import kotlinx.android.synthetic.main.main_ui.*

class MainUI : AppCompatActivity(), AppAddFragment.OnAppSave {

    private val appList: ArrayList<Apps>? = ArrayList()
    var appsAdapter: AppsAdapter? = null
    var helper: Helper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_ui)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "FCM Helper"

        fab.setOnClickListener { showAppAddDialog() }

        queryApps()

        swipeLayout.setOnRefreshListener {
            if (swipeLayout.isRefreshing)
                getApps()
        }


    }

    private fun showAppAddDialog() {
        val addAppFragment: DialogFragment = AppAddFragment()
        addAppFragment.show(supportFragmentManager, "Dialog Fragment")

    }

    private fun queryApps() {
        getApps()
        appsAdapter = AppsAdapter(this, appList!!)
        appsAdapter!!.onItemClicked = { apps ->
            Log.d("kaku", apps.appName)
        }

        appHolder.layoutManager = GridLayoutManager(this, 1)
        appHolder.adapter = appsAdapter

    }

    public fun saveApp(appName: String, serverKey: String) {
        if (helper == null)
            helper = Helper(this)

        helper!!.saveApp(appName, serverKey)
        showSnackbar()
    }

    private fun showSnackbar() {
        val snackbar = Snackbar.make(main_layout, "App added successfully. Pull to refresh app list !", Snackbar.LENGTH_LONG)
        snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackbar.setAction("Refresh") {getApps()}
        snackbar.show()
    }

    private fun getApps() {
        if (helper == null)
            helper = Helper(this)
        appList!!.clear()
        extractApps(helper!!.getAllApps())
    }

    private fun extractApps(allApps: Cursor) {
        if (allApps.moveToFirst())
            do {
                appList?.add(
                    Apps(
                        allApps.getString(allApps.getColumnIndex(DBHelper.APP_NAME)),
                        allApps.getString(allApps.getColumnIndex(DBHelper.SERVER_KEY))
                    )
                )
            } while (allApps.moveToNext())
        allApps.close()
        notifyAdapter()
    }

    private fun notifyAdapter() {
        appsAdapter?.notifyDataSetChanged()
        if (appList!!.size < 1)
            showErrorDialog()

        swipeLayout.isRefreshing = false
    }

    private fun showErrorDialog() {
        Toast.makeText(this, "No App Kela", Toast.LENGTH_SHORT).show()
    }

    override fun onAppSave(appName: String, serverKey: String) {
        saveApp(appName, serverKey)
    }


}