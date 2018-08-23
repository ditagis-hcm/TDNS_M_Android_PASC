package vn.com.tdns.qlsc.utities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import vn.com.tdns.qlsc.MainActivity;
import vn.com.tdns.qlsc.R;
import vn.com.tdns.qlsc.adapter.FeatureViewInfoAdapter;
import vn.com.tdns.qlsc.adapter.FeatureViewMoreInfoAdapter;
import vn.com.tdns.qlsc.async.EditAsync;
import vn.com.tdns.qlsc.async.FindLocationAsycn;
import vn.com.tdns.qlsc.common.Constant;
import vn.com.tdns.qlsc.common.DApplication;
import vn.com.tdns.qlsc.entities.DAddress;

@SuppressLint("Registered")
public class Popup extends AppCompatActivity implements View.OnClickListener {
    private MainActivity mMainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable mServiceFeatureTable;
    private Callout mCallout;
    private List<String> lstFeatureType;
    private FeatureViewMoreInfoAdapter mFeatureViewMoreInfoAdapter;
    private LinearLayout linearLayout;
    private MapView mMapView;
    private Geocoder mGeocoder;
    private String mIDSuCo;
    private Button mBtnLeft;
    private DApplication mApplication;

    public Popup(MainActivity mainActivity, MapView mapView, ServiceFeatureTable serviceFeatureTable,
                 Callout callout, Geocoder geocoder) {
        this.mMainActivity = mainActivity;
        this.mApplication = (DApplication) mainActivity.getApplication();
        this.mMapView = mapView;
        this.mServiceFeatureTable = serviceFeatureTable;
        this.mCallout = callout;
        this.mGeocoder = geocoder;


    }


    public Button getmBtnLeft() {
        return mBtnLeft;
    }

    public Callout getCallout() {
        return mCallout;
    }


