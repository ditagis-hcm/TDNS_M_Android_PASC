package vn.com.tdns.qlsc.entities;


import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class DFeatureLayer {


    private FeatureLayer layer;

    private DLayerInfo layerInfoDTG;

    public DFeatureLayer(FeatureLayer layer, DLayerInfo layerInfoDTG) {
        this.layer = layer;
        this.layerInfoDTG = layerInfoDTG;
    }

    public FeatureLayer getLayer() {
        return layer;
    }

    public void setLayer(FeatureLayer layer) {
        this.layer = layer;
    }

    public DLayerInfo getLayerInfoDTG() {
        return layerInfoDTG;
    }

}
