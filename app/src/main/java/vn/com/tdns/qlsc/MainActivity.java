package vn.com.tdns.qlsc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
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
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.tdns.qlsc.common.Constant;
import vn.com.tdns.qlsc.common.DApplication;
import vn.com.tdns.qlsc.entities.DFeatureLayer;
import vn.com.tdns.qlsc.utities.MapViewHandler;
import vn.com.tdns.qlsc.utities.MySnackBar;
import vn.com.tdns.qlsc.utities.Popup;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.mapViewMainActivity)
    MapView mMapView;
    @BindView(R.id.imgBtn_main_menu)
    ImageButton mImgBtnMenu;
    @BindView(R.id.imgBtn_main_add)
    ImageButton mImgBtnAdd;
    @BindView(R.id.imgBtn_main_bookmark)
    ImageButton mImgBtnBookmark;
    @BindView(R.id.imgBtn_main_search)
    ImageButton mImgBtnSearch;
    @BindView(R.id.imgBtn_main_location)
    ImageButton mImgBtnLocation;
    @BindView(R.id.llayout_main_location)
    LinearLayout mLayoutLocation;
    @BindView(R.id.flayout_main_flag_location)
    FrameLayout mLayoutFlagLocation;
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
        DApplication dApplication = (DApplication) getApplication();

        final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(dApplication.getDLayerInfo.getUrl());
        final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
//        featureLayer.setDefinitionExpression(null);
        featureLayer.setName(dApplication.getDLayerInfo.getTitleLayer());
        featureLayer.setMaxScale(0);
        featureLayer.setMinScale(1000000);
        featureLayer.setPopupEnabled(true);

        featureLayer.addDoneLoadingListener(() -> {
            DFeatureLayer dFeatureLayer = new DFeatureLayer(featureLayer, dApplication.getDLayerInfo);
            dApplication.setDFeatureLayer(dFeatureLayer);
            Callout callout = mMapView.getCallout();
            mPopUp = new Popup(MainActivity.this, mMapView, serviceFeatureTable, callout, mGeocoder);


            mMapViewHandler = new MapViewHandler(dFeatureLayer, callout, mMapView, mPopUp,
                    MainActivity.this, mGeocoder);

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
                } catch (Exception e) {
                    Log.e("Lỗi tìm kiếm", e.toString());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
        TooltipCompat.setTooltipText(mImgBtnMenu, "Menu");
        TooltipCompat.setTooltipText(mImgBtnAdd, "Thêm");
        TooltipCompat.setTooltipText(mImgBtnBookmark, "Đánh dấu");
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
        mLayoutLocation.setVisibility(View.GONE);
        mLayoutFlagLocation.setBackgroundColor(Color.RED);
        mmIsLocating = false;
    }

    private void enableLocation() {
        mmIsLocating = true;
        mLayoutLocation.setVisibility(View.VISIBLE);
        mLayoutFlagLocation.setBackgroundColor(Color.GREEN);
    }

    private void showMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.activity_main, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_measure:
                    return true;
                case R.id.item_basemap:
                    Intent intent = new Intent(this, BasemapActivity.class);
                    this.startActivityForResult(intent, Constant.REQUEST_CODE_BASEMAP);

                    return true;
                case R.id.item_feature:
                    Intent intentLayer = new Intent(this, LayerActivity.class);
                    this.startActivityForResult(intentLayer, Constant.REQUEST_CODE_LAYER);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        });
        popup.show();
    }

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_main_menu:
                showMenu(view);
                break;
            case R.id.imgBtn_main_location:
                if (mmIsLocating) {
                    disableLocation();

                } else {
                    enableLocation();
                }
                break;
            case R.id.imgBtn_main_add:
                break;
            case R.id.imgBtn_main_bookmark:
                Intent intentBookmark = new Intent(this, BookmarkActivity.class);
                this.startActivityForResult(intentBookmark, Constant.REQUEST_CODE_LAYER);
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
}
