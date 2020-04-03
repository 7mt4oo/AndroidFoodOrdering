package com.sushi.foodordering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.sushi.foodordering.Fragment.ProfileFragment;
import com.sushi.foodordering.entities.LoginObject;
import com.sushi.foodordering.network.GsonRequest;
import com.sushi.foodordering.network.VolleySingleton;
import com.sushi.foodordering.util.CustomApplication;
import com.sushi.foodordering.util.Helper;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView errorDisplay;
    private TextView signInformation;
    private EditText emailInput;
    private EditText passwordInput;
    private Button btnForgotPass;
    private SharedPreferences prefs;
    public static boolean isLogin = false;
    private RequestQueue queue;
    private GoogleSignInOptions gso;//
    private ProgressDialog mProgressDialog;

    private GoogleApiClient mGoogleApiClient;
    // /*Facebook login callback class variable*/
    private CallbackManager mCallbackManager;
//
private GoogleSignInClient googleSignInClient;

    //    /*Google sign in class variable*/
    private static final int RC_SIGN_IN = 9001;
    //

    //
    private SignInButton googleSignInButton;

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
        googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken()
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

//        googleSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.sign_in_button:
//                        break;
//                }
//            }
//        });
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        // Customizing G+ button
//        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
//        googleSignInButton.setScopes(gso.getScopeArray());
//        /*Google login*/
//        gso = ((MyApplication) getApplication()).getGoogleSignInOptions();
//        mGoogleApiClient = ((MyApplication) getApplication()).getGoogleApiClient(LoginActivity.this,this);
//        SignInButton mSignInButton = (SignInButton)findViewById(R.id.sign_in_button);
//        assert mSignInButton != null;
//        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
//        mSignInButton.setScopes(gso.getScopeArray());
//        mSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });



        prefs = getSharedPreferences(Helper.SHARED_PREF, Context.MODE_PRIVATE);
        String mEmail = prefs.getString(Helper.EMAIL, "");
        String facebookLogin = prefs.getString(Helper.FACEBOOK_ID, "");
        String googleLogin = prefs.getString(Helper.GOOGLE_ID, "");
        if (!mEmail.isEmpty() || !facebookLogin.isEmpty() || !googleLogin.isEmpty()) {
            isLogin = true;
        } else {
            isLogin = false;
        }

        if (isLogin) {
            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentMain);
        }


        isUserLoggedIn();

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

    private void isUserLoggedIn() {
        Gson mGson = ((CustomApplication) getApplication()).getGsonObject();
        String storedUser = ((CustomApplication) getApplication()).getShared().getUserData();
        LoginObject userObject = mGson.fromJson(storedUser, LoginObject.class);
        if (userObject != null) {
            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentMain);
        }

//        //Facebook login
//        mCallbackManager = CallbackManager.Factory.create();
//        LoginButton mLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
//        assert mLoginButton != null;
//        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//
//
//                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intentMain);
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
//    private void revokeAccess() {
//        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        updateUI(false);
//                    }
//                });
//    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount userAccount = result.getSignInAccount();

            String userId = userAccount.getId();
            String displayedUsername = userAccount.getDisplayName();
            String userEmail = userAccount.getEmail();
            String userProfilePhoto = userAccount.getPhotoUrl().toString();

            SharedPreferences.Editor googleEdit = prefs.edit();
            googleEdit.putString(Helper.GOOGLE_ID, String.valueOf(userId));
            googleEdit.apply();



            Intent intentMain = new Intent(LoginActivity.this, ProfileFragment.class);
            startActivity(intentMain);



        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(alreadyloggedAccount);
        } else {
            Log.d(TAG, "Not logged in");
        }



    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
    }
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getApplicationContext());
    }
    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {

            Intent loginPageIntent = new Intent(this, MainActivity.class);
            startActivity(loginPageIntent);
        }
    }
}


