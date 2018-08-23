package vn.com.tdns.qlsc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.tdns.qlsc.common.DApplication;

public class AddFeatureActivity extends AppCompatActivity {
    @BindView(R.id.etxtFullName_add_feature)
    EditText etxtFullName;
    @BindView(R.id.etxtPhoneNumber_add_feature)
    EditText etxtPhoneNumber;
    @BindView(R.id.etxtAddress_add_feature)
    EditText etxtAddress;
    @BindView(R.id.etxtNote_add_feature)
    EditText etxtNote;
    private DApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feature);
        mApplication = (DApplication) getApplication();
        ButterKnife.bind(this);

        etxtAddress.setText(mApplication.getDiemSuCo.getVitri());

    }

    private boolean isNotEmpty() {
        return !etxtFullName.getText().toString().trim().isEmpty() &&
                !etxtPhoneNumber.getText().toString().trim().isEmpty() &&
                !etxtAddress.getText().toString().trim().isEmpty() &&
                !etxtNote.getText().toString().trim().isEmpty();
    }

    private void handlingEmpty() {
        Toast.makeText(this, "Thiếu thông tin!!!", Toast.LENGTH_SHORT).show();
    }

    public void onClickTextView(View view) {
        switch (view.getId()) {
            case R.id.txt_add_feature_add_feature:
                if (isNotEmpty()) {
                    mApplication.getDiemSuCo.setNguoiCapNhat(etxtFullName.getText().toString());
                    mApplication.getDiemSuCo.setSdt(etxtPhoneNumber.getText().toString());
                    mApplication.getDiemSuCo.setVitri(etxtAddress.getText().toString());
                    mApplication.getDiemSuCo.setGhiChu(etxtNote.getText().toString());
                    finish();
                } else {
                    handlingEmpty();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mApplication.getDiemSuCo.setPoint(null);
        finish();
    }
}
