package com.apps.andro_socio.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.apps.andro_socio.BuildConfig;
import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.FireBaseDatabaseConstants;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.ui.registration.RegistrationActivity;
import com.apps.andro_socio.ui.roledetails.admin.AdminMainActivity;
import com.apps.andro_socio.ui.roledetails.mnofficer.MnOfficerMainActivity;
import com.apps.andro_socio.ui.roledetails.police.PoliceMainActivity;
import com.apps.andro_socio.ui.roledetails.user.usermain.UserMainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RESULT_CODE = 111;
    private ProgressDialog progressDialog;

    private Button btnLogin;
    private TextView textSignup, textVersion;
    private EditText editMobileNumber;
    private TextInputEditText editMPIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);

        setUpViews();
    }

    private void setUpViews() {
        btnLogin = findViewById(R.id.btn_login);
        textSignup = findViewById(R.id.text_signup);
        textVersion = findViewById(R.id.text_version);

        editMobileNumber = findViewById(R.id.edit_mobile_number);
        editMPIn = findViewById(R.id.edit_mPin);

        String version = "v." + BuildConfig.VERSION_NAME;
        textVersion.setText(version);

        textSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), RESULT_CODE);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(LoginActivity.this);
                if (validateFields())
                    checkLogin();
            }
        });

    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(editMobileNumber.getText().toString())) {
            AndroSocioToast.showErrorToast(LoginActivity.this, getString(R.string.enter_phone_number), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            return false;
        } else if (editMobileNumber.getText().toString().length() != 10) {
            AndroSocioToast.showErrorToast(LoginActivity.this, getString(R.string.phone_number_digits), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            return false;
        } else if (TextUtils.isEmpty(editMPIn.getText().toString())) {
            AndroSocioToast.showErrorToast(LoginActivity.this, getString(R.string.mpin_enter), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            return false;
        } else if (editMPIn.getText().toString().length() != 4) {
            AndroSocioToast.showErrorToast(LoginActivity.this, getString(R.string.mpin_digit_validation), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void checkLogin() {
        try {
            String userMobileNumber = editMobileNumber.getText().toString().trim();
            String userMPin = editMPIn.getText().toString().trim();

            showProgressDialog("Verifying please wait.");

            verifyUserLogin(LoginActivity.this, userMobileNumber, userMPin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyUserLogin(Context context, String userMobileNumber, String mPin) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.USERS_TABLE);

            databaseReference.child(userMobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String mobileNumber = snapshot.child(FireBaseDatabaseConstants.USER_MOBILE_NUMBER).getValue(String.class);
                        String mobilePin = snapshot.child(FireBaseDatabaseConstants.USER_M_PIN).getValue(String.class);
                        Log.d(TAG, "onDataChange: mobileNumber: " + mobileNumber);
                        Log.d(TAG, "onDataChange: mobileNumber: " + mobilePin);
                        if (mobileNumber != null && mobilePin != null) {
                            if (userMobileNumber.equals(mobileNumber) && mobilePin.equals(mPin)) {
                                String userMPin = snapshot.child(FireBaseDatabaseConstants.USER_M_PIN).getValue(String.class);
                                String userMainRole = snapshot.child(FireBaseDatabaseConstants.USER_MAIN_ROLE).getValue(String.class);
                                String userCity = snapshot.child(FireBaseDatabaseConstants.USER_CITY).getValue(String.class);
                                String userType = snapshot.child(FireBaseDatabaseConstants.USER_TYPE).getValue(String.class);
                                String userFullName = snapshot.child(FireBaseDatabaseConstants.USER_FULL_NAME).getValue(String.class);
                                String userGender = snapshot.child(FireBaseDatabaseConstants.USER_GENDER).getValue(String.class);
                                String userIsActive = snapshot.child(FireBaseDatabaseConstants.USER_IS_ACTIVE).getValue(String.class);

                                Log.d(TAG, "onDataChange: role: " + userMainRole);
                                Log.d(TAG, "onDataChange: userIsActive: " + userIsActive);
                                if (userIsActive.equalsIgnoreCase(AppConstants.ACTIVE_USER)) {
                                    User user = new User();
                                    user.setMobileNumber(mobileNumber);
                                    user.setmPin(userMPin);
                                    user.setMainRole(userMainRole);
                                    user.setUserCity(userCity);
                                    user.setUserType(userType);
                                    user.setFullName(userFullName);
                                    user.setGender(userGender);
                                    user.setIsActive(userIsActive);
                                    Utils.saveSharedPrefsString(context, AppConstants.LOGIN_TOKEN, user.getMobileNumber());
                                    Utils.saveSharedPrefsString(context, AppConstants.USER_ROLE, user.getMainRole());
                                    Utils.saveLoginUserDetails(context, user);
                                    hideProgressDialog();

                                    AndroSocioToast.showSuccessToastWithBottom(context, "Login success", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                                    navigateToDashboard(user.getMainRole());
                                } else {
                                    hideProgressDialog();
                                    AndroSocioToast.showErrorToastWithBottom(context, mobileNumber + " is not activated or deActivated, Please contact admin.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_LONG);
                                }
                            } else {
                                hideProgressDialog();
                                AndroSocioToast.showErrorToastWithBottom(context, "Credential mismatch.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                            }
                        } else {
                            hideProgressDialog();
                            AndroSocioToast.showErrorToastWithBottom(context, "Mobile number and mPin null", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    } else {
                        hideProgressDialog();
                        AndroSocioToast.showErrorToastWithBottom(context, "Failed to login, verify credentials", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    hideProgressDialog();
                    AndroSocioToast.showErrorToastWithBottom(context, error.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            e.printStackTrace();
            AndroSocioToast.showErrorToastWithBottom(context, e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
        }
    }

    private void navigateToDashboard(String userMainRole) {
        try {
            if (userMainRole != null) {
                switch (userMainRole) {
                    case AppConstants.ROLE_ADMIN: {
                        startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                        finish();
                        break;
                    }
                    case AppConstants.ROLE_POLICE: {
                        startActivity(new Intent(LoginActivity.this, PoliceMainActivity.class));
                        finish();
                        break;
                    }
                    case AppConstants.ROLE_MUNICIPAL_OFFICER: {
                        startActivity(new Intent(LoginActivity.this, MnOfficerMainActivity.class));
                        finish();
                        break;
                    }
                    default: {
                        startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                        finish();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog(String message) {
        try {
            if (progressDialog != null) {
                progressDialog.setMessage(message);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (RESULT_CODE): {
                if (resultCode == Activity.RESULT_OK) {
//                    String returnValue = data.getStringExtra("some_key");
                }
                break;
            }
        }
    }
}