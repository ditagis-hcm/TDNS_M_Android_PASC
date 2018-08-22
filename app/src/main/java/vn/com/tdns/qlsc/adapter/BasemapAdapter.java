package vn.com.tdns.qlsc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class BasemapAdapter extends ArrayAdapter<BasemapAdapter.Item> {
    private Context mContext;
    private List<Item> items;

    public BasemapAdapter(Context context, List<Item> items) {
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
            convertView = inflater.inflate(R.layout.item_gridview_fragment, null);
        }
        Item item = items.get(position);
        if (item.getImage() != null)
            ((ImageView) convertView.findViewById(R.id.img_item_frag)).setImageDrawable(item.getImage());
        ((TextView) convertView.findViewById(R.id.txt_title_item_frag)).setText(item.getTitle());
        ((TextView) convertView.findViewById(R.id.txt_subtitle_item_frag)).setText(item.getSubTitle());

        return convertView;
    }


    public static class Item {
        private Drawable image;
        private String title;
        private String subTitle;

        public Item() {
        }

        public Item(String title, String subTitle) {
            this.title = title;
            this.subTitle = subTitle;
        }

        public Drawable getImage() {
            return image;
        }

        public void setImage(Drawable image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }
    }
}
