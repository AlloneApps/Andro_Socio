package com.apps.andro_socio.ui.roledetails.user.usermain;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.FireBaseDatabaseConstants;
import com.apps.andro_socio.helper.NetworkUtil;
import com.apps.andro_socio.helper.SliderUtils;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.apps.andro_socio.ui.roledetails.ViewComplaintOrIssueActivity;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardFragment extends Fragment implements UserDashboardMainAdapter.MnIssueMasterClickListener {
    private static final String TAG = UserDashboardFragment.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;

    private TextView textNoPublicIssuesAvailable;
    private RecyclerView recyclerPublicIssues;
    private UserDashboardMainAdapter userDashboardIssueMainAdapter;

    private List<MnIssueMaster> issuesMasterList = new ArrayList<>();

    private ProgressDialog progressDialog;

    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    final private int MULTIPLE_PERMISSIONS = 124;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_dashboard, container, false);
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
        mainActivityInteractor.setScreenTitle(getString(R.string.user_dashboard_title));
        progressDialog = new ProgressDialog(requireContext());

        ImageSlider imageSlider = rootView.findViewById(R.id.image_slider);
        List<SlideModel> slideModelList = SliderUtils.getUserDashboardSliderItemList();
        imageSlider.setImageList(slideModelList, ScaleTypes.FIT); // for all images
        imageSlider.startSliding(SliderUtils.SLIDER_TIME); // with new period

        checkPermissions();
//        setUpViews();
        loadAllIssuesStatusList();
    }

    private void setUpViews() {
        try {
            textNoPublicIssuesAvailable = rootView.findViewById(R.id.no_issues_available);
            recyclerPublicIssues = rootView.findViewById(R.id.recycler_public_issue);

            if (issuesMasterList.size() > 0) {
                textNoPublicIssuesAvailable.setVisibility(View.GONE);
                recyclerPublicIssues.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new GridLayoutManager(requireContext(), 1);
                recyclerPublicIssues.setLayoutManager(linearLayoutManager);
                userDashboardIssueMainAdapter = new UserDashboardMainAdapter(requireContext(), issuesMasterList, this);
                recyclerPublicIssues.setAdapter(userDashboardIssueMainAdapter);

                if (userDashboardIssueMainAdapter != null) {
                    userDashboardIssueMainAdapter.notifyDataSetChanged();
                }
            } else {
                recyclerPublicIssues.setVisibility(View.GONE);
                textNoPublicIssuesAvailable.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllIssuesStatusList() {
        try {
            showProgressDialog("All public issues loading..");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.MN_ISSUE_LIST_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    hideProgressDialog();
                    issuesMasterList.clear();
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        for (DataSnapshot typeSnapshot : citySnapshot.getChildren()) {
                            for (DataSnapshot userSnapshot : typeSnapshot.getChildren()) {
                                for (DataSnapshot compSnapshot : userSnapshot.getChildren()) {
                                    MnIssueMaster mnIssueMaster = compSnapshot.getValue(MnIssueMaster.class);
                                    if (mnIssueMaster != null) {
                                        if (mnIssueMaster.getMnIssueAccessType().equalsIgnoreCase(AppConstants.ISSUE_ACCESS_TYPE_PUBLIC)) {
                                            issuesMasterList.add(mnIssueMaster);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    setUpViews();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                    setUpViews();
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            setUpViews();
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void mnIssueMasterClicked(int position, ImageView imageIssue, TextView textIssueHeader, MnIssueMaster mnIssueMaster) {
        try {

        /*    if (NetworkUtil.getConnectivityStatus(requireContext())) {
                Intent intentView = new Intent(requireContext(), ViewComplaintOrIssueActivity.class);
                intentView.putExtra(AppConstants.VIEW_MUNICIPAL_ISSUE_DATA, mnIssueMaster);
                intentView.putExtra(AppConstants.VIEW_COMPLAINT_DATA, new ComplaintMaster());
                intentView.putExtra(AppConstants.VIEW_COMPLAINT_OR_ISSUE_FLAG, false);

                Pair<View, String> transactionPairOne = Pair.create((View) imageIssue, requireContext().getResources().getString(R.string.transaction_complaint_or_issue_photo));
                Pair<View, String> transactionPairTwo = Pair.create((View) textIssueHeader, requireContext().getResources().getString(R.string.transaction_complaint_or_issue_header));

                // Call Multiple Shared Transaction using Pair Option
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(requireActivity(), transactionPairOne, transactionPairTwo);
                startActivityForResult(intentView, 3, options.toBundle());

            } else {
                AndroSocioToast.showErrorToast(requireContext(), getString(R.string.no_internet), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            }*/

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


    private boolean checkPermissions() {
        try {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                result = ContextCompat.checkSelfPermission(requireContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(requireActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case MULTIPLE_PERMISSIONS: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permissions granted.
                    } else {
                        String perStr = "";
                        for (String per : permissions) {
                            perStr += "\n" + per;
                        }
                        // permissions list of don't granted permission
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}