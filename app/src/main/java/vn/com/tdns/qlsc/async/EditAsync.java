package vn.com.tdns.qlsc.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.com.tdns.qlsc.R;
import vn.com.tdns.qlsc.adapter.FeatureViewMoreInfoAdapter;
import vn.com.tdns.qlsc.common.Constant;


/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditAsync extends AsyncTask<FeatureViewMoreInfoAdapter, Void, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature;
    private boolean isUpdateAttachment;
    private byte[] mImage;

    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(ArcGISFeature feature);
    }

    public EditAsync(Context context, ServiceFeatureTable serviceFeatureTable,
                     ArcGISFeature selectedArcGISFeature, boolean isUpdateAttachment, byte[] image,
                     AsyncResponse delegate) {
        mContext = context;
        this.mDelegate = delegate;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
        mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        this.isUpdateAttachment = isUpdateAttachment;
        this.mImage = image;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        mDialog.setCancelable(false);

        mDialog.show();

    }

    @Override
    protected Void doInBackground(FeatureViewMoreInfoAdapter... params) {
        final FeatureViewMoreInfoAdapter adapter = params[0];
        final Calendar[] c = {Calendar.getInstance()};

        boolean hasDomain = false;
        List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
        for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
            if (item.getValue() == null || !item.isEdit() || !item.isEdited()) continue;
            Domain domain = mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain();
            Object codeDomain = null;
            if (domain != null) {
                hasDomain = true;
                List<CodedValue> codedValues = ((CodedValueDomain) EditAsync.this.mSelectedArcGISFeature
                        .getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                codeDomain = getCodeDomain(codedValues, item.getValue());

            } else switch (item.getFieldType()) {
                case DATE:
                    Date date;
                    try {

                        date = Constant.DATE_FORMAT_VIEW.parse(item.getValue());
                        c[0].setTime(date);
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), c[0]);
                    } catch (ParseException e) {
                        try {
                            date = Constant.DATE_FORMAT.parse(item.getValue());
                            c[0].setTime(date);
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), c[0]);
                        } catch (ParseException ignored) {

                        }

                    }
                    break;

                case TEXT:
                    if (hasDomain)
                        if (codeDomain != null)
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), codeDomain.toString());
                        else mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), null);
                    else
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), item.getValue());
                    break;
                case SHORT:
                    if (codeDomain != null) {
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(codeDomain.toString()));
                    } else
                        try {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                        } catch (NumberFormatException e) {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), null);
                        }
                    break;
                case DOUBLE:
                    if (codeDomain != null) {
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Double.parseDouble(codeDomain.toString()));
                    } else
                        try {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Double.parseDouble(item.getValue()));
                        } catch (NumberFormatException e) {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), null);
                        }
                    break;
                case INTEGER:
                    if (codeDomain != null) {
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Integer.parseInt(codeDomain.toString()));
                    } else
                        try {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Integer.parseInt(item.getValue()));
                        } catch (NumberFormatException e) {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), null);
                        }
                    break;
            }
        }
//        if (finalTrangThai.equals(mContext.getString(R.string.SuCo_TrangThai_HoanThanh)))
//
//        {
//            c[0] = Calendar.getInstance();
//            mSelectedArcGISFeature.getAttributes().put(mContext.getString(R.string.Field_SuCo_NgayKhacPhuc), c[0]);
//        }
//        mSelectedArcGISFeature.getAttributes().put(mContext.getString(R.string.Field_SuCo_NhanVienGiamSat),
//                UserDangNhap.getInstance().getUser().getUserName());

        mServiceFeatureTable.loadAsync();

        mServiceFeatureTable.addDoneLoadingListener(() -> {
            // update feature in the feature table
            mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature).addDoneListener(()
                    -> mServiceFeatureTable.applyEditsAsync().addDoneListener(() -> {
                if (isUpdateAttachment && mImage != null) {
                    if (mSelectedArcGISFeature.canEditAttachments())
                        addAttachment();
                    else
                        applyEdit();
                } else {
                    applyEdit();

                }
            }));
        });
        return null;
    }


    private void addAttachment() {

        final String attachmentName = mContext.getString(R.string.attachment) + "_" + System.currentTimeMillis() + ".png";
        final ListenableFuture<Attachment> addResult = mSelectedArcGISFeature.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName);
        addResult.addDoneListener(() -> {
            try {
                Attachment attachment = addResult.get();
                if (attachment.getSize() > 0) {
                    final ListenableFuture<Void> tableResult = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
                    tableResult.addDoneListener(this::applyEdit);
                }
            } catch (Exception ignored) {
                publishProgress();
            }
        });
    }


    private void applyEdit() {

        final ListenableFuture<List<FeatureEditResult>> updatedServerResult = mServiceFeatureTable.applyEditsAsync();
        updatedServerResult.addDoneListener(() -> {
            List<FeatureEditResult> edits;
            try {
                edits = updatedServerResult.get();
                if (edits.size() > 0) {
                    edits.get(0).hasCompletedWithErrors();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                publishProgress();
            }
        });

    }

    private Object getIdFeatureTypes(List<FeatureType> featureTypes, String value) {
        Object code = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getName().equals(value)) {
                code = featureType.getId();
                break;
            }
        }
        return code;
    }

    private Object getCodeDomain(List<CodedValue> codedValues, String value) {
        Object code = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getName().equals(value)) {
                code = codedValue.getCode();
                break;
            }
        }
        return code;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            this.mDelegate.processFinish(mSelectedArcGISFeature);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

