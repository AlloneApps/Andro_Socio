package com.apps.andro_socio.ui.roledetails.mnofficer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.FireBaseDatabaseConstants;
import com.apps.andro_socio.helper.SliderUtils;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.model.issue.MnIssueSubDetails;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MnOfficerDashboardFragment extends Fragment {
    private static final String TAG = MnOfficerDashboardFragment.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;

    private TextView textNoIssuesAvailable, textAllIssuesStatusHeader;
    private PieChart pieChartAllIssuesStatus;

    private LinearLayout allIssuesStatusPieChartLayout;

    private List<MnIssueMaster> issuesMasterList = new ArrayList<>();

    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mn_officer_dashboard, container, false);
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
        mainActivityInteractor.setScreenTitle(getString(R.string.mn_officer_dashboard_title));
        progressDialog = new ProgressDialog(requireContext());

        ImageSlider imageSlider = rootView.findViewById(R.id.image_slider);
        List<SlideModel> slideModelList = SliderUtils.getMnOfficerDashboardSliderItemList();
        imageSlider.setImageList(slideModelList, ScaleTypes.FIT); // for all images
        imageSlider.startSliding(SliderUtils.SLIDER_TIME); // with new period

        setUpViews();
    }

    private void setUpViews() {
        try {
            textAllIssuesStatusHeader = rootView.findViewById(R.id.all_complaints_status_header);
            textNoIssuesAvailable = rootView.findViewById(R.id.no_issues_available);

            allIssuesStatusPieChartLayout = rootView.findViewById(R.id.issues_status_chart_layout);
            pieChartAllIssuesStatus = rootView.findViewById(R.id.pie_chart_all_issues_status);

            setPieChartOfIssuesStataus();

            loadAllIssuesStatusList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieChartOfIssuesStataus() {
        try {
            pieChartAllIssuesStatus.setUsePercentValues(false);
            pieChartAllIssuesStatus.setDrawEntryLabels(false);
            pieChartAllIssuesStatus.getDescription().setEnabled(false);
            pieChartAllIssuesStatus.setExtraOffsets(0, 0, 0, 10);

            pieChartAllIssuesStatus.setDragDecelerationFrictionCoef(0.95f);

            pieChartAllIssuesStatus.setDrawHoleEnabled(false);

            pieChartAllIssuesStatus.setTransparentCircleColor(Color.WHITE);
            pieChartAllIssuesStatus.setTransparentCircleAlpha(110);

            pieChartAllIssuesStatus.setHoleRadius(58f);
            pieChartAllIssuesStatus.setTransparentCircleRadius(61f);

            pieChartAllIssuesStatus.setDrawCenterText(false);

            pieChartAllIssuesStatus.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChartAllIssuesStatus.setRotationEnabled(true);
            pieChartAllIssuesStatus.setHighlightPerTapEnabled(true);

            pieChartAllIssuesStatus.animateY(500, Easing.EaseInOutQuad);
            // pieChartAllIssuesStatus.spin(2000, 0, 360);

            Legend l = pieChartAllIssuesStatus.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(0f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            l.setXOffset(0f);
            l.setTextSize(8f);

            // entry label styling
            pieChartAllIssuesStatus.setEntryLabelColor(Color.WHITE);
            pieChartAllIssuesStatus.setEntryLabelTextSize(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadAllIssuesStatusList() {
        try {
            showProgressDialog("Issue details loading..");

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
                                    issuesMasterList.add(mnIssueMaster);
                                }
                            }
                        }
                    }
                    generatePieChartForIssueStatusDetails(issuesMasterList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                    generatePieChartForIssueStatusDetails(issuesMasterList);
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            generatePieChartForIssueStatusDetails(issuesMasterList);
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePieChartForIssueStatusDetails(List<MnIssueMaster> issuesMasterList) {
        try {
            if (issuesMasterList.size() > 0) {
                textNoIssuesAvailable.setVisibility(View.GONE);
                allIssuesStatusPieChartLayout.setVisibility(View.VISIBLE);

                int newStatus = 0;
                int acceptedStatus = 0;
                int completedStatus = 0;
                int rejectedStatus = 0;
                int cancelledStatus = 0;
                int totalIssues = 0;

                for (MnIssueMaster mnIssueMaster : issuesMasterList) {
                    if (mnIssueMaster.getMnIssueSubDetailsList().size() > 0) {
                        int lastPosition = mnIssueMaster.getMnIssueSubDetailsList().size() - 1;
                        MnIssueSubDetails mnIssueSubDetails = mnIssueMaster.getMnIssueSubDetailsList().get(lastPosition);
                        if (mnIssueSubDetails.getMnIssueStatus().equalsIgnoreCase(AppConstants.NEW_STATUS)) {
                            newStatus = newStatus + 1;
                            totalIssues = totalIssues + 1;
                        } else if (mnIssueSubDetails.getMnIssueStatus().equalsIgnoreCase(AppConstants.ACCEPTED_STATUS)) {
                            acceptedStatus = acceptedStatus + 1;
                            totalIssues = totalIssues + 1;
                        } else if (mnIssueSubDetails.getMnIssueStatus().equalsIgnoreCase(AppConstants.COMPLETED_STATUS)) {
                            completedStatus = completedStatus + 1;
                            totalIssues = totalIssues + 1;
                        } else if (mnIssueSubDetails.getMnIssueStatus().equalsIgnoreCase(AppConstants.REJECTED_STATUS)) {
                            rejectedStatus = rejectedStatus + 1;
                            totalIssues = totalIssues + 1;
                        } else if (mnIssueSubDetails.getMnIssueStatus().equalsIgnoreCase(AppConstants.CANCELLED_STATUS)) {
                            cancelledStatus = cancelledStatus + 1;
                            totalIssues = totalIssues + 1;
                        }
                    }
                }

                Log.d(TAG, "generatePieChartForIssueStatusDetails: newStatus: " + newStatus);
                Log.d(TAG, "generatePieChartForIssueStatusDetails: acceptedStatus: " + acceptedStatus);
                Log.d(TAG, "generatePieChartForIssueStatusDetails: completedStatus: " + completedStatus);
                Log.d(TAG, "generatePieChartForIssueStatusDetails: rejectedStatus: " + rejectedStatus);
                Log.d(TAG, "generatePieChartForIssueStatusDetails: cancelledStatus: " + cancelledStatus);
                Log.d(TAG, "generatePieChartForIssueStatusDetails: totalIssues: " + totalIssues);

                ArrayList<PieEntry> entries = new ArrayList<>();


                entries.add(new PieEntry(newStatus, "New  "));
                entries.add(new PieEntry(acceptedStatus, "Accepted  "));
                entries.add(new PieEntry(completedStatus, "Completed  "));
                entries.add(new PieEntry(rejectedStatus, "Rejected  "));
                entries.add(new PieEntry(cancelledStatus, "Cancelled  "));

                String totalIssuesText = "Total issues : " + totalIssues;

                PieDataSet dataSet = new PieDataSet(entries, totalIssuesText);
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);

                // add a lot of colors
                ArrayList<Integer> colors = new ArrayList<>();

                colors.add(Color.rgb(84, 153, 199));
                colors.add(Color.rgb(155, 89, 182));
                colors.add(Color.rgb(51, 255, 0));
                colors.add(Color.rgb(255, 110, 0));
                colors.add(Color.rgb(255, 87, 34));

                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new LargeValueFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.WHITE);
                pieChartAllIssuesStatus.setData(data);

                // undo all highlights
                pieChartAllIssuesStatus.highlightValues(null);

                pieChartAllIssuesStatus.invalidate();
            } else {
                allIssuesStatusPieChartLayout.setVisibility(View.GONE);
                textNoIssuesAvailable.setVisibility(View.VISIBLE);
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
}