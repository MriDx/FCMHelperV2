/*
 * Copyright (c) 2020.
 * Code by Mridul Baishya
 */

package com.mridx.fcmhelper.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class DBManager(c: Context) {
    private var dbHelper: DBHelper? = null
    private var context: Context = c
    private var database: SQLiteDatabase? = null

    @Throws(SQLException::class)
    fun open(): DBManager {
        dbHelper = DBHelper(context)
        database = dbHelper!!.writableDatabase
        return this
    }

    fun close() {
        dbHelper?.close()
    }

    fun insert(appName: String?, serverKey: String?) {
        val contentValue = ContentValues()
        contentValue.put(DBHelper.APP_NAME, appName)
        contentValue.put(DBHelper.SERVER_KEY, serverKey)
        database!!.insert(DBHelper.TABLE_NAME, null, contentValue)
    }

    fun fetch(): Cursor? {
        val columns =
            arrayOf(DBHelper.APP_NAME, DBHelper.SERVER_KEY, DBHelper.ID)
        val cursor =
            database!!.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null)
        cursor?.moveToFirst()
        return cursor
    }

    fun update(appName: String, serverKey: String?): Int {
        val contentValues = ContentValues()
        //contentValues.put(DBHelper.APP_NAME, appName);
        contentValues.put(DBHelper.SERVER_KEY, serverKey)
        return database!!.update(
            DBHelper.TABLE_NAME,
            contentValues,
            DBHelper.APP_NAME + " = " + appName,
            null
        )
    }

    fun delete(appName: String) {
        database!!.delete(DBHelper.TABLE_NAME, DBHelper.APP_NAME + "=" + appName, null)
    }

    fun load() {
        val query = "SELECT app_name, server_key FROM apps_table"
        database!!.execSQL(query)
    }

    fun server_key(appName: String): Cursor {
        return database!!.rawQuery(
            "SELECT server_key FROM apps_table WHERE app_name = '$appName'",
            null,
            null
        )
    }

    fun updateKey(appName: String, serverKey: String): Cursor {
        val m = database!!.rawQuery(
            "UPDATE apps_table SET server_key = '$serverKey' WHERE app_name ='$appName'",
            null,
            null
        )
        m.moveToFirst()
        val k = database!!.rawQuery(
            "SELECT server_key FROM apps_table WHERE app_name = '$appName'",
            null,
            null
        )
        m.close()
        return k
    }

    fun delete_app(appName: String): Cursor {
        val m = database!!.rawQuery(
            "DELETE FROM apps_table WHERE app_name = '$appName'",
            null,
            null
        )
        m.moveToFirst()
        val n = database!!.rawQuery(
            "SELECT server_key FROM apps_table WHERE app_name = '$appName'",
            null,
            null
        )
        m.close()
        return n
    }

    fun delete_app(id: Int): Cursor {
        val m = database!!.rawQuery(
            "DELETE FROM ${DBHelper.TABLE_NAME} WHERE ${DBHelper.ID} = '$id'",
            null,
            null
        )
        m.moveToFirst()
        /*val n = database!!.rawQuery(
            "SELECT server_key FROM apps_table WHERE app_name = '$appName'",
            null,
            null
        )
        m.close()
        return n*/
        return m;
    }

}
