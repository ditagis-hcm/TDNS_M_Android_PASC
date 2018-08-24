package vn.com.tdns.qlsc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.tdns.qlsc.common.Constant;
import vn.com.tdns.qlsc.common.DApplication;
import vn.com.tdns.qlsc.utities.ImageFile;

public class AddFeatureActivity extends AppCompatActivity {
    @BindView(R.id.etxtFullName_add_feature)
    EditText etxtFullName;
    @BindView(R.id.etxtPhoneNumber_add_feature)
    EditText etxtPhoneNumber;
    @BindView(R.id.etxtAddress_add_feature)
    EditText etxtAddress;
    @BindView(R.id.etxtNote_add_feature)
    EditText etxtNote;
    @BindView(R.id.img_add_feature)
    ImageView mImage;
    private DApplication mApplication;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feature);
        mApplication = (DApplication) getApplication();
        ButterKnife.bind(this);

        etxtAddress.setText(mApplication.getDiemSuCo.getVitri());

        //for camera
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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

    public void capture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

        File photo = ImageFile.getFile(this);
//        this.mUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", photo);
        mUri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
//        this.mUri = Uri.fromFile(photo);
        try {
            this.startActivityForResult(cameraIntent, Constant.REQUEST_CODE_ADD_FEATURE_ATTACHMENT);
        } catch (Exception e) {
            Log.e("Lỗi chụp ảnh", e.toString());
        }
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

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.btnAttachemnt_add_feature:
                capture();
                break;
        }
    }

    @Nullable
    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            assert in != null;
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            assert in != null;
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_ADD_FEATURE_ATTACHMENT:
                if (resultCode == RESULT_OK) {
//                this.mUri= data.getData();
                    if (this.mUri != null) {
//                    Uri selectedImage = this.mUri;
//                    getContentResolver().notifyChange(selectedImage, null);
                        Bitmap bitmap = getBitmap(mUri.getPath());
                        try {
                            if (bitmap != null) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                byte[] image = outputStream.toByteArray();
                                Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
                               mImage.setImageBitmap(rotatedBitmap);
//                            mMapViewHandler.addFeature(image);
                                mApplication.getDiemSuCo.setImage(image);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Hủy chụp ảnh", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(this, "Lỗi khi chụp ảnh", Toast.LENGTH_SHORT);
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
