package com.example.home.android_project_3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by David on 3/15/16.
 */
public class DownloadTask extends AsyncTask<String, Void, ArrayList<Pet>> {
    private static final int READ_THIS_AMOUNT = 8096;
    private static final String TAG = "DownloadTask";
    private static final int TIMEOUT = 1000;

    @Override
    protected ArrayList<Pet> doInBackground(String... params) {
        String myURL = params[0];
        String dat = "";

        try {
            URL url = new URL(myURL+"pets.json");


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
                dat = sb.toString();

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
            Extract information from JsonObject
         */
        ArrayList<Pet> pets = new ArrayList<Pet>();

        try {
            JSONObject jObject  = new JSONObject(dat);
            JSONArray pet = jObject.getJSONArray("pets");

            Pet temp_pet = new Pet("","");

            for(int i = 0; i < pet.length(); i++) {
                JSONObject pet_field = pet.getJSONObject(i);
                temp_pet.name = pet_field.getString("name");
                    Log.e("Pet Name", temp_pet.name);
                temp_pet.file = pet_field.getString("file");
                    Log.e("Pet File", temp_pet.file);
                pets.add(temp_pet);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
            Download pictures
         */
        for (Pet p: pets) {
            Bitmap myBitmap = null;
            try {
                URL url = new URL(myURL+p.file);

                // this does no network IO
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                myBitmap = BitmapFactory.decodeStream(input);

            } catch (Exception e) {
                e.printStackTrace();
            }
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File temp_file  = new File(storageDir, p.file);

            if (myBitmap != null){
                saveProcessedImage(myBitmap,temp_file.getAbsolutePath());
            } else {
                Log.e("Downloaded_Bitmap", "Is null");
            }



        }
        return pets;
    }

    public static boolean saveProcessedImage(Bitmap bmp, String processedImagePath) {
        OutputStream outStream = null;
        final int QUALITY_FACTOR = 100;

        File file = new File(processedImagePath);

        try {
            outStream = new FileOutputStream(file);
            try {
                bmp.compress(Bitmap.CompressFormat.PNG, QUALITY_FACTOR, outStream);
                outStream.flush();
            } finally {
                outStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
