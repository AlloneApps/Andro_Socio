package com.apps.andro_socio.ui.roledetails.police.viewcitywisecomplaints;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.FireBaseDatabaseConstants;
import com.apps.andro_socio.helper.NetworkUtil;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.model.citydetails.City;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.ViewComplaintOrIssueActivity;
import com.apps.andro_socio.ui.roledetails.user.createissueorcomplaint.CreateIssueOrComplaint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewCityWiseUserComplaintsByPolice extends Fragment implements UserCityWiseComplaintByPoliceMainAdapter.UserComplaintItemClickListener {

    private static final String TAG = CreateIssueOrComplaint.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;
    private TextView textNoComplaintsAvailable, textCityComplaintHeader, textCity;

    private List<City> cityList = new ArrayList<>();
    private List<String> cityStringList = new ArrayList<>();

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceComplaint;

    private RecyclerView recyclerViewUserComplaintByPolice;
    private UserCityWiseComplaintByPoliceMainAdapter userCityWiseComplaintByPoliceMainAdapter;

    private List<ComplaintMaster> complaintMasterList = new ArrayList<>();

    private User loginUser;

    public ViewCityWiseUserComplaintsByPolice() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_user_city_wise_complaints_by_police, container, false);
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
        try {
            mainActivityInteractor.setScreenTitle(getString(R.string.city_wise_complaints_header));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceComplaint = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.COMPLAINT_LIST_TABLE);

            loginUser = Utils.getLoginUserDetails(requireContext());

            textCity = rootView.findViewById(R.id.text_city);

            getCityList();

            if (loginUser != null) {
                textCity.setText(loginUser.getUserCity());
                getUserComplaintList(loginUser.getUserCity(), true);
            } else {
                setUpViews();
                AndroSocioToast.showErrorToast(requireContext(), "Failed to fetch details", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            recyclerViewUserComplaintByPolice = rootView.findViewById(R.id.recycler_user_issues_by_police);
            textNoComplaintsAvailable = rootView.findViewById(R.id.no_complaints_available);
            textCityComplaintHeader = rootView.findViewById(R.id.city_complaint_header);

            if (loginUser != null) {
                String cityIssueText = loginUser.getUserCity() + " City Complaints";
                textCityComplaintHeader.setText(cityIssueText);
            }

            if (complaintMasterList.size() > 0) {
                textNoComplaintsAvailable.setVisibility(View.GONE);
                textCityComplaintHeader.setVisibility(View.VISIBLE);
                recyclerViewUserComplaintByPolice.setVisibility(View.VISIBLE);
            } else {
                recyclerViewUserComplaintByPolice.setVisibility(View.GONE);
                textCityComplaintHeader.setVisibility(View.GONE);
                textNoComplaintsAvailable.setVisibility(View.VISIBLE);
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            recyclerViewUserComplaintByPolice.setLayoutManager(linearLayoutManager);
            userCityWiseComplaintByPoliceMainAdapter = new UserCityWiseComplaintByPoliceMainAdapter(requireContext(), complaintMasterList, this);
            recyclerViewUserComplaintByPolice.setAdapter(userCityWiseComplaintByPoliceMainAdapter);

            if (userCityWiseComplaintByPoliceMainAdapter != null) {
                userCityWiseComplaintByPoliceMainAdapter.notifyDataSetChanged();
            }

            RxView.touches(textCity).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                        builderSingle.setTitle(requireContext().getString(R.string.select_city));
                        final ArrayAdapter<String> citySelectionAdapter = new ArrayAdapter<String>(requireContext(),
                                android.R.layout.select_dialog_singlechoice, cityStringList) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        builderSingle.setNegativeButton("Cancel", (dialog, position) -> dialog.dismiss());

                        builderSingle.setAdapter(citySelectionAdapter, (dialog, position) -> {
                            textCity.setText(citySelectionAdapter.getItem(position));
                            getUserComplaintList(citySelectionAdapter.getItem(position), false);
                        });
                        builderSingle.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserComplaintList(String cityName, boolean isSetupLoad) {
        try {
            showProgressDialog("Fetching details, please wait");
            mUserReferenceComplaint.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: snapshot: " + snapshot);

                        DataSnapshot dataSnapshot = snapshot.child(cityName).child(AppConstants.COMPLAINT_TYPE);
                        Log.d(TAG, "onDataChange: dataSnapshot: " + dataSnapshot);
                        complaintMasterList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot childSnapshot : postSnapshot.getChildren()) {
                                ComplaintMaster complaintMaster = childSnapshot.getValue(ComplaintMaster.class);
                                if (complaintMaster != null) {
                                    complaintMasterList.add(complaintMaster);
                                }
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: complaintMasterList:" + complaintMasterList);
                    loadComplaintsByCity(cityName, isSetupLoad);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    hideProgressDialog();
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                    loadComplaintsByCity(cityName, isSetupLoad);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadComplaintsByCity(String cityName, boolean isSetupLoad) {
        try {
            if (isSetupLoad) {
                setUpViews();
            } else {
                if (complaintMasterList.size() > 0) {
                    textNoComplaintsAvailable.setVisibility(View.GONE);
                    recyclerViewUserComplaintByPolice.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewUserComplaintByPolice.setVisibility(View.GONE);
                    textNoComplaintsAvailable.setVisibility(View.VISIBLE);
                }

                if (cityName != null) {
                    textCityComplaintHeader.setVisibility(View.VISIBLE);
                    String cityIssueText = cityName + " City Complaints";
                    textCityComplaintHeader.setText(cityIssueText);
                }

                if (userCityWiseComplaintByPoliceMainAdapter != null) {
                    userCityWiseComplaintByPoliceMainAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userComplaintViewClicked(int position, ComplaintMaster complaintMaster, ImageView imageView, TextView textView) {
        try {
            if (NetworkUtil.getConnectivityStatus(requireContext())) {
                Intent intentView = new Intent(requireContext(), ViewComplaintOrIssueActivity.class);
                intentView.putExtra(AppConstants.VIEW_MUNICIPAL_ISSUE_DATA, new MnIssueMaster());
                intentView.putExtra(AppConstants.VIEW_COMPLAINT_DATA, complaintMaster);
                intentView.putExtra(AppConstants.VIEW_COMPLAINT_OR_ISSUE_FLAG, true);

                Pair<View, String> transactionPairOne = Pair.create((View) imageView, requireContext().getResources().getString(R.string.transaction_complaint_or_issue_photo));
                Pair<View, String> transactionPairTwo = Pair.create((View) textView, requireContext().getResources().getString(R.string.transaction_complaint_or_issue_header));

           /*
           // Call single Shared Transaction
           ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(requireActivity(), (View) imagePlace, requireContext().getResources().getString(R.string.transaction_name));
            */

                // Call Multiple Shared Transaction using Pair Option
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(requireActivity(), transactionPairOne, transactionPairTwo);
                startActivityForResult(intentView, 3, options.toBundle());

            } else {
                AndroSocioToast.showErrorToast(requireContext(), getString(R.string.no_internet), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
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

    public void getCityList() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.CITY_TABLE);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        cityList.clear();
                        cityStringList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            City city = postSnapshot.getValue(City.class);
                            if (city != null) {
                                cityList.add(city);
                                cityStringList.add(city.getCityName());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}