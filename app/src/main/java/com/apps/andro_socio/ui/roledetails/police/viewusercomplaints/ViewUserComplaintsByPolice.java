package com.apps.andro_socio.ui.roledetails.police.viewusercomplaints;

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
import android.widget.Button;
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
import com.apps.andro_socio.helper.dataUtils.DataUtils;
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.complaint.ComplaintSubDetails;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.ViewComplaintOrIssueActivity;
import com.apps.andro_socio.ui.roledetails.user.createissueorcomplaint.CreateIssueOrComplaint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewUserComplaintsByPolice extends Fragment implements UserComplaintByPoliceMainAdapter.UserComplaintItemClickListener {

    private static final String TAG = CreateIssueOrComplaint.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;
    private TextView textNoComplaintsAvailable, textCityComplaintHeader;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceComplaint;

    private RecyclerView recyclerViewUserComplaintByPolice;
    private UserComplaintByPoliceMainAdapter userComplaintByPoliceMainAdapter;

    private List<ComplaintMaster> complaintMasterList = new ArrayList<>();

    private User loginUser;

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
            mainActivityInteractor.setScreenTitle(getString(R.string.my_city_complaints_header));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceComplaint = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.COMPLAINT_LIST_TABLE);

            loginUser = Utils.getLoginUserDetails(requireContext());
            getUserComplaintList(loginUser);

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
        try {
            if (userComplaintStatus.equalsIgnoreCase(AppConstants.COMPLETED_STATUS)) {
                AndroSocioToast.showAlertToast(requireContext(), "Complaint already Completed.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            } else if (userComplaintStatus.equalsIgnoreCase(AppConstants.CANCELLED_STATUS)) {
                AndroSocioToast.showAlertToast(requireContext(), "Complaint cancelled by User, can't Update.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            } else {
                showDialogForComplaintOrIssueStatusUpdate(requireContext(), position, userComplaintStatus, complaintMaster);
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

    public void showDialogForComplaintOrIssueStatusUpdate(Context context, int position, String currentStatus, ComplaintMaster complaintMaster) {
        try {
            String lastStatus = "";

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_issue_or_complaint_status_update, null);
            builder.setView(dialogView);
            builder.setCancelable(false);

            // TextView and EditText Initialization
            TextView textMainHeader = dialogView.findViewById(R.id.text_complaint_or_issue_status_update_main_header);
            TextView textComplaintOrIssueHeader = dialogView.findViewById(R.id.text_complaint_or_issue_header);
            TextView textComplaintOrIssueStatusHeader = dialogView.findViewById(R.id.text_complaint_or_issue_status_header);
            TextView textComplaintOrIssueStatusValue = dialogView.findViewById(R.id.text_complaint_or_issue_status_value);

            //Button Initialization
            Button btnUpdate = dialogView.findViewById(R.id.btn_update);
            Button btnClose = dialogView.findViewById(R.id.btn_close);

            List<String> complaintOrIssueNextStatusList = DataUtils.getNextStatusBasedOnRole(currentStatus, loginUser.getMainRole());

            RxView.touches(textComplaintOrIssueStatusValue).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                        builderSingle.setTitle(AppConstants.COMPLAINT_STATUS);

                        final ArrayAdapter<String> taskStatusSelectionAdapter = new ArrayAdapter<String>(requireContext(),
                                android.R.layout.select_dialog_singlechoice, complaintOrIssueNextStatusList) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        builderSingle.setNegativeButton("Cancel", (dialog, subPosition) -> dialog.dismiss());

                        builderSingle.setAdapter(taskStatusSelectionAdapter, (dialog, subPosition) -> {
                            textComplaintOrIssueStatusValue.setText(taskStatusSelectionAdapter.getItem(subPosition));
                        });
                        builderSingle.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            String headerMessage = complaintMaster.getComplaintType() + " : " + complaintMaster.getComplaintPlacePhotoId();
            textMainHeader.setText(headerMessage);
            textMainHeader.setTextColor(context.getResources().getColor(R.color.error_color, null));

            textComplaintOrIssueHeader.setText(complaintMaster.getComplaintHeader());

            textComplaintOrIssueStatusHeader.setText(AppConstants.COMPLAINT_STATUS);

            int lastPosition = (complaintMaster.getComplaintsSubDetailsList().size() - 1);
            Log.d(TAG, "lastPosition: " + lastPosition);

            if (lastPosition >= 0) {
                lastStatus = complaintMaster.getComplaintsSubDetailsList().get(lastPosition).getComplaintStatus();
                Log.d(TAG, "lastStatus: " + lastStatus);
                textComplaintOrIssueStatusValue.setText(lastStatus);
            }

            AlertDialog alert = builder.create();
            alert.show();

            String finalLastStatus = lastStatus;
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!(finalLastStatus.equalsIgnoreCase(textComplaintOrIssueStatusValue.getText().toString().trim()))) {
                            User loginUser = Utils.getLoginUserDetails(requireContext());

                            ComplaintSubDetails complaintSubDetails = new ComplaintSubDetails();
                            complaintSubDetails.setComplaintId(complaintMaster.getComplaintPlacePhotoId());
                            complaintSubDetails.setComplaintAcceptedId(loginUser.getMobileNumber());
                            complaintSubDetails.setComplaintStatus(textComplaintOrIssueStatusValue.getText().toString().trim());
                            complaintSubDetails.setModifiedBy(loginUser.getMobileNumber());
                            complaintSubDetails.setModifiedOn(Utils.getCurrentTimeStampWithSeconds());
                            complaintSubDetails.setComplaintAcceptedRole(loginUser.getMainRole());
                            complaintMaster.getComplaintsSubDetailsList().add(complaintSubDetails);

                            Log.d(TAG, "onClick: complaintMain: " + complaintMaster);

                            updateIssueOrComplaintDetails(position, complaintMaster, alert);
                        } else {
                            AndroSocioToast.showInfoToast(requireContext(), "Nothing to Update.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
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

    public void updateIssueOrComplaintDetails(int position, ComplaintMaster complaintMaster, AlertDialog alert) {
        try {
            showProgressDialog("Updating status please wait.");
            mUserReferenceComplaint.child(complaintMaster.getComplaintCity()).child(complaintMaster.getComplaintType()).child(complaintMaster.getComplaintAcceptedOfficerId()).child(complaintMaster.getComplaintPlacePhotoId()).setValue(complaintMaster)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            AndroSocioToast.showSuccessToast(requireContext(), "Complaint status updated successfully.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_LONG);
                            if (alert != null) {
                                alert.dismiss();
                            }
                            if (userComplaintByPoliceMainAdapter != null) {
                                userComplaintByPoliceMainAdapter.notifyItemChanged(position);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            AndroSocioToast.showErrorToast(requireContext(), "Failed to update complaint", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }
}