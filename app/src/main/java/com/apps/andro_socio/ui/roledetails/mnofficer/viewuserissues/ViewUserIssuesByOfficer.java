package com.apps.andro_socio.ui.roledetails.mnofficer.viewuserissues;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.FireBaseDatabaseConstants;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewUserIssuesByOfficer extends Fragment implements UserIssueByOfficerMainAdapter.UserIssueItemClickListener {
    private static final String TAG = ViewUserIssuesByOfficer.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceIssue;

    private RecyclerView recyclerViewUserIssueByOfficer;
    private UserIssueByOfficerMainAdapter userIssueByOfficerMainAdapter;

    private List<MnIssueMaster> mnIssueMasterList = new ArrayList<>();


    public ViewUserIssuesByOfficer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_user_issues_by_officer, container, false);
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
            mainActivityInteractor.setScreenTitle(getString(R.string.view_municipal_issues));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceIssue = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.MN_ISSUE_LIST_TABLE);

            User loginUser = Utils.getLoginUserDetails(requireContext());
            getUserIssueList(loginUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            recyclerViewUserIssueByOfficer = rootView.findViewById(R.id.recycler_user_issues_by_officer);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            recyclerViewUserIssueByOfficer.setLayoutManager(linearLayoutManager);
            userIssueByOfficerMainAdapter = new UserIssueByOfficerMainAdapter(requireContext(), mnIssueMasterList, this);
            recyclerViewUserIssueByOfficer.setAdapter(userIssueByOfficerMainAdapter);

            if (userIssueByOfficerMainAdapter != null) {
                userIssueByOfficerMainAdapter.notifyDataSetChanged();
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

    public void getUserIssueList(User user) {
        try {
            showProgressDialog("Fetching details, please wait");
            mUserReferenceIssue.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: snapshot: " + snapshot);

                        DataSnapshot dataSnapshot = snapshot.child(user.getUserCity()).child(AppConstants.MUNICIPAL_ISSUE_TYPE);
                        Log.d(TAG, "onDataChange: dataSnapshot: " + dataSnapshot);
                        mnIssueMasterList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: child: "+postSnapshot);
                            for(DataSnapshot childData : postSnapshot.getChildren()){
                                MnIssueMaster mnIssueMaster = childData.getValue(MnIssueMaster.class);
                                if (mnIssueMaster != null) {
                                    mnIssueMasterList.add(mnIssueMaster);
                                }
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: mnIssueMasterList:" + mnIssueMasterList);
                    setUpViews();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    hideProgressDialog();
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                    setUpViews();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void userIssueUpdateClicked(int position, MnIssueMaster mnIssueMaster, String userIssueStatus) {

    }

    @Override
    public void userIssueViewClicked(int position, MnIssueMaster mnIssueMaster) {

    }
}