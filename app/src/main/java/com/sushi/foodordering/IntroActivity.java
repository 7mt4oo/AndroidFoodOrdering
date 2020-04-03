package com.sushi.foodordering;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.sushi.foodordering.util.Keys;
import com.sushi.foodordering.util.PrefUtils;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }

//        Button startButton = (Button)findViewById(R.id.start_app);
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent startAppIntent = new Intent(IntroActivity.this, LoginOptionActivity.class);
//                startActivity(startAppIntent);
//            }
//        });
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4000);
                    isUserLogged();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        myThread.start();
    }
    private void isUserLogged() {
        boolean isUserLogged = PrefUtils.getInstance().getBoolean(Keys.IS_LOGGED.name(), false);
        Log.d(TAG, "isUserLogged: " +  isUserLogged);

        if (isUserLogged){
            //navigate to MainActivity
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        }
    }

}

