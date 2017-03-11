package com.example.umik.easyspot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.umik.easyspot.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anhtuan.nguyen on 09.01.2016.
 */
public class BackgroundSpotService extends Service {

    private static final String TAG = "BackgroundSpotService";
    public static final String BROADCAST_ACTION = "BackgroundSpotService.checkParseDbChanges";

    private final Handler handler = new Handler();
    private List<ParseObject> mStatusObject;
    protected Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000);
        return START_NOT_STICKY;
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            currentSpotStatus();
            handler.postDelayed(this, 3000); // 3 seconds
        }
    };

    private void currentSpotStatus() {
        ParseQuery<ParseObject> queryStatus = new ParseQuery<ParseObject>("Spot");
        queryStatus.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> statusObject, ParseException e) {
                if (e == null) {
                    mStatusObject = statusObject;
                    ArrayList<String> currentSpotsStatus = new ArrayList<String>(mStatusObject.size());
                    Log.d(TAG, "Parse data successfully fetched");

                    for (ParseObject parseObject : mStatusObject) {
                        currentSpotsStatus.add(parseObject.getString("isFree"));
                    }

                    intent.putStringArrayListExtra("currentSpotsStatus", currentSpotsStatus);
                    sendBroadcast(intent);

                } else {
                    Log.d(TAG, "Error while fetching Parse data");
                    String failMsg = getResources().getString(R.string.failFetchSpots);
                    Toast.makeText(getApplicationContext(), failMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }
}
