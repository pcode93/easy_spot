package com.example.umik.easyspot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.umik.easyspot.R;

/**
 * Created by anhtuan.nguyen on 09.01.2016.
 */
public class HomePageActivity extends Activity {

    private Button mLaunchApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_homepage);

        mLaunchApp = (Button) findViewById(R.id.launchAppBtn);
        mLaunchApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent launchAppIntent = new Intent(HomePageActivity.this, MainActivity.class);
                startActivity(launchAppIntent);
            }
        });
    }
}
