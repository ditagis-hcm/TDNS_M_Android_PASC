package vn.com.tdns.qlsc.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.com.tdns.qlsc.R;
import vn.com.tdns.qlsc.common.DitagisApplication;
import vn.com.tdns.qlsc.common.Preference;
import vn.com.tdns.qlsc.entities.User;


public class LoginAsycn extends AsyncTask<String, Void, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private LoginAsycn.AsyncResponse mDelegate;
    private DitagisApplication mApplication;

    public interface AsyncResponse {
        void processFinish();
    }

    public LoginAsycn(Context context, LoginAsycn.AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
        mApplication = (DitagisApplication) context.getApplicationContext();
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.connect_message));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String userName = params[0];
        String pin = params[1];
//        String passEncoded = (new EncodeMD5()).encode(pin + "_DITAGIS");
        // Do some validation here
        String urlParameters = String.format("Username=%s&Password=%s", userName, pin);
        String urlWithParam = String.format("%s?%s", mApplication.getConstant.API_LOGIN, urlParameters);
        try {
//            + "&apiKey=" + API_KEY
            URL url = new URL(urlWithParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    break;
                }
                Preference.getInstance().savePreferences(mContext.getString(R.string.preference_login_api), stringBuilder.toString().replace("\"", ""));
                bufferedReader.close();

                mApplication.userDangNhap = new User();
                mApplication.userDangNhap.setDisplayName(getDisplayName());
            }catch (Exception e1){
                Log.e("Lỗi login", e1.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);

        }finally {
            return null;
        }

    }



    @Override
    protected void onPostExecute(Void aVoid) {
//        if (user != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish();
//        }
    }

    private String getDisplayName() {

//        String API_URL = "http://sawagis.vn/tanhoa1/api/Account/Profile";
        String displayName = "";
        try {
            URL url = new URL(mApplication.getConstant.DISPLAY_NAME);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    displayName = pajsonRouteeJSon(line);

                    break;
                }

            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        } finally {
            return displayName;
        }
    }

    private String pajsonRouteeJSon(String data) throws JSONException {
        if (data == null)
            return "";
        String displayName = "";
        String myData = "{ \"account\": [".concat(data).concat("]}");
        JSONObject jsonData = new JSONObject(myData);
        JSONArray jsonRoutes = jsonData.getJSONArray("account");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            displayName = jsonRoute.getString(mContext.getString(R.string.sql_column_login_displayname));
        }
        return displayName;

    }
}
