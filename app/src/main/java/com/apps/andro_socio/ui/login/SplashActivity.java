package com.apps.andro_socio.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.ui.roledetails.admin.AdminMainActivity;
import com.apps.andro_socio.ui.roledetails.mnofficer.MnOfficerMainActivity;
import com.apps.andro_socio.ui.roledetails.police.PoliceMainActivity;
import com.apps.andro_socio.ui.roledetails.user.UserMainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setUpView();
    }

    private void setUpView() {
        try {
            String loginToken = Utils.getSharedPrefsString(SplashActivity.this, AppConstants.LOGIN_TOKEN);
            String userRole = Utils.getSharedPrefsString(SplashActivity.this, AppConstants.USER_ROLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (loginToken.isEmpty()) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        if (userRole != null) {
                            switch (userRole) {
                                case AppConstants.ROLE_ADMIN: {
                                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                                    finish();
                                    break;
                                }
                                case AppConstants.ROLE_POLICE: {
                                    startActivity(new Intent(SplashActivity.this, PoliceMainActivity.class));
                                    finish();
                                    break;
                                }
                                case AppConstants.ROLE_MUNICIPAL_OFFICER: {
                                    startActivity(new Intent(SplashActivity.this, MnOfficerMainActivity.class));
                                    finish();
                                    break;
                                }
                                default: {
                                    startActivity(new Intent(SplashActivity.this, UserMainActivity.class));
                                    finish();
                                    break;
                                }
                            }
                        }
                    }
                }
            }, SPLASH_TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}