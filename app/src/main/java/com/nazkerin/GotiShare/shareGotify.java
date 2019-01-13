package com.nazkerin.GotiShare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class shareGotify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            new postMessage() {
                @Override
                public void onResponse(String response) {
                    super.onResponse(response);
                    onRaysponse(response);
                }
            }.execute(
                    sharedText,
                    settings.getString("URI", ""),
                    settings.getString("token", "0")
            );
        }
        finish();

    }

    public void onRaysponse(String response){
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            intent.putExtra("com.nazkerin.moustik.hello.MESSAGE", response);
            //startActivity(intent);

            Toast.makeText(getApplicationContext(), "shared", Toast.LENGTH_SHORT).show();
        }
}
