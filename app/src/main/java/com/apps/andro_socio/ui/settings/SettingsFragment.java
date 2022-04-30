package com.apps.andro_socio.ui.settings;

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
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements SettingsMainAdapter.SettingsItemClickListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private View rootView;

    private List<String> adminSettingsOptionList = new ArrayList<>();
    private RecyclerView recyclerAdminSettingOption;
    private SettingsMainAdapter settingsMainAdapter;
    private FragmentManager fragmentManager;
    private MainActivityInteractor mainActivityInteractor;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
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
        adminSettingsOptionList = Utils.getAdminSettingsOption();
        fragmentManager = getParentFragmentManager();
        setUpViews();
    }

    private void setUpViews() {
        try {
            recyclerAdminSettingOption = rootView.findViewById(R.id.recycler_admin_setting_option);

            LinearLayoutManager linearLayoutManager = new GridLayoutManager(requireContext(), 1);
            recyclerAdminSettingOption.setLayoutManager(linearLayoutManager);
            settingsMainAdapter = new SettingsMainAdapter(requireContext(), adminSettingsOptionList, this);
            recyclerAdminSettingOption.setAdapter(settingsMainAdapter);

            if (settingsMainAdapter != null) {
                settingsMainAdapter.notifyDataSetChanged();
            }

            User loginUser = Utils.getLoginUserDetails(requireContext());
            if (loginUser != null) {
                if (loginUser.getMainRole() != null) {
                    switch (loginUser.getMainRole()) {
                        case AppConstants.ROLE_ADMIN: {
                            mainActivityInteractor.setScreenTitle(getString(R.string.admin_settings_title));
                            break;
                        }
                        case AppConstants.ROLE_USER: {
                            mainActivityInteractor.setScreenTitle(getString(R.string.user_settings_title));
                            break;
                        }
                        case AppConstants.ROLE_POLICE: {
                            mainActivityInteractor.setScreenTitle(getString(R.string.police_settings_title));
                            break;
                        }
                        case AppConstants.ROLE_MUNICIPAL_OFFICER: {
                            mainActivityInteractor.setScreenTitle(getString(R.string.municipal_officer_settings_title));
                            break;
                        }
                        default: {
                            mainActivityInteractor.setScreenTitle(getString(R.string.settings_title));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void settingsItemClicked(int position, String item) {
        try {
            switch (item) {
                case AppConstants
                        .SETTINGS_PROFILE:
                    if (checkInternet()) {
//                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new CityFragment()).commit();
                    }
                    break;
                case AppConstants
                        .SETTINGS_UPDATE_MPIN:
                    if (checkInternet()) {
//                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new CityFragment()).commit();
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