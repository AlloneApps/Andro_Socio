package com.apps.andro_socio.ui.roledetails.mnofficer.viewuserissuesbycity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewCityWiseUserIssuesByOfficer extends Fragment implements UserCityWiseIssueByOfficerMainAdapter.UserIssueItemClickListener {
    private static final String TAG = ViewCityWiseUserIssuesByOfficer.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;
    private TextView textCityIssuesHeader, textNoIssuesAvailable, textCity;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceIssue;

    private RecyclerView recyclerViewUserIssueByOfficer;
    private UserCityWiseIssueByOfficerMainAdapter userIssueByOfficerMainAdapter;

    private List<City> cityList = new ArrayList<>();
    private List<String> cityStringList = new ArrayList<>();

    private List<MnIssueMaster> mnIssueMasterList = new ArrayList<>();
    private User loginUser;

    public ViewCityWiseUserIssuesByOfficer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_user_city_wise_issues_by_officer, container, false);
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
            mainActivityInteractor.setScreenTitle(getString(R.string.mn_officer_btn_third_option));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceIssue = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.MN_ISSUE_LIST_TABLE);

            loginUser = Utils.getLoginUserDetails(requireContext());

            textCity = rootView.findViewById(R.id.text_city);

            getCityList();

            if (loginUser != null) {
                textCity.setText(loginUser.getUserCity());
                getUserIssueList(loginUser.getUserCity(), true);
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
            recyclerViewUserIssueByOfficer = rootView.findViewById(R.id.recycler_user_issues_by_officer);
            textNoIssuesAvailable = rootView.findViewById(R.id.no_issues_available);
            textCityIssuesHeader = rootView.findViewById(R.id.city_issues_header);

            if (mnIssueMasterList.size() > 0) {
                textNoIssuesAvailable.setVisibility(View.GONE);
                textCityIssuesHeader.setVisibility(View.VISIBLE);
                recyclerViewUserIssueByOfficer.setVisibility(View.VISIBLE);
            } else {
                textCityIssuesHeader.setVisibility(View.VISIBLE);
                recyclerViewUserIssueByOfficer.setVisibility(View.GONE);
                textNoIssuesAvailable.setVisibility(View.VISIBLE);
            }

            if (loginUser != null) {
                String cityIssueText = loginUser.getUserCity() + " City Municipal Issues";
                textCityIssuesHeader.setText(cityIssueText);
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            recyclerViewUserIssueByOfficer.setLayoutManager(linearLayoutManager);
            userIssueByOfficerMainAdapter = new UserCityWiseIssueByOfficerMainAdapter(requireContext(), mnIssueMasterList, this);
            recyclerViewUserIssueByOfficer.setAdapter(userIssueByOfficerMainAdapter);

            if (userIssueByOfficerMainAdapter != null) {
                userIssueByOfficerMainAdapter.notifyDataSetChanged();
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

                            getUserIssueList(citySelectionAdapter.getItem(position), false);
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

    public void getUserIssueList(String cityName, boolean isSetupLoad) {
        try {
            showProgressDialog("Fetching details, please wait");
            mUserReferenceIssue.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: snapshot: " + snapshot);
                        DataSnapshot dataSnapshot = snapshot.child(cityName).child(AppConstants.MUNICIPAL_ISSUE_TYPE);
                        Log.d(TAG, "onDataChange: dataSnapshot: " + dataSnapshot);
                        mnIssueMasterList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: child: " + postSnapshot);
                            for (DataSnapshot childData : postSnapshot.getChildren()) {
                                MnIssueMaster mnIssueMaster = childData.getValue(MnIssueMaster.class);
                                if (mnIssueMaster != null) {
                                    mnIssueMasterList.add(mnIssueMaster);
                                }
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: mnIssueMasterList:" + mnIssueMasterList);

                    loadCityWiseDetails(cityName, isSetupLoad);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    hideProgressDialog();
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                    loadCityWiseDetails(cityName, isSetupLoad);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCityWiseDetails(String cityName, boolean isSetupLoad) {
        try {
            if (isSetupLoad) {
                setUpViews();
            } else {
                if (mnIssueMasterList.size() > 0) {
                    textNoIssuesAvailable.setVisibility(View.GONE);
                    recyclerViewUserIssueByOfficer.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewUserIssueByOfficer.setVisibility(View.GONE);
                    textNoIssuesAvailable.setVisibility(View.VISIBLE);
                }

                if (cityName != null) {
                    textCityIssuesHeader.setVisibility(View.VISIBLE);
                    String cityIssueText = cityName + " City Municipal Issues";
                    textCityIssuesHeader.setText(cityIssueText);
                }

                if (userIssueByOfficerMainAdapter != null) {
                    userIssueByOfficerMainAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userIssueViewClicked(int position, MnIssueMaster mnIssueMaster, ImageView imageView, TextView textView) {
        try {
            if (NetworkUtil.getConnectivityStatus(requireContext())) {
                Intent intentView = new Intent(requireContext(), ViewComplaintOrIssueActivity.class);
                intentView.putExtra(AppConstants.VIEW_MUNICIPAL_ISSUE_DATA, mnIssueMaster);
                intentView.putExtra(AppConstants.VIEW_COMPLAINT_DATA, new ComplaintMaster());
                intentView.putExtra(AppConstants.VIEW_COMPLAINT_OR_ISSUE_FLAG, false);

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