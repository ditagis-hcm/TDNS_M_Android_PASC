package vn.com.tdns.qlsc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.tdns.qlsc.adapter.AnnotationAdapter;
import vn.com.tdns.qlsc.adapter.TraCuuAdapter;
import vn.com.tdns.qlsc.async.FindLocationAsycn;
import vn.com.tdns.qlsc.common.Constant;
import vn.com.tdns.qlsc.common.DApplication;
import vn.com.tdns.qlsc.entities.DAddress;
import vn.com.tdns.qlsc.entities.DFeatureLayer;
import vn.com.tdns.qlsc.utities.MapViewHandler;
import vn.com.tdns.qlsc.utities.MySnackBar;
import vn.com.tdns.qlsc.utities.Popup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.mapViewMainActivity)
    MapView mMapView;
    @BindView(R.id.imgBtn_main_annotation)
    ImageButton mImgBtnMenu;
    @BindView(R.id.imgBtn_main_search)
    ImageButton mImgBtnSearch;
    @BindView(R.id.imgBtn_main_location)
    ImageButton mImgBtnLocation;
    @BindView(R.id.llayout_main_location)
    LinearLayout mLayoutLocation;
    @BindView(R.id.flayout_main_flag_location)
    FrameLayout mLayoutFlagLocation;
    @BindView(R.id.lstview_search)
    ListView mLstViewSearch;
    @BindView(R.id.lstview_anotation)
    ListView mLstViewAnnotation;
    @BindView(R.id.llayout_annotation)
    LinearLayout mLLayoutAnnotation;
    private DApplication mApplication;
    private SearchView mTxtSearchView;
    private boolean mmIsLocating = false;
    private boolean mmIsSearching = false;
    private MenuItem mMenuSearch;
    private MapViewHandler mMapViewHandler;
    private Popup mPopUp;
    private Geocoder mGeocoder;
    private GraphicsOverlay mGraphicsOverlay;
    private Point mPointFindLocation;
    private boolean mIsAddFeature;
    private TraCuuAdapter mSearchAdapter;
    private LocationDisplay mLocationDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApplication = (DApplication) getApplication();
        ButterKnife.bind(this);
        startMain();
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    private void startMain() {
        setToolbar();
        setLicense();
        setTitle(getString(R.string.app_name));
        mGeocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        mMapView.setMap(new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 10.7554041, 106.6546293, 12));

        mMapView.getMap().addDoneLoadingListener(this::handlingMapViewDoneLoading);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handlingMapViewDoneLoading() {
        setTooltipText();
        mLocationDisplay = mMapView.getLocationDisplay();
        mSearchAdapter = new TraCuuAdapter(MainActivity.this, new ArrayList<>());
        mLstViewSearch.setAdapter(mSearchAdapter);
        mLstViewSearch.invalidate();
        mLstViewSearch.setOnItemClickListener(this);
        mApplication = (DApplication) getApplication();

        final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(mApplication.getDLayerInfo.getUrl());
        final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
//        featureLayer.setDefinitionExpression(null);
        featureLayer.setName(mApplication.getDLayerInfo.getTitleLayer());
        featureLayer.setMaxScale(0);
        featureLayer.setMinScale(1000000);
        featureLayer.setPopupEnabled(true);
        featureLayer.addDoneLoadingListener(() -> {


            DFeatureLayer dFeatureLayer = new DFeatureLayer(featureLayer, mApplication.getDLayerInfo);
            mApplication.setDFeatureLayer(dFeatureLayer);
            Callout callout = mMapView.getCallout();
            mPopUp = new Popup(MainActivity.this, mMapView, serviceFeatureTable, callout, mGeocoder);


            mMapViewHandler = new MapViewHandler(dFeatureLayer, callout, mMapView, mPopUp,
                    MainActivity.this, mGeocoder);
            initAnnotation();

        });
        mMapView.getMap().getOperationalLayers().add(featureLayer);
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                try {
                    if (mMapViewHandler != null)
                        mMapViewHandler.onSingleTapMapView(e);
                } catch (ArcGISRuntimeException ex) {
                    Log.d("", ex.toString());
                }
                return super.onSingleTapConfirmed(e);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mIsAddFeature && mMapViewHandler != null) {
                    //center is x, y
                    Point center = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();

                    //project is long, lat
//                    Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());

                    //geometry is x,y
//                    Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
                    SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20);
                    Graphic graphic = new Graphic(center, symbol);
                    mGraphicsOverlay.getGraphics().clear();
                    mGraphicsOverlay.getGraphics().add(graphic);
                    double[] location = mMapViewHandler.onScroll(e2);

                    mPopUp.getCallout().setLocation(center);
                    mPointFindLocation = center;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });
    }

    private void initAnnotation() {
        AnnotationAdapter adapter = new AnnotationAdapter(this, new ArrayList<>());
        Feature feature = mApplication.getDFeatureLayer().getLayer().getFeatureTable().createFeature();
        Symbol symbol_khong = mApplication.getDFeatureLayer().getLayer().getRenderer().getSymbol(feature);
        feature.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI, (short) 0);
        Symbol symbol_chua = mApplication.getDFeatureLayer().getLayer().getRenderer().getSymbol(feature);
        feature.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI, (short) 1);
        Symbol symbol_dang = mApplication.getDFeatureLayer().getLayer().getRenderer().getSymbol(feature);
        feature.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI, (short) 2);
        Symbol symbol = mApplication.getDFeatureLayer().getLayer().getRenderer().getSymbol(feature);
        Bitmap bitmap = null;
        Bitmap bitmap_chua = null;
        Bitmap bitmap_dang = null;
        Bitmap bitmap_da = null;
        try {
            bitmap = symbol_khong.createSwatchAsync(this, ContextCompat.getColor(this, R.color.colorWhite)).get();
            bitmap_chua = symbol_chua.createSwatchAsync(this, ContextCompat.getColor(this, R.color.colorWhite)).get();
            bitmap_dang = symbol_dang.createSwatchAsync(this, ContextCompat.getColor(this, R.color.colorWhite)).get();
            bitmap_da = symbol.createSwatchAsync(this, ContextCompat.getColor(this, R.color.colorWhite)).get();

            adapter.add(new AnnotationAdapter.Item(bitmap, "Chưa xác định"));
            adapter.add(new AnnotationAdapter.Item(bitmap_chua, "Chưa sửa chữa"));
            adapter.add(new AnnotationAdapter.Item(bitmap_dang, "Đang sửa chữa"));
            adapter.add(new AnnotationAdapter.Item(bitmap_da, "Đã sửa chữa"));
            adapter.notifyDataSetChanged();
            mLstViewAnnotation.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

//        adapter.add();
    }

    private void setViewPointCenter(final Point position) {
        if (mPopUp == null) {
            MySnackBar.make(mMapView, getString(R.string.message_unloaded_map), true);
        } else {
            final Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
            final ListenableFuture<Boolean> booleanListenableFuture = mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
            booleanListenableFuture.addDoneListener(() -> {
                try {
                    if (booleanListenableFuture.get()) {
                        MainActivity.this.mPointFindLocation = position;
                    }
                    mPopUp.showPopupFindLocation(position);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            });
        }

    }

    private void setViewPointCenterLongLat(Point position, String location) {
        if (mPopUp == null) {
            MySnackBar.make(mMapView, getString(R.string.message_unloaded_map), true);
        } else {
            Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWgs84());
            Geometry geometry1 = GeometryEngine.project(geometry, SpatialReferences.getWebMercator());
            Point point = geometry1.getExtent().getCenter();

            SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20);
            Graphic graphic = new Graphic(point, symbol);
            mGraphicsOverlay.getGraphics().add(graphic);

            mMapView.setViewpointCenterAsync(point, mApplication.getConstant.MAX_SCALE_IMAGE_WITH_LABLES);
            mPopUp.showPopupFindLocation(point, location);
            this.mPointFindLocation = point;
        }

    }

    private void deleteSearching() {
        mGraphicsOverlay.getGraphics().clear();
        mSearchAdapter.clear();
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.activity_main_action_bar, menu);
        mMenuSearch = menu.findItem(R.id.menu_search);
        mTxtSearchView = (SearchView) mMenuSearch.getActionView();
        mTxtSearchView.setQueryHint(getString(R.string.title_search));
        mTxtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    if (query.length() > 0) {
                        deleteSearching();
                        FindLocationAsycn findLocationAsycn = new FindLocationAsycn(MainActivity.this,
                                true, mGeocoder, output -> {
                            if (output != null) {
                                mSearchAdapter.notifyDataSetChanged();
                                if (output.size() > 0) {
                                    for (DAddress address : output) {
                                        TraCuuAdapter.Item item = new TraCuuAdapter.Item(-1, "", 1, "", address.getLocation());
                                        item.setLatitude(address.getLatitude());
                                        item.setLongtitude(address.getLongtitude());
                                        mSearchAdapter.add(item);
                                    }
                                    mSearchAdapter.notifyDataSetChanged();

                                    //                                    }
                                }
                            }

                        });
                        findLocationAsycn.execute(query);
                    }
                } catch (Exception e) {
                    Log.e("Lỗi tìm kiếm", e.toString());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 0) {
                    mIsAddFeature = true;
                } else {
                    mIsAddFeature = false;
                    mSearchAdapter.clear();
                    mSearchAdapter.notifyDataSetChanged();
                    mGraphicsOverlay.getGraphics().clear();
                }
                return false;
            }
        });
        menu.findItem(R.id.menu_search).

                setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mmIsSearching = true;
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mmIsSearching = false;
                        return true;
                    }
                });
        return true;
    }


    private void setTooltipText() {
        TooltipCompat.setTooltipText(mImgBtnMenu, "Chú thích");
        TooltipCompat.setTooltipText(mImgBtnLocation, "Vị trí");
        TooltipCompat.setTooltipText(mImgBtnSearch, "Tìm kiếm");
    }

    private void setLicense() {
        //way 1
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license));
        //way 2
