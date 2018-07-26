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

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.util.Log;

import com.example.android.Project8.data.ProductContract.ProdEntry;
import com.example.android.Project8.data.ProdDbHelper;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int PROD_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    ProdCursorAdapter mCursorAdapter;

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

        // Find the ListView which will be populated with the prod data
        ListView prodListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        prodListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of prod data in the Cursor.
        // There is no prod data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ProdCursorAdapter(this, null);
        prodListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        prodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific prod that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProdEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.products/products/2"
                // if the product with ID 2 was clicked on.
                Uri currentProdUri = ContentUris.withAppendedId(ProdEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentProdUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PROD_LOADER, null, this);


    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertProduct() {


        // Create a ContentValues object where column names are the keys,
        // and Mars products attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProdEntry.COLUMN_PROD_NAME, "Mars");
        values.put(ProdEntry.COLUMN_PROD_PRICE, "2");
        values.put(ProdEntry.COLUMN_PROD_QUANTITY, "50");
        values.put(ProdEntry.COLUMN_PROD_SUPP, ProdEntry.SUPP_JOHN);
        values.put(ProdEntry.COLUMN_SUPP_PHONE, "+605784465");

        // Insert a new row for Mars into the provider using the ContentResolver.
        // Use the {@link ProdEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Mars's data in the future.
        Uri newUri = getContentResolver().insert(ProdEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ProdEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProdEntry._ID,
                ProdEntry.COLUMN_PROD_NAME,
                ProdEntry.COLUMN_PROD_PRICE,
                ProdEntry.COLUMN_PROD_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,     // Parent activity context
                ProdEntry.CONTENT_URI,          // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                 // Default sort order;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link ProdCursorAdapter} with this new cursor containing updated prod data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}