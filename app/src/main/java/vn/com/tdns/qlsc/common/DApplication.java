package vn.com.tdns.qlsc.common;

import android.app.Application;

import vn.com.tdns.qlsc.entities.DFeatureLayer;
import vn.com.tdns.qlsc.entities.DLayerInfo;
import vn.com.tdns.qlsc.entities.User;

public class DApplication extends Application {
    public Constant getConstant;

    {
        getConstant = new Constant();
    }

    public User userDangNhap;
    private DFeatureLayer mDFeatureLayer;

    public DFeatureLayer getDFeatureLayer() {
        return mDFeatureLayer;
    }

    public void setDFeatureLayer(DFeatureLayer dFeatureLayer) {
        this.mDFeatureLayer = dFeatureLayer;
    }

    public DLayerInfo getDLayerInfo;

    {
        getDLayerInfo = new DLayerInfo("", "Sự cố",
                "http://sawagis.vn/arcgis/rest/services/TruyenDan/TruyenDanDiemSuCo/FeatureServer/0",
                true, false, false, true, null,
                "IDSUCO,GhiChu,NGAYCAPNHAT,VITRI,NGUYENNHAN",
                "IDSUCO,TRANGTHAI,GhiChu,NGUOICAPNHAT,NGAYCAPNHAT,NGAYTHONGBAO,VITRI,SODIENTHOAI,NGUYENNHAN");
    }
}
