package com.nazkerin.GotiShare;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class createApplication extends AsyncTask<String, String, String> {
    private final String TAG = "HELLO APP";
    private final String SCHEME = "/application";

    @Override
    protected String doInBackground(String... params) {
        StringBuilder response = new StringBuilder();

        String content = params[0];
        Log.e(TAG, content);
        String URI = params[1];
        String login = params[2];
        String password = params[3];

        try {
            URL url = new URL(URI + SCHEME);
            Log.e(TAG, url.toString());

            String userPassword = login + ":" + password;
            String encoding = Base64.encodeToString(userPassword.getBytes(), Base64.NO_WRAP);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", "Basic " + encoding);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", content);
            jsonObject.put("description", content + "app for GotiShare");

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            Log.e(TAG, jsonObject.toString());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

            int responseCode=httpURLConnection.getResponseCode();

            BufferedReader br;
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                br=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            }
            else {
                br=new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            }

            String line;
            while ((line=br.readLine()) != null) {
                response.append(line);
            }

            Log.e("HELLO APP", response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        onResponse(result);
    }

    public void onResponse(String response){

    }
}
