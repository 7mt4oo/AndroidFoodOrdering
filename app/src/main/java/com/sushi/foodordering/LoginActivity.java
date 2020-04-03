package com.sushi.foodordering;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sushi.foodordering.entities.LoginObject;
import com.sushi.foodordering.network.GsonRequest;
import com.sushi.foodordering.network.VolleySingleton;
import com.sushi.foodordering.util.CustomApplication;
import com.sushi.foodordering.util.Helper;
import com.sushi.foodordering.util.Keys;
import com.sushi.foodordering.util.PrefUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView errorDisplay;
    private TextView signInformation;
    private EditText emailInput;
    private EditText passwordInput;
    private SharedPreferences prefs;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }



        isUserLogged();
        errorDisplay = (TextView) findViewById(R.id.login_error);
        signInformation = (TextView) findViewById(R.id.sign_in_notice);
        signInformation.setText(Helper.NEW_ACCOUNT);
        emailInput = (EditText) findViewById(R.id.email);
        passwordInput = (EditText) findViewById(R.id.password);
        TextView forgetPassword = (TextView) findViewById(R.id.forgotten_password);

        assert forgetPassword != null;
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetPasswordIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetPasswordIntent);
            }
        });


        Button signUpButton = (Button) findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(signInIntent);
            }
        });


        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredEmail = emailInput.getText().toString().trim();
                String enteredPassword = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredPassword)) {
                    Helper.displayErrorMessage(LoginActivity.this, getString(R.string.fill_all_fields));
                    return;
                }
                if (!Helper.isValidEmail(enteredEmail)) {
                    Helper.displayErrorMessage(LoginActivity.this, getString(R.string.invalid_email));
                    return;
                }

                //make server call for user authentication
                authenticateUserInRemoteServer(enteredEmail, enteredPassword);
            }
        });
    }

    private void isUserLogged() {
        boolean isUserLogged = PrefUtils.getInstance().getBoolean(Keys.IS_LOGGED.name(), false);
        Log.d(TAG, "isUserLogged: " + isUserLogged);

        if (isUserLogged) {
            //navigate to MainActivity
            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentMain);
        }
    }


    private void authenticateUserInRemoteServer(String email, String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Helper.EMAIL, email);
        params.put(Helper.PASSWORD, password);

        GsonRequest<LoginObject> serverRequest = new GsonRequest<LoginObject>(
                Request.Method.POST,
                Helper.PATH_TO_SERVER_LOGIN,
                LoginObject.class,
                params,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(serverRequest);
    }

    private Response.Listener<LoginObject> createRequestSuccessListener() {
        return new Response.Listener<LoginObject>() {
            @Override
            public void onResponse(LoginObject response) {
                try {
                    Log.d(TAG, "Json Response " + response.getLoggedIn());
                    if (response.getLoggedIn().equals("1")) {
                        //save login data to a shared preference
                        //user is logged
                        PrefUtils.getInstance().storeBoolean(Keys.IS_LOGGED.name(), true);
                        String userData = ((CustomApplication) getApplication()).getGsonObject().toJson(response);
                        ((CustomApplication) getApplication()).getShared().setUserData(userData);

                        // navigate to restaurant home
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.failed_login, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }


    }



