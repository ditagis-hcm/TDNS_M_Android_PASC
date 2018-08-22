package vn.com.tdns.qlsc.common;

import android.app.Application;

import vn.com.tdns.qlsc.entities.User;

public class DitagisApplication extends Application {
    public Constant getConstant;

    {
        getConstant = new Constant();
    }
    public User userDangNhap;
}