    public void refreshPopup(ArcGISFeature arcGISFeature) {
        mSelectedArcGISFeature = arcGISFeature;
        Map<String, Object> attributes = mSelectedArcGISFeature.getAttributes();
        ListView listView = linearLayout.findViewById(R.id.lstview_thongtinsuco);
        FeatureViewInfoAdapter featureViewInfoAdapter = new FeatureViewInfoAdapter(mMainActivity, new ArrayList<>());
        listView.setAdapter(featureViewInfoAdapter);
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        String[] outFieldsArr = mApplication.getDLayerInfo.getOutFieldsArr();
        boolean isFoundField = false;
        mIDSuCo = attributes.get(mMainActivity.getString(R.string.Field_SuCo_IDSuCo)).toString();


        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            for (String outField : outFieldsArr)
                if (outField.equals(field.getName())) {
                    isFoundField = true;
                    break;
                }
            if (!isFoundField) {
                continue;
            }
            isFoundField = false;
            Object value = attributes.get(field.getName());
            FeatureViewInfoAdapter.Item item = new FeatureViewInfoAdapter.Item();

            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
            if (value != null)
                switch (field.getFieldType()) {
                    case DATE:

                        item.setValue(Constant.DATE_FORMAT_VIEW.format(((Calendar) value).getTime()));
                        break;
                    case OID:
                    case TEXT:
                    case SHORT:
                    case DOUBLE:
                    case INTEGER:
                    case FLOAT:
                        item.setValue(value.toString());
                        break;
                }
            featureViewInfoAdapter.add(item);
            featureViewInfoAdapter.notifyDataSetChanged();

        }
    }

    private void viewMoreInfo(final boolean isAddFeature) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        @SuppressLint("InflateParams") final View layout = mMainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);
        mFeatureViewMoreInfoAdapter = new FeatureViewMoreInfoAdapter(mMainActivity, new ArrayList<>());
        final ListView lstViewInfo = layout.findViewById(R.id.lstView_alertdialog_info);
        mBtnLeft = layout.findViewById(R.id.btn_updateinfo_left);
        Button btnRight = layout.findViewById(R.id.btn_updateinfo_right);
        layout.findViewById(R.id.layout_viewmoreinfo_id_su_co).setVisibility(View.VISIBLE);


        lstViewInfo.setAdapter(mFeatureViewMoreInfoAdapter);
        loadDataViewMoreInfo(isAddFeature, layout);
        builder.setView(layout);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (isAddFeature) {
//            mBtnLeft.setText(mMainActivity.getString(R.string.btnLeftAddFeature));
//            btnRight.setText(mMainActivity.getString(R.string.btnRightAddFeature));
            mBtnLeft.setOnClickListener(view -> {
                EditAsync editAsync = new EditAsync(mMainActivity,
                        (ServiceFeatureTable) mApplication.getDFeatureLayer().getLayer().getFeatureTable(),
                        mSelectedArcGISFeature, true, null, arcGISFeature -> {
                    mCallout.dismiss();
                    dialog.dismiss();
                });
                editAsync.execute(mFeatureViewMoreInfoAdapter);

            });
            btnRight.setOnClickListener((View view) -> capture(true));
        } else {
//            mBtnLeft.setText(mMainActivity.getString(R.string.btnLeftUpdateFeature));
//            btnRight.setText(mMainActivity.getString(R.string.btnRightUpdateFeature));
            mBtnLeft.setOnClickListener((View view) -> {
                EditAsync editAsync = new EditAsync(mMainActivity,
                        (ServiceFeatureTable) mApplication.getDFeatureLayer().getLayer().getFeatureTable(),
                        mSelectedArcGISFeature, true, null, (ArcGISFeature arcGISFeature) -> {
                    mCallout.dismiss();
                    dialog.dismiss();
                });
                editAsync.execute(mFeatureViewMoreInfoAdapter);
                btnRight.setOnClickListener(view1 -> capture(false));
            });
            dialog.show();
        }
    }

    private void loadDataViewMoreInfo(boolean isAddFeature, View layout) {
        Map<String, Object> attr = mSelectedArcGISFeature.getAttributes();

        String[] outFields = mApplication.getDLayerInfo.getOutFieldsArr();
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        boolean isFoundContinue = false;
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            Object value = attr.get(field.getName());

            //nếu là nodisplay field thì bỏ qua
            for (String outField : outFields)
                if (!field.getName().equals(outField)) {
                    isFoundContinue = true;
                    break;
                }
            if (isFoundContinue) {
                isFoundContinue = false;
                continue;
            }

            if (field.getName().equals(mMainActivity.getString(R.string.Field_SuCo_IDSuCo))) {
                if (value != null) {
                    mIDSuCo = value.toString();
                    ((TextView) layout.findViewById(R.id.txt_alertdialog_id_su_co)).setText(mIDSuCo);
//                    QueryHoSoVatTuSuCoAsync queryHoSoVatTuSuCoAsync = new QueryHoSoVatTuSuCoAsync(mMainActivity);
//                    queryHoSoVatTuSuCoAsync.doInBackground();
                }
            } else {
                FeatureViewMoreInfoAdapter.Item item = new FeatureViewMoreInfoAdapter.Item();
                item.setAlias(field.getAlias());
                item.setFieldName(field.getName());
                if (value != null) {
                    if (item.getFieldName().equals(typeIdField)) {
                        List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                        Object valueFeatureType = getValueFeatureType(featureTypes, value.toString());
                        if (valueFeatureType != null) {
                            item.setValue(valueFeatureType.toString());
                        }

                    } else if (field.getDomain() != null) {
                        List<CodedValue> codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature
                                .getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();

                        Object valueDomain = getValueDomain(codedValues, value.toString());
                        if (valueDomain != null) item.setValue(valueDomain.toString());
                    } else switch (field.getFieldType()) {
                        case DATE:
                            item.setValue(Constant.DATE_FORMAT_VIEW.format(((Calendar) value).getTime()));
                            break;
                        case OID:
                        case TEXT:
                            item.setValue(value.toString());
                            break;
                        case DOUBLE:
                        case SHORT:
                            item.setValue(value.toString());
                            break;
                    }
                }
                item.setEdit(false);
                item.setFieldType(field.getFieldType());
                mFeatureViewMoreInfoAdapter.add(item);
                mFeatureViewMoreInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    private Object getValueDomain(List<CodedValue> codedValues, String code) {
        Object value = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getCode().toString().equals(code)) {
                value = codedValue.getName();
                break;
            }

        }
        return value;
    }

    private Object getValueFeatureType(List<FeatureType> featureTypes, String code) {
        Object value = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getId().toString().equals(code)) {
                value = featureType.getName();
                break;
            }
        }
        return value;
    }

    public void capture(boolean isAddFeature) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

        File photo = ImageFile.getFile(mMainActivity);
        Uri uri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        mMainActivity.setSelectedArcGISFeature(mSelectedArcGISFeature);
