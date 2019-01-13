package com.nazkerin.GotiShare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.nazkerin.moustik.hello.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText URI_value = findViewById(R.id.URI_value);
        EditText app_name_value = findViewById(R.id.app_name_value);
        EditText token_value = findViewById(R.id.token_value);
        SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
        URI_value.setText(settings.getString("URI", URI_value.getText().toString()));
        token_value.setText(settings.getString("app_name", app_name_value.getText().toString()));
        token_value.setText(settings.getString("token", token_value.getText().toString()));
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Log.e("HELLO APP", "button clicked  !!!!!!!!!!!!!!!!!!");

        saveSettings();

        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
    }

    public void onRaysponse(String response) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, response);
        startActivity(intent);
    }

    public void saveSettings() {

        EditText URI_value = findViewById(R.id.URI_value);
        EditText app_name_value = findViewById(R.id.app_name_value);
        EditText token_value = findViewById(R.id.token_value);

        SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("URI", URI_value.getText().toString());
        editor.putString("app_name", app_name_value.getText().toString());
        editor.putString("token", token_value.getText().toString());
        editor.apply();
    }

    public void onGetToken(View view) {
        // save URI
        EditText URI_value = findViewById(R.id.URI_value);
        EditText app_name_value = findViewById(R.id.app_name_value);
        SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("URI", URI_value.getText().toString());
        editor.putString("app_name", app_name_value.getText().toString());
        editor.commit();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
        EditText token_value = findViewById(R.id.token_value);
        token_value.setText(settings.getString("token", token_value.getText().toString()));
    }
}