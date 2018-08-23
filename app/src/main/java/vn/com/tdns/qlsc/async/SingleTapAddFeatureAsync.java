package vn.com.tdns.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.com.tdns.qlsc.R;
import vn.com.tdns.qlsc.common.Constant;
import vn.com.tdns.qlsc.common.DApplication;
import vn.com.tdns.qlsc.utities.MySnackBar;


/**
 * Created by ThanLe on 4/16/2018.
 */

public class SingleTapAddFeatureAsync extends AsyncTask<Void, Feature, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature;
    @SuppressLint("StaticFieldLeak")
    private MapView mMapView;
    private AsyncResponse mDelegate;
    private Geocoder mGeocoder;
    private DApplication mApplication;

    public interface AsyncResponse {
        void processFinish(Feature output);
    }

    public SingleTapAddFeatureAsync(Activity activity,
                                    ServiceFeatureTable serviceFeatureTable, MapView mapView, Geocoder geocoder, AsyncResponse delegate) {
        this.mServiceFeatureTable = serviceFeatureTable;
        this.mMapView = mapView;
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        this.mDialog = new ProgressDialog(activity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDelegate = delegate;
        this.mGeocoder = geocoder;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
//        final Point clickPoint = params[0];

        final Feature feature;
        try {
            feature = mServiceFeatureTable.createFeature();
            feature.setGeometry(mApplication.getDiemSuCo.getPoint());
            feature.getAttributes().put(Constant.FIELD_SUCO.VI_TRI, mApplication.getDiemSuCo.getVitri());
            feature.getAttributes().put(Constant.FIELD_SUCO.GHI_CHU, mApplication.getDiemSuCo.getGhiChu());
            feature.getAttributes().put(Constant.FIELD_SUCO.NGUOI_CAP_NHAT, mApplication.getDiemSuCo.getNguoiCapNhat());
            feature.getAttributes().put(Constant.FIELD_SUCO.SDT, mApplication.getDiemSuCo.getSdt());
            addFeatureAsync(feature);

        } catch (Exception e) {
            MySnackBar.make(mMapView, mActivity.getString(R.string.message_error_add_feature), true);
            publishProgress();
        }


        return null;
    }

    private void addFeatureAsync(final Feature feature) {
        new GenerateIDSuCoAsycn(mActivity, output -> {
            output = "1_23_8_2018";
            if (output.isEmpty()) {
                publishProgress();
                return;
            }
            feature.getAttributes().put(Constant.FIELD_SUCO.ID_SUCO, output);
            Short intObj = (short) 0;
            feature.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI, intObj);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Calendar c = Calendar.getInstance();
                feature.getAttributes().put(Constant.FIELD_SUCO.NGAY_CAP_NHAT, c);
                feature.getAttributes().put(Constant.FIELD_SUCO.NGAY_THONG_BAO, c);
            }
//            final ListenableFuture<List<IdentifyLayerResult>> listListenableFuture = mMapView.identifyLayersAsync(mClickPoint, 5, false, 1);
//            listListenableFuture.addDoneListener(() -> {
//                List<IdentifyLayerResult> identifyLayerResults;
//                try {
//                    identifyLayerResults = listListenableFuture.get();
//                    for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
//                        {
//                            List<GeoElement> elements = identifyLayerResult.getElements();
//                            if (elements.size() > 0) {
//                                if (elements.get(0) instanceof ArcGISFeature) {
//                                    mSelectedArcGISFeature = (ArcGISFeature) elements.get(0);
//                                }
//                            }
//                        }
//                    }
////                        publishProgress(null);
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            });
            mServiceFeatureTable.addFeatureAsync(feature).addDoneListener(() -> {
                final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mServiceFeatureTable.applyEditsAsync();
                listListenableEditAsync.addDoneListener(() -> {
                    try {
                        List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                        if (featureEditResults.size() > 0) {
                            long objectId = featureEditResults.get(0).getObjectId();
                            final QueryParameters queryParameters = new QueryParameters();
                            final String query = String.format(mActivity.getString(R.string.arcgis_query_by_OBJECTID), objectId);
                            queryParameters.setWhereClause(query);
                            final ListenableFuture<FeatureQueryResult> featuresAsync = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                            featuresAsync.addDoneListener(() -> {
                                try {
                                    FeatureQueryResult result = featuresAsync.get();
                                    if (result.iterator().hasNext()) {
                                        Feature item = result.iterator().next();
                                        publishProgress(item);
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }

                            });
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        publishProgress();
                        e.printStackTrace();
                    }

                });
            });
        }).execute();
    }

    @Override
    protected void onProgressUpdate(Feature... values) {
        if (values == null)
            this.mDelegate.processFinish(null);
        else if (values.length > 0) this.mDelegate.processFinish(values[0]);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    @Override
    protected void onPostExecute(Void result) {


    }

}