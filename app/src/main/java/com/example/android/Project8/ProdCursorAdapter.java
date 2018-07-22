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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.Project8.data.ProductContract.ProdEntry;

/**
 * {@link ProdCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of prod data as its data source. This adapter knows
 * how to create list items for each row of prod data in the {@link Cursor}.
 */


public class ProdCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link ProdCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProdCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the prod data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current prod can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
//        ImageButton buyButton = (ImageButton) view.findViewById(R.id.buy_button);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of prod attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProdEntry.COLUMN_PROD_QUANTITY);

        // Read the prod attributes from the Cursor for the current prod
        String prodName = cursor.getString(nameColumnIndex);
        String prodPrice = cursor.getString(priceColumnIndex);
        String prodQuantity = cursor.getString(quantityColumnIndex);

        // Display text
        String prodPriceText = "Price: " + prodPrice + " $";
        String prodQuantityText = "Quantity: " + prodQuantity;


        // Update the TextViews with the attributes for the current prod
        nameTextView.setText(prodName);
        summaryTextView.setText(prodPriceText);
        quantityTextView.setText(prodQuantityText);


//        String currentQuantity = cursor.getString(quantityColumnIndex);
//        final int quantityIntCurrent = Integer.valueOf(currentQuantity);
//
//        final int productId = cursor.getInt(cursor.getColumnIndex(ProdEntry._ID));
//
//        //Sell button which decrease quantity in storage
//        buyButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (quantityIntCurrent > 0) {
//                    int newQuantity = quantityIntCurrent - 1;
//                    Uri quantityUri = ContentUris.withAppendedId(ProdEntry.CONTENT_URI, productId);
//
//                    ContentValues values = new ContentValues();
//                    values.put(ProdEntry.COLUMN_PROD_QUANTITY, newQuantity);
//                    context.getContentResolver().update(quantityUri, values, null, null);
//                } else {
//                    Toast.makeText(context, "This game is out of stock!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
}

