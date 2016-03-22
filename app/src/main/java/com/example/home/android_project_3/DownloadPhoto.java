package com.example.home.android_project_3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by David on 3/22/16.
 */
public class DownloadPhoto extends AsyncTask<String[], Void, Bitmap> {

    AsyncResponse resp = null;
    @Override
    protected Bitmap doInBackground(String[]... params) {
        String myURL = params[0][0];
        String f_name = params[0][1];

        try {
            URL url = new URL(myURL+f_name);

            // this does no network IO
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        resp.processFinish(result);
    }

}
