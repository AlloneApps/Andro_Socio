package com.apps.andro_socio.helper;

import com.apps.andro_socio.R;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class SliderUtils {
    private static final String TAG = SliderUtils.class.getSimpleName();
    public static final int SLIDER_TIME = 2000;

    // Admin Dashboard Slider Items
    public static List<SlideModel> getAdminDashboardSliderItemList() {
        List<SlideModel> adminSliderList = new ArrayList<>();
        adminSliderList.add(new SlideModel(R.drawable.ic_slider_1, ScaleTypes.FIT));
        adminSliderList.add(new SlideModel(R.drawable.ic_slider_2, ScaleTypes.FIT));
        adminSliderList.add(new SlideModel(R.drawable.ic_slider_3, ScaleTypes.FIT));
        return adminSliderList;
    }

    // User Dashboard Slider Items
    public static List<SlideModel> getUserDashboardSliderItemList() {
        List<SlideModel> userSliderList = new ArrayList<>();
        userSliderList.add(new SlideModel(R.drawable.ic_slider_1, ScaleTypes.FIT));
        userSliderList.add(new SlideModel(R.drawable.ic_slider_2, ScaleTypes.FIT));
        userSliderList.add(new SlideModel(R.drawable.ic_slider_3, ScaleTypes.FIT));
        return userSliderList;
    }

    // Police Dashboard Slider Items
    public static List<SlideModel> getPoliceDashboardSliderItemList() {
        List<SlideModel> policeSliderList = new ArrayList<>();
        policeSliderList.add(new SlideModel(R.drawable.ic_slider_1, ScaleTypes.FIT));
        policeSliderList.add(new SlideModel(R.drawable.ic_slider_2, ScaleTypes.FIT));
        policeSliderList.add(new SlideModel(R.drawable.ic_slider_3, ScaleTypes.FIT));
        return policeSliderList;
    }

    // MnOfficer Dashboard Slider Items
    public static List<SlideModel> getMnOfficerDashboardSliderItemList() {
        List<SlideModel> mnOfficerSliderList = new ArrayList<>();
        mnOfficerSliderList.add(new SlideModel(R.drawable.ic_slider_1, ScaleTypes.FIT));
        mnOfficerSliderList.add(new SlideModel(R.drawable.ic_slider_2, ScaleTypes.FIT));
        mnOfficerSliderList.add(new SlideModel(R.drawable.ic_slider_3, ScaleTypes.FIT));
        return mnOfficerSliderList;
    }


}
