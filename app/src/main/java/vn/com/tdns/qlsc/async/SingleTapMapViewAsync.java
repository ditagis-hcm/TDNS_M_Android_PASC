package vn.com.tdns.qlsc.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.com.tdns.qlsc.MainActivity;
import vn.com.tdns.qlsc.common.DApplication;
import vn.com.tdns.qlsc.entities.DFeatureLayer;
import vn.com.tdns.qlsc.utities.Popup;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class SingleTapMapViewAsync extends AsyncTask<Point, DFeatureLayer, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private MainActivity mMainActivity;
    @SuppressLint("StaticFieldLeak")
    private MapView mMapView;
    private ArcGISFeature mSelectedArcGISFeature;
    @SuppressLint("StaticFieldLeak")
    private Popup mPopUp;
    private android.graphics.Point mClickPoint;
    private boolean isFound = false;

    public SingleTapMapViewAsync(MainActivity mainActivity, Popup popup,
                                 android.graphics.Point clickPoint, MapView mapview) {
        this.mMapView = mapview;
        this.mPopUp = popup;
        this.mClickPoint = clickPoint;
        this.mMainActivity = mainActivity;
        this.mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected Void doInBackground(Point... points) {
        final ListenableFuture<List<IdentifyLayerResult>> listListenableFuture = mMapView
                .identifyLayersAsync(mClickPoint, 5, false);
        listListenableFuture.addDoneListener(() -> {
            List<IdentifyLayerResult> identifyLayerResults;
            try {
                identifyLayerResults = listListenableFuture.get();
                for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
                    {
                        List<GeoElement> elements = identifyLayerResult.getElements();
                        if (elements.size() > 0 && elements.get(0) instanceof ArcGISFeature && !isFound) {
                            isFound = true;
                            mSelectedArcGISFeature = (ArcGISFeature) elements.get(0);
                            publishProgress();
                            publishProgress(((DApplication)mMainActivity.getApplication()).getDFeatureLayer());
                        }
                    }
                }
                publishProgress();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", (dialogInterface, i) -> publishProgress());
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(DFeatureLayer... values) {
        super.onProgressUpdate(values);
        if (values != null && values.length > 0 && mSelectedArcGISFeature != null) {

            DFeatureLayer featureLayerDTG = values[0];
            mPopUp.showPopup(mSelectedArcGISFeature, false);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        } else if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}