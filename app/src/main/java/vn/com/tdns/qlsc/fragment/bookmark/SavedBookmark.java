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

import butterknife.BindView;
import vn.com.tdns.qlsc.R;

@SuppressLint("ValidFragment")
public class SavedBookmark extends Fragment {
    @BindView(R.id.grid_fragment_on_network)
    GridView mGridview;
    private View mRootView;

    @SuppressLint("ValidFragment")
    public SavedBookmark(final LayoutInflater inflater, ViewPager viewPager) {
        mRootView = inflater.inflate(R.layout.fragment_basemap_on_network, null);
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
