package vn.com.tdns.qlsc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import vn.com.tdns.qlsc.R;


/**
 * Created by ThanLe on 04/10/2017.
 */

public class LayerAdapter extends ArrayAdapter<LayerAdapter.Item> {
    private Context mContext;
    private List<Item> items;

    public LayerAdapter(Context context, List<Item> items) {
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
            convertView = inflater.inflate(R.layout.item_list_layer, null);
        }
        Item item = items.get(position);
        ((TextView) convertView.findViewById(R.id.txt_title_item_feature)).setText(item.getTitle());
        ((CheckBox)convertView.findViewById(R.id.chk_feature)).setChecked(item.isCheck());
        return convertView;
    }


    public static class Item {
        private String title;
        private boolean isCheck;
        public Item() {
        }

        public Item(String title, boolean isCheck) {
            this.title = title;
            this.isCheck = isCheck;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }
}
