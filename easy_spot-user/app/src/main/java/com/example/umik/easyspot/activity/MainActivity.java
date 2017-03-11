package com.example.umik.easyspot.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.umik.easyspot.R;
import com.example.umik.easyspot.adapter.SpotsAdapter;
import com.example.umik.easyspot.service.BackgroundSpotService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvSpots;
    private SpotsAdapter mSpotsAdapter;
    private Intent intent;
    private List<ParseObject> mSpotObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rvSpots = (RecyclerView) findViewById(R.id.spotsRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSpots.setLayoutManager(linearLayoutManager);
        rvSpots.setHasFixedSize(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ParseQuery<ParseObject> queryStatus = new ParseQuery<ParseObject>("Spot");
        queryStatus.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> spotObject, ParseException e) {
                if (e == null) {

                    mSpotObject = spotObject;
                    mSpotsAdapter = new SpotsAdapter(mSpotObject);
                    rvSpots.setAdapter(mSpotsAdapter);

                } else {
                    String failMsg = getResources().getString(R.string.failFetchSpots);
                    Toast.makeText(MainActivity.this, failMsg, Toast.LENGTH_LONG).show();
                }
            }
        });

        intent = new Intent(this, BackgroundSpotService.class);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };


    private void updateUI(Intent intent) {
        ArrayList<String> currentSpotsStatus = intent.getStringArrayListExtra("currentSpotsStatus");

        for(int i = 0; i < mSpotObject.size(); i++){
            mSpotObject.get(i).put("isFree", currentSpotsStatus.get(i));
        }
        mSpotsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(BackgroundSpotService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
