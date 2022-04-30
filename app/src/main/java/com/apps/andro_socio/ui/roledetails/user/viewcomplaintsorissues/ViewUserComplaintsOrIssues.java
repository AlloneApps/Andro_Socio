package com.apps.andro_socio.ui.roledetails.user.viewcomplaintsorissues;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.ViewComplaintOrIssueActivity;
import com.apps.andro_socio.ui.roledetails.user.createissueorcomplaint.CreateIssueOrComplaint;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewUserComplaintsOrIssues extends Fragment implements UserComplaintMainAdapter.UserComplaintItemClickListener, UserIssueMainAdapter.UserIssueItemClickListener {

    private static final String TAG = CreateIssueOrComplaint.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;
    private TextView textNoComplaintsOrIssuesAvailable;

    private TabLayout complaintOrIssueTabLayout;
    BadgeDrawable badgeIssues, badgeComplaints;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceComplaint;
    private DatabaseReference mUserReferenceIssue;

    private RecyclerView recyclerViewUserComplaintOrIssues;
    private UserComplaintMainAdapter userComplaintMainAdapter;
    private UserIssueMainAdapter userIssueMainAdapter;

    private List<ComplaintMaster> complaintMasterList = new ArrayList<>();
    private List<MnIssueMaster> mnIssueMasterList = new ArrayList<>();

    private User loginUser;

    public ViewUserComplaintsOrIssues() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_user_complaints, container, false);
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
            mainActivityInteractor.setScreenTitle(getString(R.string.my_issue_or_complaints_ttl));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceComplaint = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.COMPLAINT_LIST_TABLE);
            mUserReferenceIssue = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.MN_ISSUE_LIST_TABLE);

            complaintOrIssueTabLayout = rootView.findViewById(R.id.user_issues_or_complaints_tab);

            try {
                //set the icons
                complaintOrIssueTabLayout.addTab(complaintOrIssueTabLayout.newTab().setText(AppConstants.MUNICIPAL_ISSUES_BADGE), 0);
                complaintOrIssueTabLayout.addTab(complaintOrIssueTabLayout.newTab().setText(AppConstants.COMPLAINTS_BADGE), 1);

                //set the badge
                badgeIssues = complaintOrIssueTabLayout.getTabAt(0).getOrCreateBadge();
                badgeComplaints = complaintOrIssueTabLayout.getTabAt(1).getOrCreateBadge();

                badgeIssues.setBadgeTextColor(getResources().getColor(R.color.colorWhite, null));
                badgeComplaints.setBadgeTextColor(getResources().getColor(R.color.colorWhite, null));

                badgeIssues.setVisible(true);
                badgeComplaints.setVisible(true);

                badgeIssues.setNumber(0);
                badgeComplaints.setNumber(0);

            } catch (Exception e) {
                e.printStackTrace();
            }

            loginUser = Utils.getLoginUserDetails(requireContext());
            if (loginUser != null) {
                getUserIssueList(loginUser, true);
                getUserComplaintList(loginUser, false);
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
            textNoComplaintsOrIssuesAvailable = rootView.findViewById(R.id.no_complaints_or_issues_available);
            recyclerViewUserComplaintOrIssues = rootView.findViewById(R.id.recycler_user_complaints_or_issues);

            if (mnIssueMasterList.size() > 0) {
                textNoComplaintsOrIssuesAvailable.setVisibility(View.GONE);
                recyclerViewUserComplaintOrIssues.setVisibility(View.VISIBLE);
            } else {
                recyclerViewUserComplaintOrIssues.setVisibility(View.GONE);
                textNoComplaintsOrIssuesAvailable.setVisibility(View.VISIBLE);
                textNoComplaintsOrIssuesAvailable.setText("No Issues Available");
            }

            complaintOrIssueTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            showOrHideTabsBasedOnSelectedTab(AppConstants.MUNICIPAL_ISSUES_BADGE);
                            break;
                        case 1:
                            showOrHideTabsBasedOnSelectedTab(AppConstants.COMPLAINTS_BADGE);
                            break;
                        default:
                            showOrHideTabsBasedOnSelectedTab(AppConstants.MUNICIPAL_ISSUES_BADGE);
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            showOrHideTabsBasedOnSelectedTab(AppConstants.MUNICIPAL_ISSUES_BADGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOrHideTabsBasedOnSelectedTab(String selectedTab) {
        try {
            switch (selectedTab) {
                case AppConstants.COMPLAINTS_BADGE:
                    getUserComplaintList(loginUser, true);
                    break;

                case AppConstants.MUNICIPAL_ISSUES_BADGE:
                    getUserIssueList(loginUser, false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserIssues() {
        badgeIssues.setBackgroundColor(getResources().getColor(R.color.colorNewUxOrange, null));
        badgeComplaints.setBackgroundColor(getResources().getColor(R.color.colorDarkGrayBorder, null));

        if (mnIssueMasterList.size() > 0) {
            textNoComplaintsOrIssuesAvailable.setVisibility(View.GONE);
            recyclerViewUserComplaintOrIssues.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            recyclerViewUserComplaintOrIssues.setLayoutManager(linearLayoutManager);
            userIssueMainAdapter = new UserIssueMainAdapter(requireContext(), mnIssueMasterList, this);
            recyclerViewUserComplaintOrIssues.setAdapter(userIssueMainAdapter);

            if (userIssueMainAdapter != null) {
                userIssueMainAdapter.notifyDataSetChanged();
            }
        } else {
            recyclerViewUserComplaintOrIssues.setVisibility(View.GONE);
            textNoComplaintsOrIssuesAvailable.setVisibility(View.VISIBLE);
            textNoComplaintsOrIssuesAvailable.setText("No Issues Available");
        }

        badgeIssues.setNumber(Math.max(mnIssueMasterList.size(), 0));
        badgeComplaints.setNumber(Math.max(complaintMasterList.size(), 0));
    }

    private void loadUserComplaints(boolean loadOnlyBadge) {
        if (loadOnlyBadge) {
            badgeIssues.setBackgroundColor(getResources().getColor(R.color.colorNewUxOrange, null));
            badgeComplaints.setBackgroundColor(getResources().getColor(R.color.colorDarkGrayBorder, null));
        } else {
            badgeComplaints.setBackgroundColor(getResources().getColor(R.color.colorNewUxOrange, null));
            badgeIssues.setBackgroundColor(getResources().getColor(R.color.colorDarkGrayBorder, null));
            if (complaintMasterList.size() > 0) {
                textNoComplaintsOrIssuesAvailable.setVisibility(View.GONE);
                recyclerViewUserComplaintOrIssues.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
                recyclerViewUserComplaintOrIssues.setLayoutManager(linearLayoutManager);
                userComplaintMainAdapter = new UserComplaintMainAdapter(requireContext(), complaintMasterList, this);
                recyclerViewUserComplaintOrIssues.setAdapter(userComplaintMainAdapter);
            } else {
                recyclerViewUserComplaintOrIssues.setVisibility(View.GONE);
                textNoComplaintsOrIssuesAvailable.setVisibility(View.VISIBLE);
                textNoComplaintsOrIssuesAvailable.setText("No Complaints Available");
            }

            if (userComplaintMainAdapter != null) {
                userComplaintMainAdapter.notifyDataSetChanged();
            }
        }

        badgeIssues.setNumber(Math.max(mnIssueMasterList.size(), 0));
        badgeComplaints.setNumber(Math.max(complaintMasterList.size(), 0));
    }

    public void getUserIssueList(User user, boolean isSetUp) {
        try {
            showProgressDialog("Fetching details, please wait");
            mUserReferenceIssue.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: snapshot: " + snapshot);

                        DataSnapshot dataSnapshot = snapshot.child(user.getUserCity()).child(AppConstants.MUNICIPAL_ISSUE_TYPE).child(user.getMobileNumber());
                        Log.d(TAG, "onDataChange: dataSnapshot: " + dataSnapshot);
                        mnIssueMasterList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            MnIssueMaster mnIssueMaster = postSnapshot.getValue(MnIssueMaster.class);
                            if (mnIssueMaster != null) {
                                mnIssueMasterList.add(mnIssueMaster);
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: mnIssueMasterList:" + mnIssueMasterList);
                    if (isSetUp) {
                        setUpViews();
                    } else {
                        loadUserIssues();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    hideProgressDialog();
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                    if (isSetUp) {
                        setUpViews();
                    } else {
                        loadUserIssues();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserComplaintList(User user, boolean isShowProgress) {
        try {
            if (isShowProgress) {
                showProgressDialog("Fetching details, please wait");
            }

            mUserReferenceComplaint.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: snapshot: " + snapshot);

                        DataSnapshot dataSnapshot = snapshot.child(user.getUserCity()).child(AppConstants.COMPLAINT_TYPE).child(user.getMobileNumber());
                        Log.d(TAG, "onDataChange: dataSnapshot: " + dataSnapshot);
                        complaintMasterList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            ComplaintMaster complaintMaster = postSnapshot.getValue(ComplaintMaster.class);
                            if (complaintMaster != null) {
                                complaintMasterList.add(complaintMaster);
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: complaintMasterList:" + complaintMasterList);

                    if (isShowProgress) {
                        loadUserComplaints(false);
                    } else {
                        loadUserComplaints(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                    if (isShowProgress) {
                        hideProgressDialog();
                        loadUserComplaints(false);
                    } else {
                        loadUserComplaints(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userComplaintUpdateClicked(int position, ComplaintMaster complaintMaster, String userComplaintStatus) {

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

    @Override
    public void userIssueUpdateClicked(int position, MnIssueMaster mnIssueMaster, String userIssueStatus) {

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
}