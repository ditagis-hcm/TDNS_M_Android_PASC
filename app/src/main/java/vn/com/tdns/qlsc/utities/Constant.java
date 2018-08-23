package vn.com.tdns.qlsc.utities;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;


/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_VIEW = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");


    public static final String SERVER_API = "http://gis.capnuoccholon.com.vn/cholon/api";
    //    private final String SERVER_API = "http://sawagis.vn/cholon/api";
    public String API_LOGIN;

    {
        API_LOGIN = SERVER_API + "/Login";
    }

    public String DISPLAY_NAME;

    {
        DISPLAY_NAME = SERVER_API + "/Account/Profile";
    }

    public String LAYER_INFO;

    {
        LAYER_INFO = SERVER_API + "/layerinfo";
    }

    public String GENERATE_ID_SUCO;

    {
        GENERATE_ID_SUCO = SERVER_API + "/quanlysuco/generateidsuco/";
    }

    public String IS_ACCESS;

    {
        IS_ACCESS = SERVER_API + "/Account/IsAccess/m_qlsc";
    }

    public class HOSOVATTUSUCO_METHOD {
        public static final int FIND = 0;
        public static final int INSERT = 2;
    }

    public class ACCOUNT_ROLE {
        public static final String QLCN1 = "qlcn1";
        public static final String QLCN2 = "qlcn2";
    }


    private static Constant mInstance = null;

    public static Constant getInstance() {
        if (mInstance == null)
            mInstance = new Constant();
        return mInstance;
    }

    private Constant() {
    }


}
