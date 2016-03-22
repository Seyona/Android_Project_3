package com.example.home.android_project_3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;

public class MainActivity extends AppCompatActivity implements AsyncResponse{

    private static final boolean NO_NETWORK_ACCESS = false;
    private SharedPreferences prefs = null;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    Spinner spinner;
    Toolbar toolbar;
    private ImageView background;
    private ArrayList<Pet> pets;
    private Integer connection_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        background = (ImageView)findViewById(R.id.background);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals("listPref")) {
                        Toast.makeText(getBaseContext(), "Preference changed", Toast.LENGTH_SHORT).show();
                        setup();
                    }
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(listener);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        //Lets remove the title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spinner = (Spinner)findViewById(R.id.imageSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        connection_code = -1;
        pets = new ArrayList<Pet>();


        if (!isOnline()) {
            // check for website connectivity
            // populate image view w/ default picture
            background.setImageResource(R.drawable.nointernet);
            /*background.setScaleType(ImageView.ScaleType.FIT_XY);
                Commented out until tool bar is fixed
            */
            spinner.setPrompt("Default_No_Connection");
            spinner.setSelection(0);
            Log.e("Connection", "No_Connection");
        } else {
            Log.e("Connection", "Connection");
            setup();

        }



    }

    /**
     * Function sets up the field based on the preference set
     * @return true for successful setup, false otherwise
     */
    private boolean setup()  {
        String url = prefs.getString("listPref","");
        if (url.equals("")) return false; //shouldn't happen

        ConnectionCheckTask checkConnection = new ConnectionCheckTask();
        checkConnection.resp = this;
        checkConnection.execute(url);

        if (checkConnection.resp != null) {
            if (connection_code == -1) {
                Log.e("Connection_Code", "Still -1 something went wrong");
            } else {
                if (connection_code != 200) {
                    Toast.makeText(this,"Error could not reach page code " + connection_code + " was given", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    // connection is good
                    DownloadTask download = new DownloadTask();
                    download.resp = this;
                    download.execute(url);

                    if (pets.size() == 0) {
                        Log.e("Pets", "There are no pets???");
                    } else {
                        String[] pet_names_array = new String[pets.size()];
                        int index = 0;
                        for (Pet p : pets) {
                            pet_names_array[index] = p.name;
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,pet_names_array);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                    }

                }
            }
        } else {
            Log.e("PostExecute", "resp still null");
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            default:
                break;
        }
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
                if (exitValue != 0) {
                    Toast.makeText(this, "You are connected to a network, but there is not access to the internet", Toast.LENGTH_SHORT).show();
                }
                return (exitValue == 0);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this,"You are not connected to a network", Toast.LENGTH_SHORT).show();
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


    @Override
    public void processFinish(Integer output) {
        this.connection_code = output;
    }

    @Override
    public void processFinish(ArrayList<Pet> output) {

        for (Pet p : output) {
            pets.add(p);
        }
    }
}