//        mMainActivity.setFeatureViewMoreInfoAdapter(mFeatureViewMoreInfoAdapter);
//        mMainActivity.setUri(uri);
        if (isAddFeature)
            mMainActivity.startActivityForResult(cameraIntent, mMainActivity.getResources().getInteger(R.integer.REQUEST_ID_IMAGE_CAPTURE_ADD_FEATURE));
        else
            mMainActivity.startActivityForResult(cameraIntent, mMainActivity.getResources().getInteger(R.integer.REQUEST_ID_IMAGE_CAPTURE_POPUP));
    }

    private void clearSelection() {
        if (mApplication.getDFeatureLayer() != null) {
            FeatureLayer featureLayer = mApplication.getDFeatureLayer().getLayer();
            featureLayer.clearSelection();
        }
    }

    private void dimissCallout() {
        if (mCallout != null && mCallout.isShowing()) {
            mCallout.dismiss();
        }
    }

    @SuppressLint("InflateParams")
    public void showPopup(final ArcGISFeature selectedArcGISFeature,
                          final boolean isAddFeature) {
        clearSelection();
        dimissCallout();
        this.mSelectedArcGISFeature = selectedArcGISFeature;

        FeatureLayer featureLayer = mApplication.getDFeatureLayer().getLayer();
        featureLayer.selectFeature(mSelectedArcGISFeature);
        lstFeatureType = new ArrayList<>();
        for (int i = 0; i < mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().size(); i++) {
            lstFeatureType.add(mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().get(i).getName());
        }
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_thongtinsuco, null);
        refreshPopup(mSelectedArcGISFeature);
        ((TextView) linearLayout.findViewById(R.id.txt_thongtin_ten)).setText(featureLayer.getName());
        linearLayout.findViewById(R.id.imgBtn_cancel_thongtinsuco).setOnClickListener(view -> mCallout.dismiss());

        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Envelope envelope = mSelectedArcGISFeature.getGeometry().getExtent();
        mMapView.setViewpointGeometryAsync(envelope, 0);
        // show CallOut
        mCallout.setLocation(envelope.getCenter());
        mCallout.setContent(linearLayout);
        this.runOnUiThread(() -> {
            mCallout.refresh();
            mCallout.show();
            if (isAddFeature) {
//                    QueryFeatureAsycn queryFeatureAsycn = new QueryFeatureAsycn(mMainActivity, mServiceFeatureTable, new QueryFeatureAsycn.AsyncResponse() {
//                        @Override
//                        public void processFinish(ArcGISFeature output) {
//
//                        }
//                    });
//                    String idSuCo = "";
//                    Map<String, Object> attr = mSelectedArcGISFeature.getAttributes();
//                    for (Field field : mSelectedArcGISFeature.getFeatureTable().getFields()) {
//                        if (field.getName().equals(mMainActivity.getString(R.string.Field_OBJECTID))) {
//                            idSuCo = attr.get(field.getName()).toString();
//                            break;
//                        }
//                    }
//                    queryFeatureAsycn.execute(idSuCo);
                viewMoreInfo(true);
            }
        });
    }

    @SuppressLint("InflateParams")
    public void showPopupFindLocation(Point position, String location) {
        try {
            if (position == null)
                return;
            clearSelection();
            dimissCallout();

            LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
            linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_timkiemdiachi, null);

            ((TextView) linearLayout.findViewById(R.id.txt_timkiemdiachi)).setText(location);
            linearLayout.findViewById(R.id.imgBtn_timkiemdiachi_themdiemsuco).setOnClickListener(this);
            linearLayout.findViewById(R.id.imgBtn_timkiemdiachi).setOnClickListener(this);
            linearLayout.findViewById(R.id.imgBtn_cancel_thongtinsuco).setOnClickListener(view -> mCallout.dismiss());

            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // show CallOut
            mCallout.setLocation(position);
            mCallout.setContent(linearLayout);
            this.runOnUiThread(() -> {
                mCallout.refresh();
                mCallout.show();
            });
        } catch (Exception e) {
            Log.e("Popup tìm kiếm", e.toString());
        }

    }

    public void showPopupFindLocation(final Point position) {
        try {
            if (position == null)
                return;

            @SuppressLint("InflateParams") FindLocationAsycn findLocationAsycn = new FindLocationAsycn(mMainActivity, false,
                    mGeocoder, output -> {
                if (output != null && output.size() > 0) {
                    clearSelection();
                    dimissCallout();
                    DAddress address = output.get(0);
                    String addressLine = address.getLocation();
                    LayoutInflater inflater = LayoutInflater.from(mMainActivity.getApplicationContext());
                    linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_timkiemdiachi, null);
                    ((TextView) linearLayout.findViewById(R.id.txt_timkiemdiachi)).setText(addressLine);
                    linearLayout.findViewById(R.id.imgBtn_timkiemdiachi_themdiemsuco).setOnClickListener(Popup.this);
                    linearLayout.findViewById(R.id.imgBtn_timkiemdiachi).setOnClickListener(Popup.this);
                    linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    // show CallOut
                    mCallout.setLocation(position);
                    mCallout.setContent(linearLayout);
                    Popup.this.runOnUiThread(() -> {
                        mCallout.refresh();
                        mCallout.show();
                    });
                }
            });
            Geometry project = GeometryEngine.project(position, SpatialReferences.getWgs84());
            double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
            findLocationAsycn.setmLongtitude(location[0]);
            findLocationAsycn.setmLatitude(location[1]);
            findLocationAsycn.execute();
        } catch (Exception e) {
            Log.e("Popup tìm kiếm", e.toString());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.imgBtn_timkiemdiachi:
//                if (mCallout != null && mCallout.isShowing())
//                    mCallout.dismiss();
//                break;
//            case R.id.imgBtn_ViewMoreInfo:
//                viewMoreInfo(false);
//                break;
            case R.id.imgBtn_timkiemdiachi_themdiemsuco:
                mMainActivity.onClick(view);
                break;
        }
    }
}
