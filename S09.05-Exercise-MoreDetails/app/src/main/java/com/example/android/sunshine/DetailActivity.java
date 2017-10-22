/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.sunshine;

import android.content.ContentProvider;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
//      TODO (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

//  TODO (18) Create a String array containing the names of the desired data columns from our ContentProvider
    protected static final String[] array = {WeatherContract.WeatherEntry.COLUMN_DATE,
                                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID};

//  TODO (19) Create constant int values representing each column name's position above
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

//  TODO (20) Create a constant int to identify our loader used in DetailActivity
    private static final int ID_DETAIL_LOADER = 353;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//  TODO (15) Declare a private Uri field called mUri
    private Uri mUri;


//  TODO (10) Remove the mWeatherDisplay TextView declaration
    //private TextView mWeatherDisplay;

//  TODO (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    TextView tvDate;
    TextView tvDescription;
    TextView tvHI;
    TextView tvLOW;
    TextView tvHumidity;
    TextView tvWind;
    TextView tvPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      TODO (12) Remove mWeatherDisplay TextView
        //mWeatherDisplay = (TextView) findViewById(R.id.tv_display_weather);

//      TODO (13) Find each of the TextViews by ID
        tvDate = (TextView)findViewById(R.id.selected_date);
        tvDescription = (TextView)findViewById(R.id.selected_description);
        tvHI = (TextView)findViewById(R.id.selected_tempHI);
        tvLOW = (TextView)findViewById(R.id.selected_tempLOW);
        tvHumidity = (TextView)findViewById(R.id.selected_humidity);
        tvWind = (TextView)findViewById(R.id.selected_wind);
        tvPressure = (TextView)findViewById(R.id.selected_pressure);

//      TODO (14) Remove the code that checks for extra text
        //Intent intentThatStartedThisActivity = getIntent();
        //if (intentThatStartedThisActivity != null) {
            //if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            //    mForecastSummary = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            //    mWeatherDisplay.setText(mForecastSummary);
            //}
        //}

//      TODO (16) Use getData to get a reference to the URI passed with this Activity's Intent
        mUri = getIntent().getData();

//      TODO (17) Throw a NullPointerException if that URI is null
        if(null==mUri)
            throw new NullPointerException("uri is null");

//      TODO (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

//  TODO (22) Override onCreateLoader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        //  TODO (23) If the loader requested is our detail loader, return the appropriate CursorLoader
        switch (id)
        {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        array,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

//  TODO (24) Override onLoadFinished
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //      TODO (25) Check before doing anything that the Cursor has valid data
        boolean isValid = (data!=null && data.moveToFirst())?true:false;

        if(!isValid)
            return;


        //      TODO (26) Display a readable data string
        long dateId = data.getLong(INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(this, dateId, true);
        tvDate.setText(dateString);

        //      TODO (27) Display the weather description (using SunshineWeatherUtils)
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
        tvDescription.setText(description);

        //      TODO (28) Display the high temperature
        double tempHI = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String tempHIString = SunshineWeatherUtils.formatTemperature(this, tempHI);
        tvHI.setText(tempHIString);

        //      TODO (29) Display the low temperature
        double tempLOW = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String tempLowString = SunshineWeatherUtils.formatTemperature(this, tempLOW);
        tvLOW.setText(tempLowString);

        //      TODO (30) Display the humidity
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = "{humidity} %%";
        tvHumidity.setText(humidityString);

        //      TODO (31) Display the wind speed and direction
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        tvWind.setText(windString);

        //      TODO (32) Display the pressure
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = "{pressure} hPa";
        tvPressure.setText(pressureString);

        //      TODO (33) Store a forecast summary in mForecastSummary
        mForecastSummary = String.format("%s - %s - %s/%s",
                dateString, description, tempHIString, tempLowString);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //  TODO (34) Override onLoaderReset, but don't do anything in it yet

    }
}