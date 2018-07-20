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
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;

import com.example.android.Project8.data.ProductContract.ProdEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_PROD_LOADER = 0;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentProdUri;

    /**
     * EditText field to enter the product name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product qty
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the suppliers phone number
     */
    private EditText mPhoneEditText;

    /**
     * EditText field to enter product supplier
     */
    private Spinner mSuppSpinner;

    /**
     * Product supplier. The possible valid values are in the ProductContract.java file:
     * {@link ProdEntry#SUPP_MARK}, {@link ProdEntry#SUPP_JOHN}, or
     * {@link ProdEntry#SUPP_ANN}.
     */
    private int mSupp = ProdEntry.SUPP_MARK;

    /**
     * Boolean flag that keeps track of whether the prod has been edited (true) or not (false)
     */
    private boolean mProdHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProdHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProdUri = intent.getData();

        // If the intent DOES NOT contain a prod content URI, then we know that we are
        // creating a new prod.
        if (mCurrentProdUri == null) {
            // This is a new prod, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_prod));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a prod that hasn't been created yet.)
            invalidateOptionsMenu();

        } else {
            // Otherwise this is an existing prod, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_prod));

            // Initialize a loader to read the prod data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PROD_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_prod_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mPhoneEditText = (EditText) findViewById(R.id.edit_phone);
        mSuppSpinner = (Spinner) findViewById(R.id.spinner_supp);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mSuppSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the supplier.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supp_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSuppSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSuppSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supp_john))) {
                        mSupp = ProdEntry.SUPP_JOHN;
                    } else if (selection.equals(getString(R.string.supp_ann))) {
                        mSupp = ProdEntry.SUPP_ANN;
                    } else {
                        mSupp = ProdEntry.SUPP_MARK;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupp = ProdEntry.SUPP_MARK;
            }
        });
    }

    /**
     * Get user input from editor and save new product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        if ("".equals(priceString) || "".equals(quantityString) || "".equals(phoneString)) {

            Toast.makeText(this, "All fields need to be filled, please add product again", Toast.LENGTH_SHORT).show();
            return;
        }
        int price = Integer.parseInt(priceString);
        int quantity = Integer.parseInt(quantityString);
        int phone = Integer.parseInt(phoneString);

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProdEntry.COLUMN_PROD_NAME, nameString);
        values.put(ProdEntry.COLUMN_PROD_PRICE, price);
        values.put(ProdEntry.COLUMN_PROD_QUANTITY, quantity);
        values.put(ProdEntry.COLUMN_PROD_SUPP, mSupp);
        values.put(ProdEntry.COLUMN_SUPP_PHONE, phone);

        // Determine if this is a new or existing prod by checking if mCurrentPetUri is null or not
        if (mCurrentProdUri == null) {
            // This is a NEW prod, so insert a new prod into the provider,
            // returning the content URI for the new prod.
            Uri newUri = getContentResolver().insert(ProdEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            // Otherwise this is an EXISTING prod, so update the prod with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new prod, hide the "Delete" menu item.
        if (mCurrentProdUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProdHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the prod hasn't changed, continue with handling back button press
        if (!mProdHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all prod attributes, define a projection that contains
        // all columns from the products table
        String[] projection = {
                ProdEntry._ID,
                ProdEntry.COLUMN_PROD_NAME,
                ProdEntry.COLUMN_PROD_PRICE,
                ProdEntry.COLUMN_PROD_QUANTITY,
                ProdEntry.COLUMN_PROD_SUPP,
                ProdEntry.COLUMN_SUPP_PHONE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,     // Parent activity context
                mCurrentProdUri,                // Query the content URI for the current prod
                projection,                     // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of prod attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_QUANTITY);
            int suppColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_SUPP);
            int phoneColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_SUPP_PHONE);

            // Extract out the value from the Cursor for the given column index
            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSupp = cursor.getInt(suppColumnIndex);
            int currentPhone = cursor.getInt(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(currentName);
            mPriceEditText.setText(Integer.toString(currentPrice));
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mPhoneEditText.setText(Integer.toString(currentPhone));
            // Supplier is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (currentSupp) {
                case ProdEntry.SUPP_JOHN:
                    mSuppSpinner.setSelection(1);
                    break;
                case ProdEntry.SUPP_ANN:
                    mSuppSpinner.setSelection(2);
                    break;
                default:
                    mSuppSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mPhoneEditText.setText("");
        mSuppSpinner.setSelection(0); // Select Mark as supplier
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this prod.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the prod.
                deleteProd();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the prod.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the prod in the database.
     */
    private void deleteProd() {
        // Only perform the delete if this is an existing prod.
        if (mCurrentProdUri != null) {
            // Call the ContentResolver to delete the prod in the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the prod that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProdUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}