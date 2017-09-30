package com.soo.logisticstest.Common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static java.lang.Double.parseDouble;

/**
 * Created by 이수연 on 2017-08-01.
 */

public class GoggleMapAPI extends AsyncTask<Void, Void, Boolean> {

    private final String mAddress;
    private final Context context;

    private String data = "";
    String lat = "";
    String lng = "";

    public interface AsyncResponse{
        void processFinish(Double lat, Double lng);
    }

    public AsyncResponse delegate = null;

    public GoggleMapAPI(String Address, Context receiveContext, AsyncResponse delegate) {

//        if(Address.length() > 0){
//            if(Address.contains("물류센터")){
//                Address = Address.replace("물류센터", "우편물류센터");
//            }
//        }

        mAddress = Address;
        context = receiveContext;
        this.delegate = delegate;
    }



    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
            /* 인풋 파라메터값 생성 */
        //  String param = "";
        //  Log.e("POST",param);

        try {
            //request 데이터 생성
            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();

            // convert JSONObject to JSON to String
            json = jsonObject.toString();


                /* 서버연결 */
            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?sensor=false&language=ko&address="+mAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
            OutputStream outs = conn.getOutputStream() ;
            outs.write(json.getBytes("UTF-8"));
            outs.flush();
            outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
            InputStream is = null;
            BufferedReader in = null;


            is = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            String line = null;
            StringBuffer buff = new StringBuffer();
            while ( ( line = in.readLine() ) != null )
            {
                buff.append(line + "\n");
            }
            data = buff.toString().trim();

                /* 서버에서 응답 */
              Log.e("RECV DATA",data);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            //성공해서 응답받아왔을 경우
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(data);
                String status = jsonObj.getString("status");

                if(status.equals("OK")){

                    JSONArray resultsObj = jsonObj.getJSONArray("results");

                    // JSONObject resultsObj = new JSONObject(jsonObj.getString("results"));
                    JSONObject geometryObj = new JSONObject(resultsObj.getJSONObject(0).getString("geometry"));
                    JSONObject locationObj = new JSONObject(geometryObj.getString("location"));

                    lat = locationObj.getString("lat");
                    lng = locationObj.getString("lng");

//                    if(CheckViewChange) {
//                        //위도, 경도를 보내준다.
//                        Intent intent = new Intent(context, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("lat", lat);
//                        intent.putExtra("lng", lng);
//                        intent.putParcelableArrayListExtra("List", TrackingList);
//
//                        context.startActivity(intent);
//                    }
                    Double dLat = Double.parseDouble(lat);
                    Double dLng = Double.parseDouble(lng);
                    delegate.processFinish(dLat, dLng);



                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // mPasswordView.setError(getString(R.string.error_incorrect_password));
            //   mPasswordView.requestFocus();
        }

       // Queue<Integer> qu = new LinkedList<Integer>();
    }

    @Override
    protected void onCancelled() {
        // mAuthTask = null;
        // showProgress(false);
    }



    public Double getLat(){
        return parseDouble(lat);
    }
    public Double getLng(){
        return parseDouble(lng);
    }
}