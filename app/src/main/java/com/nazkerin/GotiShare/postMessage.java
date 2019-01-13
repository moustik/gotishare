package com.nazkerin.GotiShare;

import android.os.AsyncTask;
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

public class postMessage extends AsyncTask<String, String, String> {
    private final String TAG = "HELLO APP";

    @Override
    protected String doInBackground(String... params) {
        String response = "";

        String content = params[0];
        Log.e(TAG, content);
        String URI = params[1];
        String token = params[2];

        try {
            URL url = new URL(URI + "/message?token=" + token);
            Log.e(TAG, url.toString());
            //url = new URL("http://192.168.1.88:8000/version"); //Enter URL here
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", content);
            jsonObject.put("title", "title");
            jsonObject.put("priority", 2);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            Log.e(TAG, jsonObject.toString());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

            int responseCode=httpURLConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        onResponse(result);
    }

    public void onResponse(String response){

    }
}
