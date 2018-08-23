package vn.com.tdns.qlsc.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.com.tdns.qlsc.R;
import vn.com.tdns.qlsc.utities.Constant;
import vn.com.tdns.qlsc.utities.Preference;

public class GenerateIDSuCoAsycn extends AsyncTask<Void, Void, String> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    GenerateIDSuCoAsycn(Context context, AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.message_preparing));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        //Tránh gặp lỗi networkOnMainThread nên phải dùng asyncTask
        String id = "";
        try {
            String API_URL = Constant.getInstance().GENERATE_ID_SUCO ;

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = bufferedReader.readLine();
                id = line.replace("\"", "");
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("Lỗi lấy IDSuCo", e.toString());
        }
        return id;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(String value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }


}
