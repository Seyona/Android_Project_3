package com.example.home.android_project_3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final boolean NO_NETWORK_ACCESS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
