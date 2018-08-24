package vn.com.tdns.qlsc.common;

import android.Manifest;

import java.text.SimpleDateFormat;

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy");
    public static final SimpleDateFormat DATE_FORMAT_VIEW = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    public static final int REQUEST_CODE_PERMISSION = 2;
    public static final int REQUEST_CODE_BASEMAP = 5;
    public static final int REQUEST_CODE_LAYER = 6;
    public static final int REQUEST_CODE_ADD_FEATURE = 7;
    public static final int REQUEST_CODE_ADD_FEATURE_ATTACHMENT = 8;
    public static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //    public static final String SERVER_API = "http://gis.capnuoccholon.com.vn/cholon/api";
    private final String SERVER_API = "http://sawagis.vn/tdns";
    public String URL_SUCO;

    {
//        URL_SUCO = "http://112.78.5.191:6080/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0";
        URL_SUCO = "https://sawagis.vn/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0";
    }

    public String API_LOGIN;


    {
        API_LOGIN = SERVER_API + "/Login";
    }

    public String DISPLAY_NAME;


    {
        DISPLAY_NAME = SERVER_API + "/Account/Profile";
    }

    public String GENERATE_ID_SUCO;


    {
        GENERATE_ID_SUCO = SERVER_API + "/TiepNhanSuCo/GenerateIDSuCo";
    }

    public String LAYER_INFO;


    {
        LAYER_INFO = SERVER_API + "/layerinfo";
    }

    public String ADMIN_AREA_TPHCM;

    {
        ADMIN_AREA_TPHCM = "Hồ Chí Minh";
    }

    public int SCALE_IMAGE_WITH_LABLES;

    {
        SCALE_IMAGE_WITH_LABLES = 100;
    }

    public int MAX_SCALE_IMAGE_WITH_LABLES;

    {
        MAX_SCALE_IMAGE_WITH_LABLES = 5;
    }

    public class FIELD_SUCO {
        public static final String ID_SUCO = "IDSUCO";
        public static final String TRANG_THAI = "TRANGTHAI";
        public static final String GHI_CHU = "GhiChu";
        public static final String NGUOI_CAP_NHAT = "NGUOICAPNHAT";
        public static final String NGAY_CAP_NHAT = "NGAYCAPNHAT";
        public static final String NGAY_THONG_BAO = "NGAYTHONGBAO";
        public static final String VI_TRI = "VITRI";
        public static final String SDT = "SODIENTHOAI";
        public static final String NGUYEN_NHAN = "NGUYENNHAN";
    }

    public Constant() {
    }
}