//        UserCredential credential = new UserCredential("thanle95", "Gemini111");
//
//// replace the URL with either the ArcGIS Online URL or your portal URL
//        final Portal portal = new Portal("https://than-le.maps.arcgis.com");
//        portal.setCredential(credential);
//
//// load portal and listen to done loading event
//        portal.loadAsync();
//        portal.addDoneLoadingListener(new Runnable() {
//            @Override
//            public void run() {
//                LicenseInfo licenseInfo = portal.getPortalInfo().getLicenseInfo();
//                // Apply the license at Standard level
//                ArcGISRuntimeEnvironment.setLicense(licenseInfo);
//            }
//        });
    }

    private void disableLocation() {
        mLocationDisplay.stop();
        mLayoutLocation.setVisibility(View.GONE);
        mLayoutFlagLocation.setBackgroundColor(Color.RED);
        mmIsLocating = false;
        mPopUp.getCallout().dismiss();
    }

    private void enableLocation() {
       if (!mLocationDisplay.isStarted()) {
            mLocationDisplay.startAsync();
            setViewPointCenter(mLocationDisplay.getMapLocation());
           mmIsLocating = true;
           mLayoutLocation.setVisibility(View.VISIBLE);
           mLayoutFlagLocation.setBackgroundColor(Color.GREEN);
           mIsAddFeature = true;
        }

    }

    private void themDiemSuCoNoCapture() {
        FindLocationAsycn findLocationAsycn = new FindLocationAsycn(this, false,
                mGeocoder, output -> {
            if (output != null) {
                String subAdminArea = output.get(0).getSubAdminArea();
                //nếu tài khoản có quyền truy cập vào
//                if (subAdminArea.equals(getString(R.string.Quan5Name)) ||
//                        subAdminArea.equals(getString(R.string.Quan6Name)) ||
//                        subAdminArea.equals(getString(R.string.Quan8Name)) ||
//                        subAdminArea.equals(getString(R.string.QuanBinhTanName)))
                {
                    mTxtSearchView.setQuery("", true);
                    mMapViewHandler.addFeature(null, mPointFindLocation);
                    deleteSearching();
                }
//                else{
//                    Toast.makeText(QuanLySuCo.this, R.string.message_not_area_management, Toast.LENGTH_LONG).show();
//                }
            } else {
                Toast.makeText(this, R.string.message_not_area_management, Toast.LENGTH_LONG).show();
            }

        });
        Geometry project = GeometryEngine.project(mPointFindLocation, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
        findLocationAsycn.setmLongtitude(location[0]);
        findLocationAsycn.setmLatitude(location[1]);
        findLocationAsycn.execute();
    }

    private void showHideAnnotation() {
        if (mLLayoutAnnotation.getVisibility() == View.VISIBLE) {
            mLLayoutAnnotation.setVisibility(View.GONE);
        } else mLLayoutAnnotation.setVisibility(View.VISIBLE);
    }

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_main_annotation:
                showHideAnnotation();
                break;
            case R.id.imgBtn_main_location:
                if (mmIsLocating) {
                    disableLocation();

                } else {
                    enableLocation();
                }
                break;
            case R.id.imgBtn_main_search:
                if (!mmIsSearching) {
                    mMenuSearch.expandActionView();
                } else mMenuSearch.collapseActionView();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_BASEMAP:
                break;
            case Constant.REQUEST_CODE_LAYER:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TraCuuAdapter.Item item = ((TraCuuAdapter.Item) adapterView.getItemAtPosition(i));

        setViewPointCenterLongLat(new Point(item.getLongtitude(), item.getLatitude()), item.getDiaChi());
        Log.d("Tọa độ tìm kiếm", String.format("[% ,.9f;% ,.9f]", item.getLongtitude(), item.getLatitude()));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_timkiemdiachi_themdiemsuco:
//                themDiemSuCo();
                themDiemSuCoNoCapture();
                break;
        }
    }
}
