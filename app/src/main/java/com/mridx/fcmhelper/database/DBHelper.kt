/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private const val DB_NAME = "APPS.DB"
        private const val DB_VERSION = 1
        const val TABLE_NAME = "apps_table"
        const val ID = "_id"
        const val APP_NAME = "app_name"
        const val SERVER_KEY = "server_key"
        const val CREATE_TABLE =
            "create table apps_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, app_name TEXT NOT NULL, server_key TEXT NOT NULL);"
    }
}