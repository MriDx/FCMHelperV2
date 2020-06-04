/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.database

import android.content.Context
import android.database.Cursor

class Helper(context: Context) {
    var context : Context? = null
    init {
        this.context = context
    }

    fun getAllApps() : Cursor {
        val dbManager = DBManager(context!!).open()
        val cursor = dbManager.fetch()
        dbManager.close()
        return cursor!!
    }

    fun saveApp(appName: String, serverKey: String) {
        val dbManager = DBManager(context!!).open()
        dbManager.insert(appName, serverKey)
        dbManager.close()
    }

}