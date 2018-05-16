/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.Project7.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.Project7.data.ProductContract.ProdEntry;

/**
 * Database helper for Product app. Manages database creation and version management.
 */
public class ProdDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProdDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "store6.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructs a new instance of {@link ProdDbHelper}.
     *
     * @param context of the app
     */
    public ProdDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProdEntry.TABLE_NAME + " ("
                + ProdEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProdEntry.COLUMN_PROD_NAME + " TEXT NOT NULL, "
                + ProdEntry.COLUMN_PROD_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProdEntry.COLUMN_PROD_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProdEntry.COLUMN_PROD_SUPP + " INTEGER NOT NULL, "
                + ProdEntry.COLUMN_SUPP_PHONE + " INTEGER NOT NULL DEFAULT 0);";
        System.out.println("**** " + SQL_CREATE_PRODUCTS_TABLE);
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        System.out.println("****  table created");
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}