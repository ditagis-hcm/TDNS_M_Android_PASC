package vn.com.tdns.qlsc.fragment.bookmark;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import butterknife.BindView;
import vn.com.tdns.qlsc.R;
import vn.com.tdns.qlsc.adapter.BasemapAdapter;

@SuppressLint("ValidFragment")
public class NewBookmark extends Fragment {
    @BindView(R.id.grid_fragment_on_network)
    GridView mGridview;
    private View mRootView;

    @SuppressLint("ValidFragment")
    public NewBookmark(final LayoutInflater inflater, ViewPager viewPager) {
        mRootView = inflater.inflate(R.layout.fragment_basemap_on_network, null);
        mGridview = mRootView.findViewById(R.id.grid_fragment_on_network);

        BasemapAdapter basemapAdapter = new BasemapAdapter(mRootView.getContext(), new ArrayList<>());
        basemapAdapter.add(new BasemapAdapter.Item("Openstreetmap", "Hiển thị"));
        basemapAdapter.add(new BasemapAdapter.Item("Openstreetmap1", ""));
        basemapAdapter.add(new BasemapAdapter.Item("Openstreetmap2", ""));
        basemapAdapter.add(new BasemapAdapter.Item("Openstreetmap3", ""));
        basemapAdapter.add(new BasemapAdapter.Item("Openstreetmap4", ""));

        basemapAdapter.notifyDataSetChanged();
        mGridview.setAdapter(basemapAdapter);
        viewPager.setCurrentItem(0,true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView;
    }

    public void refresh() {

    }
}
