package com.apps.andro_socio.ui.roledetails.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.NetworkUtil;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.ui.login.LoginActivity;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class UserMainActivity extends AppCompatActivity implements MainActivityInteractor {

    private static final String TAG = UserMainActivity.class.getSimpleName();
    private FragmentManager fragmentManager;
    private BottomNavigation bottomNavigationUser;
    private ImageView logout;
    private TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        textTitle = findViewById(R.id.title);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new UserDashboardFragment()).commit();

        setUpViews();
    }

    private void setUpViews() {

        bottomNavigationUser = findViewById(R.id.bottom_navigation_user);
        logout = findViewById(R.id.logout);

        bottomNavigationUser.setMenuItemSelectionListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int id, int position, boolean b) {
                showUserBottomNavigation(position);
            }

            @Override
            public void onMenuItemReselect(int id, int position, boolean b) {
                showUserBottomNavigation(position);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogForLogout();
            }
        });
    }

    private void showUserBottomNavigation(int position) {

        switch (position) {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new UserDashboardFragment()).commit();
                break;
            case 1:
                if (checkInternet()) {
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new CreateIssueOrComplaint()).commit();
                }
                break;
            case 2:
                if (checkInternet()) {
                    AndroSocioToast.showInfoToast(UserMainActivity.this, "Implementation Pending", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new UserDashboardFragment()).commit();
                }
                break;
            case 3:
                AndroSocioToast.showInfoToast(UserMainActivity.this, "Implementation Pending", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new UserDashboardFragment()).commit();

                break;
        }
    }

    private boolean checkInternet() {
        try {
            if (NetworkUtil.getConnectivityStatus(UserMainActivity.this)) {
                return true;
            } else {
                AndroSocioToast.showErrorToast(UserMainActivity.this, getString(R.string.no_internet), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public void highlightBottomNavigationTabPosition(int position) {
        if (bottomNavigationUser != null) {
            bottomNavigationUser.setSelectedIndex(position, true);
        }
    }

    @Override
    public void setScreenTitle(String title) {
        if (textTitle != null) {
            textTitle.setText(title);
        }
    }

    public void showAlertDialogForLogout() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserMainActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_with_two_buttons, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            // TextView and EditText Initialization
            TextView textAlertHeader = dialogView.findViewById(R.id.dialog_message_header);
            TextView textAlertDesc = dialogView.findViewById(R.id.dialog_message_desc);

            TextView textBtnNo = dialogView.findViewById(R.id.text_button_left);
            TextView textBtnYes = dialogView.findViewById(R.id.text_button_right);

            textAlertHeader.setText("Alert..!");
            String logoutSureMessage = "Are you sure want to logout from app?";

            textAlertDesc.setText(logoutSureMessage);
            textBtnNo.setText("No");
            textBtnYes.setText("Yes");

            AlertDialog alert = builder.create();
            alert.show();

            textBtnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        alert.dismiss();
                        logout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            textBtnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        alert.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            Utils.removeAllDataWhenLogout(UserMainActivity.this);
            startActivity(new Intent(UserMainActivity.this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
            if (fragment instanceof UserDashboardFragment) {
                moveTaskToBack(true);
            } else if (fragment instanceof CreateIssueOrComplaint) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, new UserDashboardFragment()).commit();
                highlightBottomNavigationTabPosition(0);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d(TAG, "onActivityResult: main requestCode: "+requestCode);
            Log.d(TAG, "onActivityResult: main resultCode: "+resultCode);
            Log.d(TAG, "onActivityResult: main Activity.RESULT_OK: "+Activity.RESULT_OK);
            Log.d(TAG, "onActivityResult: main CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: "+CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            Log.d(TAG, "onActivityResult: main CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: "+CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                        fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main).onActivityResult(requestCode, resultCode, data);
                        break;
                    case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE:
                        fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main).onActivityResult(requestCode, resultCode, data);
                        break;
                    default:
                        fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main).onActivityResult(requestCode, resultCode, data);
                        break;
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main)).onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}