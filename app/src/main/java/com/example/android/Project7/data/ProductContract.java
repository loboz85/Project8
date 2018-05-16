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

import android.provider.BaseColumns;

/**
 * API Contract for the Store app.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {
    }

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProdEntry implements BaseColumns {

        /**
         * Name of database table for products
         */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Product name.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PROD_NAME = "name";

        /**
         * Product Price.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PROD_PRICE = "price";

        /**
         * Product Qty.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PROD_QUANTITY = "quantity";

        /**
         * Products supplier.
         * <p>
         * The only possible values are {@link #SUPP_MARK}, {@link #SUPP_JOHN},
         * or {@link #SUPP_ANN}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PROD_SUPP = "supplier";

        /**
         * Supplier phone number.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_SUPP_PHONE = "phone";

        /**
         * Possible values for suppliers.
         */
        public static final int SUPP_MARK = 0;
        public static final int SUPP_JOHN = 1;
        public static final int SUPP_ANN = 2;
    }

}
