package com.example.home.android_project_3;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by David on 3/15/16.
 */
public class DownloadTask extends AsyncTask<String, Void, String> {
    private static final int READ_THIS_AMOUNT = 8096;
    private static final String TAG = "DownloadTask";
    private static final int TIMEOUT = 1000;

    @Override
    protected String doInBackground(String... params) {
        String myURL = params[0];

        try {
            URL url = new URL(myURL);

            // this does no network IO
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // can further configure connection before getting data
            // cannot do this after connected
            connection.setRequestMethod("GET");
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            // this opens a connection, then sends GET & headers

            // wrap in finally so that stream bis is sure to close
            // and we disconnect the HttpURLConnection
            BufferedReader in = null;
            try {
                connection.connect();

                // lets see what we got make sure its one of
                // the 200 codes (there can be 100 of them
                // http_status / 100 != 2 does integer div any 200 code will = 2
                int statusCode = connection.getResponseCode();
                if (statusCode / 100 != 2) {
                    Log.e(TAG, "Error-connection.getResponseCode returned "
                            + Integer.toString(statusCode));
                    return null;
                }

                in = new BufferedReader(new InputStreamReader(connection.getInputStream()), READ_THIS_AMOUNT);

                // the following buffer will grow as needed
                String myData;
                StringBuffer sb = new StringBuffer();

                while ((myData = in.readLine()) != null) {
                    sb.append(myData);
                }
              //  return sb.toString();

            } finally {
                // close resource no matter what exception occurs
                in.close();
                connection.disconnect();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }

        /*
        Download image from here maybe convert sb to a JSON object?
         */

        return null;
    }
}
