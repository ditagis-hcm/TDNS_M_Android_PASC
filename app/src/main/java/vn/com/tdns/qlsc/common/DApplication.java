package vn.com.tdns.qlsc.common;

import android.app.Application;

import vn.com.tdns.qlsc.entities.DFeatureLayer;
import vn.com.tdns.qlsc.entities.DLayerInfo;
import vn.com.tdns.qlsc.entities.DiemSuCo;
import vn.com.tdns.qlsc.entities.User;

public class DApplication extends Application {
    public Constant getConstant;

    {
        getConstant = new Constant();
    }

    public DiemSuCo getDiemSuCo;

    {
        getDiemSuCo = new DiemSuCo();
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
        String[] outFieldsArr = new String[]{
                Constant.FIELD_SUCO.ID_SUCO,
                Constant.FIELD_SUCO.VI_TRI,
                Constant.FIELD_SUCO.GHI_CHU,
                Constant.FIELD_SUCO.NGAY_CAP_NHAT,
                Constant.FIELD_SUCO.NGUYEN_NHAN
        };
        String[] addFieldsArr = new String[]{
                Constant.FIELD_SUCO.ID_SUCO,
                Constant.FIELD_SUCO.VI_TRI,
                Constant.FIELD_SUCO.GHI_CHU,
                Constant.FIELD_SUCO.NGAY_CAP_NHAT,
                Constant.FIELD_SUCO.NGUYEN_NHAN,
                Constant.FIELD_SUCO.TRANG_THAI,
                Constant.FIELD_SUCO.NGUOI_CAP_NHAT,
                Constant.FIELD_SUCO.NGAY_THONG_BAO,
                Constant.FIELD_SUCO.SDT
        };
        getDLayerInfo = new DLayerInfo("", "Sự cố",
                getConstant.URL_SUCO,
                true, false, false, true, null, outFieldsArr, addFieldsArr);
    }

}
