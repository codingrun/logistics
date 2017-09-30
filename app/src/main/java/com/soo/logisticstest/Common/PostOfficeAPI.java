package com.soo.logisticstest.Common;

import android.content.Context;
import android.os.AsyncTask;

import com.soo.logisticstest.Model.TrackingModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by 이수연 on 2017-08-20.
 */

public class PostOfficeAPI extends AsyncTask<Void, Void, Boolean> {

    String key = "5115dfc34c41d7ff51503207726154";
    String requestURL = "https://biz.epost.go.kr/KpostPortal/openapi?";
    String query = "";


    String data = "";
    private final Context context;
    ArrayList<TrackingModel> TrackingList = new ArrayList<TrackingModel>();

    //https://biz.epost.go.kr/KpostPortal/openapi?regkey=abcdefghijklmn1234567890&target=trace&query=1234567890123

    public interface AsyncResponse{
        void processFinish(boolean check,ArrayList<TrackingModel> TrackingList);
    }
    public AsyncResponse delegate = null;

    public PostOfficeAPI(String DelvNo, Context receiveContext, AsyncResponse delegate){
        query = DelvNo;
        context = receiveContext;
        this.delegate = delegate;

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        String DelvNo = query;
        //String DelvNo = "6863237152818";
        String url = "https://service.epost.go.kr/trace.RetrieveDomRigiTraceList.comm?sid1=";
        String requestURL = url+DelvNo+"&displayHeader=N";


//6863237152818
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
                public X509Certificate[] getAcceptedIssuers(){return new X509Certificate[0];}
                public void checkClientTrusted(X509Certificate[] certs, String authType){}
                public void checkServerTrusted(X509Certificate[] certs, String authType){}
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            Document doc = Jsoup.connect(requestURL).get();
            Elements tableTR = doc.select("table.detail_off tbody tr");


            for (Element e : tableTR) {

                Elements tableTD = e.select("td");

               // Log.e("내용",tableTD.get(2).text());
                TrackingModel model = new TrackingModel();
                model.setARRIVEDATE(tableTD.get(0).text());
                model.setARRIVETIME(tableTD.get(1).text());
                model.setLOCATE(tableTD.get(2).text());
                model.setSTATUS(tableTD.get(3).text());

                TrackingList.add(model);
                data = tableTD.get(2).text();
            }

            if(TrackingList.size() > 0) {
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean success) {

        delegate.processFinish(success, TrackingList);

    }

}
