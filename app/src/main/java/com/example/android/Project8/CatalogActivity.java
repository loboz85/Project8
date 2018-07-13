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
package com.example.android.Project8;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.Project8.data.ProductContract.ProdEntry;
import com.example.android.Project8.data.ProdDbHelper;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private ProdDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new ProdDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the store database.
     */
    private void displayDatabaseInfo() {
//        // Create and/or open a database to read from it DO USUNIECIA
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProdEntry._ID,
                ProdEntry.COLUMN_PROD_NAME,
                ProdEntry.COLUMN_PROD_PRICE,
                ProdEntry.COLUMN_PROD_QUANTITY,
                ProdEntry.COLUMN_PROD_SUPP,
                ProdEntry.COLUMN_SUPP_PHONE};
        System.out.println("**** ProdEntry.TABLE_NAME" + ProdEntry.TABLE_NAME);

//        // Perform a query on the produckts table
//        Cursor cursor = db.query(
//                ProdEntry.TABLE_NAME,   // The table to query
//                projection,            // The columns to return
//                null,                  // The columns for the WHERE clause
//                null,                  // The values for the WHERE clause
//                null,                  // Don't group the rows
//                null,                  // Don't filter by row groups
//                null);                   // The sort order

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to access the pet data.

        System.out.println("**** ProdEntry.CONTENT_URI " + ProdEntry.CONTENT_URI);
        Cursor cursor = getContentResolver().query(
                ProdEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows
        System.out.println("**** ProdEntry.CONTENT_URI " + cursor);
        TextView displayView = (TextView) findViewById(R.id.text_view_product);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The products table contains <number of rows in Cursor> pets.
            // _id - name - price - quantity - supplier - supplier's phone number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The products table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(ProdEntry._ID + " - " +
                    ProdEntry.COLUMN_PROD_NAME + " - " +
                    ProdEntry.COLUMN_PROD_PRICE + " - " +
                    ProdEntry.COLUMN_PROD_QUANTITY + " - " +
                    ProdEntry.COLUMN_PROD_SUPP + " - " +
                    ProdEntry.COLUMN_SUPP_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProdEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_QUANTITY);
            int suppColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_SUPP);
            int phoneColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_SUPP_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentSupp = cursor.getInt(suppColumnIndex);
                int currentPhone = cursor.getInt(phoneColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupp + " - " +
                        currentPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Mars products attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProdEntry.COLUMN_PROD_NAME, "Mars");
        values.put(ProdEntry.COLUMN_PROD_PRICE, "2");
        values.put(ProdEntry.COLUMN_PROD_QUANTITY, "50");
        values.put(ProdEntry.COLUMN_PROD_SUPP, ProdEntry.SUPP_JOHN);
        values.put(ProdEntry.COLUMN_SUPP_PHONE, "+605784465");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(ProdEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}