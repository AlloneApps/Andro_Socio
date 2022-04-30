package com.apps.andro_socio.ui.roledetails.police;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.SliderUtils;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class PoliceDashboardFragment extends Fragment {
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_police_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInteractor = (MainActivityInteractor) requireActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivityInteractor.setScreenTitle(getString(R.string.police_dashboard_title));

        setUpViews();
    }

    private void setUpViews() {
        try {
            ImageSlider imageSlider = rootView.findViewById(R.id.image_slider);
            List<SlideModel> slideModelList = SliderUtils.getPoliceDashboardSliderItemList();
            imageSlider.setImageList(slideModelList, ScaleTypes.FIT); // for all images
            imageSlider.startSliding(SliderUtils.SLIDER_TIME); // with new period
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}