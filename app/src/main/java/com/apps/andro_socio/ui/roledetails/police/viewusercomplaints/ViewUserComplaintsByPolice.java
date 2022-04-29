package com.apps.andro_socio.ui.roledetails.police.viewusercomplaints;

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
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.user.createissueorcomplaint.CreateIssueOrComplaint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewUserComplaintsByPolice extends Fragment implements UserComplaintByPoliceMainAdapter.UserComplaintItemClickListener {

    private static final String TAG = CreateIssueOrComplaint.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceComplaint;

    private RecyclerView recyclerViewUserComplaintByPolice;
    private UserComplaintByPoliceMainAdapter userComplaintByPoliceMainAdapter;

    private List<ComplaintMaster> complaintMasterList = new ArrayList<>();


    public ViewUserComplaintsByPolice() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_view_user_complaints_by_police, container, false);
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
            mainActivityInteractor.setScreenTitle(getString(R.string.view_complaints));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceComplaint = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.COMPLAINT_LIST_TABLE);

            User loginUser = Utils.getLoginUserDetails(requireContext());
            getUserComplaintList(loginUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            recyclerViewUserComplaintByPolice = rootView.findViewById(R.id.recycler_user_issues_by_police);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            recyclerViewUserComplaintByPolice.setLayoutManager(linearLayoutManager);
            userComplaintByPoliceMainAdapter = new UserComplaintByPoliceMainAdapter(requireContext(), complaintMasterList, this);
            recyclerViewUserComplaintByPolice.setAdapter(userComplaintByPoliceMainAdapter);

            if (userComplaintByPoliceMainAdapter != null) {
                userComplaintByPoliceMainAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getUserComplaintList(User user) {
        try {
            showProgressDialog("Fetching details, please wait");
            mUserReferenceComplaint.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    hideProgressDialog();
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: snapshot: " + snapshot);

                        DataSnapshot dataSnapshot = snapshot.child(user.getUserCity()).child(AppConstants.COMPLAINT_TYPE);
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
    public void userComplaintUpdateClicked(int position, ComplaintMaster complaintMaster, String userComplaintStatus) {

    }

    @Override
    public void userComplaintViewClicked(int position, ComplaintMaster complaintMaster) {

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
}