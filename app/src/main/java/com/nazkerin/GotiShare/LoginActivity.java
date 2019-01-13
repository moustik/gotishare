package com.nazkerin.GotiShare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mLoginView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLoginSignInButton = findViewById(R.id.email_sign_in_button);
        mLoginSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isLoginValid(login)) {
            mLoginView.setError(getString(R.string.error_invalid_email));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(login, password);

            SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
            mAuthTask.execute(
                    settings.getString("URI", ""),
                    settings.getString("app_name", "gotishare")
            );
        }
    }

    private boolean isLoginValid(String email) {
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, String, String> {

        private final String mLogin;
        private final String mPassword;
        private String mURI;

        UserLoginTask(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response = new StringBuilder();

            String URI = params[0];
            mURI = URI;
            String app_name = params[1];
            Log.e("HELLO APP", "URI " + URI);

            try {
                URL url = new URL(URI + "/application");
                Log.e("HELLO APP", url.toString());

                String userPassword = mLogin + ":" + mPassword;
                String encoding = Base64.encodeToString(userPassword.getBytes(), Base64.NO_WRAP);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Basic " + encoding);
                httpURLConnection.setRequestMethod("GET"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.connect();


                int responseCode=httpURLConnection.getResponseCode();
                Log.e("HELLO APP", Integer.toString(responseCode));


                String line;
                BufferedReader br;
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                }
                else {
                    br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));

                }
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                Log.e("HELLO APP", response.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(final String response) {
            mAuthTask = null;
            showProgress(false);
            String token;

            SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
            String app_name_from_settings = settings.getString("app_name", "");

            try {
                Object json = new JSONTokener(response).nextValue();
                if (json instanceof JSONArray) {
                    int i;
                    token = "-1";
                    JSONArray jsonArray = (JSONArray) json;
                    for (i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);

                        String app_name;
                        app_name = row.getString("name");
                        if(app_name.equals(app_name_from_settings)) {
                            // gotcha
                            token = row.getString("token");
                            break;
                        }

                    }
                    Log.e("HELLO APP", token);

                    if(i != jsonArray.length()) {
                        Toast.makeText(getApplicationContext(),
                                "token found for app " + app_name_from_settings, Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("token", token);
                        editor.commit();

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No token found for app " + app_name_from_settings, Toast.LENGTH_SHORT).show();
                        new createApplication() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("HELLO APP", response);
                                super.onResponse(response);
                                try {
                                    JSONObject e = new JSONObject(response);
                                    String token = e.getString("token");

                                    SharedPreferences settings = getSharedPreferences("UserConfig", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("token", token);
                                    editor.commit();

                                    finish();

                                    Toast.makeText(getApplicationContext(),
                                            "created one" + token, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute(app_name_from_settings,
                                mURI, mLogin, mPassword);
                    }
                }
                else if (json instanceof JSONObject)
                {   // might be an error
                    JSONObject e = (JSONObject) json;

                    Toast.makeText(getApplicationContext(),
                            e.getString("errorDescription"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

