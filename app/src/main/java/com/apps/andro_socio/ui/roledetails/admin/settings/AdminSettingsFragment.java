package com.apps.andro_socio.ui.roledetails.admin.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.NetworkUtil;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.admin.citydetails.CityFragment;

import java.util.ArrayList;
import java.util.List;

public class AdminSettingsFragment extends Fragment implements AdminSettingsMainAdapter.SettingsItemClickListener {

    private static final String TAG = AdminSettingsFragment.class.getSimpleName();
    private View rootView;

    private List<String> adminSettingsOptionList = new ArrayList<>();
    private RecyclerView recyclerAdminSettingOption;
    private AdminSettingsMainAdapter adminSettingsMainAdapter;
    private FragmentManager fragmentManager;
    private MainActivityInteractor mainActivityInteractor;

    public AdminSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_admin_settings, container, false);
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
        mainActivityInteractor.setScreenTitle(getString(R.string.admin_settings_title));
        adminSettingsOptionList = Utils.getAdminSettingsOption();
        fragmentManager = getParentFragmentManager();
        setUpViews();
    }

    private void setUpViews() {
        try {
            recyclerAdminSettingOption = rootView.findViewById(R.id.recycler_admin_setting_option);

            LinearLayoutManager linearLayoutManager = new GridLayoutManager(requireContext(), 1);
            recyclerAdminSettingOption.setLayoutManager(linearLayoutManager);
            adminSettingsMainAdapter = new AdminSettingsMainAdapter(requireContext(), adminSettingsOptionList, this);
            recyclerAdminSettingOption.setAdapter(adminSettingsMainAdapter);
            adminSettingsMainAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void settingsItemClicked(int position, String item) {
        try {
            switch (item) {
                case AppConstants
                        .ADMIN_SETTINGS_ADD_CITY:
                    if (checkInternet()) {
                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new CityFragment()).commit();
                    }
                    break;
                case AppConstants
                        .ADMIN_SETTINGS_PROFILE:
                    if (checkInternet()) {

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkInternet() {
        try {
            if (NetworkUtil.getConnectivityStatus(requireContext())) {
                return true;
            } else {
                AndroSocioToast.showErrorToast(requireContext(), getString(R.string.no_internet), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}