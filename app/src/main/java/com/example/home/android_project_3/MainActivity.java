package com.example.home.android_project_3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;

public class MainActivity extends AppCompatActivity {

    private static final boolean NO_NETWORK_ACCESS = false;
    private SharedPreferences prefs = null;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals("listPref")) {

                    }
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(listener);

        if (isOnline()) {
            // check for website connectivity

        } else {
            Log.e("Connection","No_Connection");
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Checks if the phone has internet connection by pining Google's dns
     * @return boolean if the phone is connected to the internet
     */
    public boolean isOnline() {
        if (connectedToNetwork()) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int exitValue = ipProcess.waitFor();
                return (exitValue == 0);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return NO_NETWORK_ACCESS;
    }

    /**
     * Checks if the phone is connected to a network
     * @return boolean if the phone is connected to a network
     */
    public boolean connectedToNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
