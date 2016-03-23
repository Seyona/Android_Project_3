package com.example.home.android_project_3;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by David on 3/17/16.
 */
public class ConnectionCheckTask extends AsyncTask<String, Void, Integer> {
    public AsyncResponse resp = null;
    @Override
    protected Integer doInBackground(String... params) {
        URL url = null;
        int code = 0;
        try {
            url = new URL(params[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            code = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return code;
    }

    @Override
    protected void onPostExecute(Integer result) {
        resp.processFinish(result);
    }
}
