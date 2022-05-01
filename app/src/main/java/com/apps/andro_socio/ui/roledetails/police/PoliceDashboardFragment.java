package com.apps.andro_socio.ui.roledetails.police;

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
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.complaint.ComplaintSubDetails;
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

public class PoliceDashboardFragment extends Fragment {
    private static final String TAG = PoliceDashboardFragment.class.getSimpleName();
    private View rootView;
    private MainActivityInteractor mainActivityInteractor;


    private TextView textNoComplaintsAvailable, textALlComplaintStatusHeader;
    private PieChart pieChartAllComplaintStatus;

    private LinearLayout allComplaintStatusPieChartLayout;

    private List<ComplaintMaster> complaintMasterList = new ArrayList<>();

    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_police_dashboard, container, false);
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
        mainActivityInteractor.setScreenTitle(getString(R.string.police_dashboard_title));
        progressDialog = new ProgressDialog(requireContext());

        ImageSlider imageSlider = rootView.findViewById(R.id.image_slider);
        List<SlideModel> slideModelList = SliderUtils.getPoliceDashboardSliderItemList();
        imageSlider.setImageList(slideModelList, ScaleTypes.FIT); // for all images
        imageSlider.startSliding(SliderUtils.SLIDER_TIME); // with new period

        setUpViews();
    }

    private void setUpViews() {
        try {

            textALlComplaintStatusHeader = rootView.findViewById(R.id.all_complaints_status_header);
            textNoComplaintsAvailable = rootView.findViewById(R.id.no_complaints_available);

            allComplaintStatusPieChartLayout = rootView.findViewById(R.id.complaints_status_chart_layout);
            pieChartAllComplaintStatus = rootView.findViewById(R.id.pie_chart_all_complaint_status);

            setPieChartOfComplaintStataus();

            loadAllComplaintStatusList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieChartOfComplaintStataus() {
        try {
            pieChartAllComplaintStatus.setUsePercentValues(false);
            pieChartAllComplaintStatus.setDrawEntryLabels(false);
            pieChartAllComplaintStatus.getDescription().setEnabled(false);
            pieChartAllComplaintStatus.setExtraOffsets(0, 0, 0, 10);

            pieChartAllComplaintStatus.setDragDecelerationFrictionCoef(0.95f);

            pieChartAllComplaintStatus.setDrawHoleEnabled(false);

            pieChartAllComplaintStatus.setTransparentCircleColor(Color.WHITE);
            pieChartAllComplaintStatus.setTransparentCircleAlpha(110);

            pieChartAllComplaintStatus.setHoleRadius(58f);
            pieChartAllComplaintStatus.setTransparentCircleRadius(61f);

            pieChartAllComplaintStatus.setDrawCenterText(false);

            pieChartAllComplaintStatus.setRotationAngle(0);
            // enable rotation of the chart by touch
            pieChartAllComplaintStatus.setRotationEnabled(true);
            pieChartAllComplaintStatus.setHighlightPerTapEnabled(true);

            pieChartAllComplaintStatus.animateY(500, Easing.EaseInOutQuad);
            // pieChartAllComplaintStatus.spin(2000, 0, 360);

            Legend l = pieChartAllComplaintStatus.getLegend();
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
            pieChartAllComplaintStatus.setEntryLabelColor(Color.WHITE);
            pieChartAllComplaintStatus.setEntryLabelTextSize(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadAllComplaintStatusList() {
        try {
            showProgressDialog("Complaint details loading..");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.COMPLAINT_LIST_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    hideProgressDialog();
                    complaintMasterList.clear();
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                       for(DataSnapshot typeSnapshot : citySnapshot.getChildren()){
                            for(DataSnapshot userSnapshot : typeSnapshot.getChildren()){
                                for(DataSnapshot compSnapshot : userSnapshot.getChildren()){
                                    ComplaintMaster complaintMaster = compSnapshot.getValue(ComplaintMaster.class);
                                    complaintMasterList.add(complaintMaster);
                                }
                            }

                        }
                    }
                    generatePieChartForComplaintStatusDetails(complaintMasterList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                    generatePieChartForComplaintStatusDetails(complaintMasterList);
                    Log.d(TAG, "onCancelled: failed to load user details");
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            generatePieChartForComplaintStatusDetails(complaintMasterList);
            Log.d(TAG, "loadAllUsers: exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePieChartForComplaintStatusDetails(List<ComplaintMaster> complaintMasterList) {
        try {
            if (complaintMasterList.size() > 0) {
                textNoComplaintsAvailable.setVisibility(View.GONE);
                allComplaintStatusPieChartLayout.setVisibility(View.VISIBLE);

                int newStatus = 0;
                int acceptedStatus = 0;
                int completedStatus = 0;
                int rejectedStatus = 0;
                int cancelledStatus = 0;
                int totalComplaints = 0;

                for (ComplaintMaster complaintMaster : complaintMasterList) {
                    if (complaintMaster.getComplaintsSubDetailsList().size() > 0) {
                        int lastPosition = complaintMaster.getComplaintsSubDetailsList().size() - 1;
                        ComplaintSubDetails complaintSubDetails = complaintMaster.getComplaintsSubDetailsList().get(lastPosition);
                        if (complaintSubDetails.getComplaintStatus().equalsIgnoreCase(AppConstants.NEW_STATUS)) {
                            newStatus = newStatus + 1;
                            totalComplaints = totalComplaints + 1;
                        } else if (complaintSubDetails.getComplaintStatus().equalsIgnoreCase(AppConstants.ACCEPTED_STATUS)) {
                            acceptedStatus = acceptedStatus + 1;
                            totalComplaints = totalComplaints + 1;
                        } else if (complaintSubDetails.getComplaintStatus().equalsIgnoreCase(AppConstants.COMPLETED_STATUS)) {
                            completedStatus = completedStatus + 1;
                            totalComplaints = totalComplaints + 1;
                        } else if (complaintSubDetails.getComplaintStatus().equalsIgnoreCase(AppConstants.REJECTED_STATUS)) {
                            rejectedStatus = rejectedStatus + 1;
                            totalComplaints = totalComplaints + 1;
                        } else if (complaintSubDetails.getComplaintStatus().equalsIgnoreCase(AppConstants.CANCELLED_STATUS)) {
                            cancelledStatus = cancelledStatus + 1;
                            totalComplaints = totalComplaints + 1;
                        }
                    }
                }

                Log.d(TAG, "generatePieChartForComplaintStatusDetails: newStatus: " + newStatus);
                Log.d(TAG, "generatePieChartForComplaintStatusDetails: acceptedStatus: " + acceptedStatus);
                Log.d(TAG, "generatePieChartForComplaintStatusDetails: completedStatus: " + completedStatus);
                Log.d(TAG, "generatePieChartForComplaintStatusDetails: rejectedStatus: " + rejectedStatus);
                Log.d(TAG, "generatePieChartForComplaintStatusDetails: cancelledStatus: " + cancelledStatus);
                Log.d(TAG, "generatePieChartForComplaintStatusDetails: totalComplaints: " + totalComplaints);

                ArrayList<PieEntry> entries = new ArrayList<>();


                entries.add(new PieEntry(newStatus, "New  "));
                entries.add(new PieEntry(acceptedStatus, "Accepted  "));
                entries.add(new PieEntry(completedStatus, "Completed  "));
                entries.add(new PieEntry(rejectedStatus, "Rejected  "));
                entries.add(new PieEntry(cancelledStatus, "Cancelled  "));

                String totalComplaintTitle = "Total complaints : " + totalComplaints;

                PieDataSet dataSet = new PieDataSet(entries, totalComplaintTitle);
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
                pieChartAllComplaintStatus.setData(data);

                // undo all highlights
                pieChartAllComplaintStatus.highlightValues(null);

                pieChartAllComplaintStatus.invalidate();
            } else {
                allComplaintStatusPieChartLayout.setVisibility(View.GONE);
                textNoComplaintsAvailable.setVisibility(View.VISIBLE);
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