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
package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherProvider;


public class SunshineSyncUtils {

    //  TODO (1) Declare a private static boolean field called sInitialized
    private static boolean sInitialize = false;

    //  TODO (2) Create a synchronized public static void method called initialize
    synchronized public static void initialize(Context context)
    {
        //  TODO (3) Only execute this method body if sInitialized is false
        if(false == sInitialize) {
            //  TODO (4) If the method body is executed, set sInitialized to true
            sInitialize = true;
            String[] array = {WeatherContract.WeatherEntry._ID};

            //  TODO (5) Check to see if our weather ContentProvider is empty
            Cursor cursor = context.getContentResolver().query(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    array,
                    WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards(),
                    null,
                    null);

            //  TODO (6) If it is empty or we have a null Cursor, sync the weather now!
            if(null==cursor || 0==cursor.getCount())
                startImmediateSync(context);

            cursor.close();
        }
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}