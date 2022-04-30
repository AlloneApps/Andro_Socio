package com.apps.andro_socio.ui.roledetails.mnofficer.viewuserissues;

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
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.model.issue.MnIssueSubDetails;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.ViewComplaintOrIssueActivity;
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

public class ViewUserIssuesByOfficer extends Fragment implements UserIssueByOfficerMainAdapter.UserIssueItemClickListener {
    private static final String TAG = ViewUserIssuesByOfficer.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;
    private ProgressDialog progressDialog;
    private TextView textCityIssuesHeader, textNoIssuesAvailable;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceIssue;

    private RecyclerView recyclerViewUserIssueByOfficer;
    private UserIssueByOfficerMainAdapter userIssueByOfficerMainAdapter;

    private List<MnIssueMaster> mnIssueMasterList = new ArrayList<>();
    private User loginUser;

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
            mainActivityInteractor.setScreenTitle(getString(R.string.mn_officer_btn_second_option));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceIssue = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.MN_ISSUE_LIST_TABLE);

            loginUser = Utils.getLoginUserDetails(requireContext());

            if (loginUser != null) {
                getUserIssueList(loginUser);
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
                textCityIssuesHeader.setVisibility(View.GONE);
                recyclerViewUserIssueByOfficer.setVisibility(View.GONE);
                textNoIssuesAvailable.setVisibility(View.VISIBLE);
            }

            if (loginUser != null) {
                String cityIssueText = loginUser.getUserCity() + " City Municipal Issues";
                textCityIssuesHeader.setText(cityIssueText);
            }

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
        try {
            if (userIssueStatus.equalsIgnoreCase(AppConstants.COMPLETED_STATUS)) {
                AndroSocioToast.showAlertToast(requireContext(), "Issue already Completed.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            } else if (userIssueStatus.equalsIgnoreCase(AppConstants.CANCELLED_STATUS)) {
                AndroSocioToast.showAlertToast(requireContext(), "Issue cancelled by User, can't Update.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            } else {
                showDialogForComplaintOrIssueStatusUpdate(requireContext(), position, userIssueStatus, mnIssueMaster);
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

    public void showDialogForComplaintOrIssueStatusUpdate(Context context, int position, String currentStatus, MnIssueMaster mnIssueMaster) {
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
                        builderSingle.setTitle(AppConstants.MUNICIPAL_ISSUE_STATUS);

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

            String headerMessage = mnIssueMaster.getMnIssueType() + " : " + mnIssueMaster.getMnIssuePlacePhotoId();
            textMainHeader.setText(headerMessage);
            textMainHeader.setTextColor(context.getResources().getColor(R.color.error_color, null));

            textComplaintOrIssueHeader.setText(mnIssueMaster.getMnIssueHeader());

            textComplaintOrIssueStatusHeader.setText(AppConstants.MUNICIPAL_ISSUE_STATUS);

            int lastPosition = (mnIssueMaster.getMnIssueSubDetailsList().size() - 1);
            Log.d(TAG, "lastPosition: " + lastPosition);

            if (lastPosition >= 0) {
                lastStatus = mnIssueMaster.getMnIssueSubDetailsList().get(lastPosition).getMnIssueStatus();
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

                            MnIssueSubDetails mnIssueSubDetails = new MnIssueSubDetails();
                            mnIssueSubDetails.setMnIssueId(mnIssueMaster.getMnIssuePlacePhotoId());
                            mnIssueSubDetails.setMnIssueAcceptedId(loginUser.getMobileNumber());
                            mnIssueSubDetails.setMnIssueStatus(textComplaintOrIssueStatusValue.getText().toString().trim());
                            mnIssueSubDetails.setMnIssueModifiedBy(loginUser.getMobileNumber());
                            mnIssueSubDetails.setMnIssueModifiedOn(Utils.getCurrentTimeStampWithSeconds());
                            mnIssueSubDetails.setMnIssueAcceptedRole(loginUser.getMainRole());
                            mnIssueMaster.getMnIssueSubDetailsList().add(mnIssueSubDetails);

                            Log.d(TAG, "onClick: complaintMain: " + mnIssueMaster);

                            updateIssueOrComplaintDetails(position, mnIssueMaster, alert);
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

    public void updateIssueOrComplaintDetails(int position, MnIssueMaster mnIssueMaster, AlertDialog alert) {
        try {
            showProgressDialog("Updating status please wait.");
            mUserReferenceIssue.child(mnIssueMaster.getMnIssueCity()).child(mnIssueMaster.getMnIssueType()).child(mnIssueMaster.getMnIssueAcceptedOfficerId()).child(mnIssueMaster.getMnIssuePlacePhotoId()).setValue(mnIssueMaster)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            AndroSocioToast.showSuccessToast(requireContext(), "Issue status updated successfully.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_LONG);
                            if (alert != null) {
                                alert.dismiss();
                            }
                            if (userIssueByOfficerMainAdapter != null) {
                                userIssueByOfficerMainAdapter.notifyItemChanged(position);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            AndroSocioToast.showErrorToast(requireContext(), "Failed to update issue.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }
}