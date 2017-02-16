package com.lizhe.devtools.accessibility.rom;

import java.util.ArrayList;
import java.util.List;

class FeatureInfo {

    private List<FeatureItem> featureItems = new ArrayList<>();

    void addFeatureItem(FeatureItem item) {
        featureItems.add(item);
    }

    List<FeatureItem> getFeatureItems() {
        return featureItems;
    }

    @Override
    public String toString() {
        return "{ FeatureInfo : mFeatureInfoItems = " + featureItems + " }";
    }
}