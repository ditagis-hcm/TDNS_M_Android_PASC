package vn.com.tdns.qlsc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import vn.com.tdns.qlsc.adapter.LayerAdapter;

public class LayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer);
        LayerAdapter layerAdapter = new LayerAdapter(this, new ArrayList<>());
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", true));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện hạ thế", true));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Dây thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));
        layerAdapter.add(new LayerAdapter.Item("Kiểm tra Lưới điện Trung thế", false));

        layerAdapter.notifyDataSetChanged();

        ListView listView = findViewById(R.id.list_layer);
        listView.setAdapter(layerAdapter);
        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            LayerAdapter.Item item = (LayerAdapter.Item) adapterView.getAdapter().getItem(i);
            if (item.isCheck()) {
                item.setCheck(false);
            } else {
                item.setCheck(true);
            }

        });
    }

}
