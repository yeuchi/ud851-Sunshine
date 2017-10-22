package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.io.IOException;
import java.net.URL;

//  TODO (1) Create a class called SunshineSyncTask
public class SunshineSyncTask {

    //  TODO (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
    synchronized public static void syncWeather(Context context)
    {
    //      TODO (3) Within syncWeather, fetch new weather data
        try
        {
            /*
             * This is not possible without the understand of the tasks and
             * the current sunshine project in a broader scope.
             */
            // get data from external service
            URL url = NetworkUtils.getUrl(context);
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            ContentValues[] values = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, json);

            if(null!=values && values.length>0)
            {
                //      TODO (4) If we have valid results, delete the old data and insert the new

                // put it in our local data on a service somewhere..
                ContentResolver resolver = context.getContentResolver();
                resolver.delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
                resolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, values);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
