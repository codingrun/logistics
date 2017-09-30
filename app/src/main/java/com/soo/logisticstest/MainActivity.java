package com.soo.logisticstest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.soo.logisticstest.Common.GoggleMapAPI;
import com.soo.logisticstest.Model.TrackingModel;

import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

/**
 * Created by 이수연 on 2017-05-28.
 */

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, AdapterView.OnItemClickListener {

    private double lat = 0;
    private double lng = 0;
    TextView recive = null;

    ArrayList<TrackingModel> TrackList;
    MapView mapView;

    private static CameraPosition CAMERA_POSITION_CURRENT = null;
    Double selectlat = 0.0;
    Double selectlng = 0.0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        TrackList = intent.getParcelableArrayListExtra("List");

        TrackListViewAdapter adapter = new TrackListViewAdapter(TrackList);
        ListView listView = (ListView)findViewById(R.id.TrackList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);


        //경도 위도 가져옴
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        String invoiceNo = intent.getStringExtra("invoiceNo");
        recive = (TextView)findViewById(R.id.data);
        recive.setText("송장번호 "+invoiceNo);

       //다음 지도 가져오는 메소드 넣기--------------------

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true);

    }

    //리스트 눌렀을 경우
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        //장소 이름 가져와 좌표 다시 가져오기
        final TrackingModel trackModel = TrackList.get(TrackList.size()-(position+1));
        String data = trackModel.getLOCATE();


        GoggleMapAPI api = (GoggleMapAPI) new GoggleMapAPI(data, getBaseContext(), new GoggleMapAPI.AsyncResponse() {
            @Override
            public void processFinish(Double lat, Double lng) {
                selectlat = lat;
                selectlng = lng;

                CAMERA_POSITION_CURRENT = new CameraPosition(MapPoint.mapPointWithGeoCoord(selectlat, selectlng), 2);
                mapView.animateCamera(CameraUpdateFactory.newCameraPosition(CAMERA_POSITION_CURRENT), 1000, new CancelableCallback() {
                    @Override
                    public void onFinish() {
                        Toast.makeText(getBaseContext(), "complete", Toast.LENGTH_SHORT).show();

                        MapPoint MAP_POINT_POI2 = MapPoint.mapPointWithGeoCoord(selectlat, selectlng);
                        MapPOIItem poiItem2 = new MapPOIItem();
                        poiItem2.setItemName(trackModel.getLOCATE());
                        poiItem2.setMapPoint(MAP_POINT_POI2);
                        poiItem2.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        poiItem2.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);
                        mapView.addPOIItem(poiItem2);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getBaseContext(), "canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).execute((Void) null);

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        MapPoint MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(lat, lng);
        MapPOIItem poiItem1 = new MapPOIItem();
        TrackingModel trackModel = TrackList.get(TrackList.size()-1);
        poiItem1.setItemName(trackModel.getLOCATE());
        poiItem1.setMapPoint(MAP_POINT_POI1);
        poiItem1.setMarkerType(MapPOIItem.MarkerType.BluePin);
        poiItem1.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem(poiItem1);

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

}
