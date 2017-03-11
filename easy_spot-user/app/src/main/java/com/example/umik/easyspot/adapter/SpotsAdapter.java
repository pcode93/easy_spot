package com.example.umik.easyspot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umik.easyspot.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by anhtuan.nguyen on 08.01.2016.
 */
public class SpotsAdapter extends RecyclerView.Adapter<SpotsAdapter.SpotHolder> {

    private List<ParseObject> mSpotList;

    @Override
    public int getItemCount() {
        return mSpotList.size();
    }

    public SpotsAdapter(List<ParseObject> spotList) {
        this.mSpotList = spotList;
    }

    @Override
    public void onBindViewHolder(SpotHolder spotViewHolder, int i) {
        final ParseObject spot = mSpotList.get(i);

        spotViewHolder.vSpotStatus.setText(spot.getString("isFree"));
        spotViewHolder.vSpotId.setText(String.valueOf(i + 1));
        spotViewHolder.vBookSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("parkingId", "parking1");
                params.put("spotId", "2");
                params.put("isFree", "zarezerwowano");
                ParseCloud.callFunctionInBackground("update_spot", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String response, ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getName(), "An error occurred while running Parse Code.", e);
                        }
                    }
                });

                spot.put(context.getString(R.string.querySpotStatus), context.getString(R.string.spotStatusBooked));
                spot.saveInBackground();

                String bookSpotSuccess = context.getString(R.string.bookSpotSuccess);
                Toast.makeText(context, bookSpotSuccess, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public SpotHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.spot_information, viewGroup, false);

        return new SpotHolder(itemView);
    }

    public static class SpotHolder extends RecyclerView.ViewHolder {

        protected TextView vSpotStatus;
        protected TextView vSpotId;
        protected Button vBookSpot;

        public SpotHolder(View viewHolder) {
            super(viewHolder);
            vSpotStatus =  (TextView) viewHolder.findViewById(R.id.spotStatusTextView);
            vSpotId =  (TextView) viewHolder.findViewById(R.id.spotIdTextView);
            vBookSpot = (Button) viewHolder.findViewById(R.id.bookSpotButton);
        }
    }
}
