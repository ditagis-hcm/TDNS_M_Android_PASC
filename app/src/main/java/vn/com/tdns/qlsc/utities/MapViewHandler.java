package vn.com.tdns.qlsc.utities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import vn.com.tdns.qlsc.MainActivity;
import vn.com.tdns.qlsc.async.SingleTapAddFeatureAsync;
import vn.com.tdns.qlsc.async.SingleTapMapViewAsync;
import vn.com.tdns.qlsc.entities.DFeatureLayer;

/**
 * Created by ThanLe on 2/2/2018.
 */

@SuppressLint("Registered")
public class MapViewHandler extends Activity {
    private android.graphics.Point mClickPoint;
    private MapView mMapView;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup mPopUp;
    private MainActivity mMainActivity;


    public MapViewHandler(DFeatureLayer featureLayerDTG, MapView mapView,
                          Popup popupInfos, MainActivity mainActivity) {
        this.mMapView = mapView;
        this.mServiceFeatureTable = (ServiceFeatureTable) featureLayerDTG.getLayer().getFeatureTable();
        this.mPopUp = popupInfos;
        this.mMainActivity = mainActivity;
    }

    public void addFeature(Point pointFindLocation) {
        mClickPoint = mMapView.locationToScreen(pointFindLocation);

        SingleTapAddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAddFeatureAsync(mMainActivity,
                mServiceFeatureTable, output -> {
            if (output != null) {
                mPopUp.showPopup((ArcGISFeature) output, true);
            }
        });
        singleTapAdddFeatureAsync.execute();
    }


    public double[] onScroll(MotionEvent e2) {
        Point center = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
        mClickPoint = new android.graphics.Point((int) e2.getX(), (int) e2.getY());
        return location;
    }

    public void onSingleTapMapView(MotionEvent e) {
        final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());

        SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync(mMainActivity, mPopUp, mClickPoint, mMapView);
        singleTapMapViewAsync.execute(clickPoint);
    }

}