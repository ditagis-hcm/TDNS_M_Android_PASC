package vn.com.tdns.qlsc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.com.tdns.qlsc.R;


/**
 * Created by ThanLe on 04/10/2017.
 */

public class AnnotationAdapter extends ArrayAdapter<AnnotationAdapter.Item> {
    private Context mContext;
    private List<Item> items;

    public AnnotationAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.mContext = context;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_annotation, null);
        }
        Item item = items.get(position);
        ((TextView) convertView.findViewById(R.id.txt_title_item_annotation)).setText(item.getTitle());
        ((ImageView) convertView.findViewById(R.id.img_item_annotation)).setImageBitmap(item.getBitmap());
        return convertView;
    }


    public static class Item {
        private Bitmap bitmap;
        private String title;

        public Item() {
        }

        public Item(Bitmap bitmap, String title) {
            this.bitmap = bitmap;
            this.title = title;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public String getTitle() {
            return title;
        }
    }
}
