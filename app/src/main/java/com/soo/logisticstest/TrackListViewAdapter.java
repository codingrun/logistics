package com.soo.logisticstest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soo.logisticstest.Model.TrackingModel;

import java.util.ArrayList;

/**
 * Created by 이수연 on 2017-08-21.
 */

public class TrackListViewAdapter extends BaseAdapter {
    private ArrayList<TrackingModel> TrackList = new ArrayList<TrackingModel>();
    int finalCount = 0;

    public TrackListViewAdapter(ArrayList<TrackingModel> receiveList){
        //거꾸로 넣어주기 위해 넣어줌
        for(int i = (receiveList.size()-1); i > 0;i--){
            TrackList.add(receiveList.get(i));
        }
    }

    //데이터 갯수 전달
    @Override
    public int getCount() {
        return TrackList.size();
    }

    @Override
    public TrackingModel getItem(int position) {
        return TrackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.track_item, parent, false);
        }

        TextView ARRIVEDATE_TEXT = (TextView)convertView.findViewById(R.id.ARRIVEDATE);
        TextView ARRIVETIME_TEXT = (TextView)convertView.findViewById(R.id.ARRIVETIME);
        TextView LOCATE_TEXT = (TextView)convertView.findViewById(R.id.LOCATE);
        TextView STATUS_TEXT = (TextView)convertView.findViewById(R.id.STATUS);

        TrackingModel trackModel = TrackList.get(position);

        ARRIVEDATE_TEXT.setText(trackModel.getARRIVEDATE());
        ARRIVETIME_TEXT.setText(trackModel.getARRIVETIME());
        LOCATE_TEXT.setText(trackModel.getLOCATE());
        STATUS_TEXT.setText(trackModel.getSTATUS());

        return convertView;
    }


    public String check(String s){
        String returnVal = "NO";

        int count = 0;
        for(int i = 0; i< s.length();i++){
            char a = s.charAt(i);
            if(a =='('){
                count += 1;
            }else{
                count -= 1;
            }
        }

        if(count == 0){
            returnVal = "YES";

        }

        return returnVal;
    }


}
