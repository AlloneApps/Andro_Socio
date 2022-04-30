package com.apps.andro_socio.ui.roledetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.bumptech.glide.Glide;

public class ViewComplaintOrIssueActivity extends AppCompatActivity {

    private static final String TAG = ViewComplaintOrIssueActivity.class.getSimpleName();
    private TextView textTitle, textIssueOrComplainType, textIssueOrComplaintHeader, textIssueOrComplaintDesc, textIssueOrComplaintPlace, textIssueOrComplaintAddress, textIssueOrComplaintLocation, textNavigateToLocation;
    private RecyclerView recyclerSubDetails;
    private ImageView imageComplaintOrIssuePhoto;
    private Button btnBack;

    private boolean isComplaint;
    private MnIssueMaster mnIssueMaster;
    private ComplaintMaster complaintMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_complaint_or_issue_viewer);

            if (getIntent() != null) {
                isComplaint = getIntent().getBooleanExtra(AppConstants.VIEW_COMPLAINT_OR_ISSUE_FLAG, false);
                mnIssueMaster = getIntent().getParcelableExtra(AppConstants.VIEW_MUNICIPAL_ISSUE_DATA);
                complaintMaster = getIntent().getParcelableExtra(AppConstants.VIEW_COMPLAINT_DATA);
            }
            Log.d(TAG, "onCreate: isComplaint: " + isComplaint);
            Log.d(TAG, "onCreate: mnIssueMaster: " + mnIssueMaster);
            Log.d(TAG, "onCreate: complaintMaster: " + complaintMaster);

            setUpViews();

            loadIssueOrComplaintDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        textTitle = findViewById(R.id.title);
        textIssueOrComplainType = findViewById(R.id.text_complaint_or_issue_type);
        textIssueOrComplaintHeader = findViewById(R.id.text_complaint_issue_title_value);
        textIssueOrComplaintDesc = findViewById(R.id.text_complaint_issue_desc);
        textIssueOrComplaintPlace = findViewById(R.id.text_complaint_issue_place);

        textIssueOrComplaintAddress = findViewById(R.id.text_complaint_issue_address);
        textIssueOrComplaintLocation = findViewById(R.id.text_complaint_issue_location);
        textNavigateToLocation = findViewById(R.id.text_navigate);

        imageComplaintOrIssuePhoto = findViewById(R.id.photo_image);
        btnBack = findViewById(R.id.btn_back);

        if (isComplaint) {
            textTitle.setText("View Complaint Details");
        } else {
            textTitle.setText("View Municipal Issue Details");
        }

        textNavigateToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isComplaint) {
                        if (complaintMaster != null && (complaintMaster.getComplaintPlaceLatitude() != 0.0)) {
                            openGoogleMapDirections(complaintMaster.getComplaintPlaceLatitude(), complaintMaster.getComplaintPlaceLongitude());
                        } else {
                            AndroSocioToast.showAlertToast(ViewComplaintOrIssueActivity.this, "Unable to fetch location", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    } else {
                        if (mnIssueMaster != null && (mnIssueMaster.getMnIssuePlaceLatitude() != 0.0)) {
                            openGoogleMapDirections(mnIssueMaster.getMnIssuePlaceLatitude(), mnIssueMaster.getMnIssuePlaceLongitude());
                        } else {
                            AndroSocioToast.showAlertToast(ViewComplaintOrIssueActivity.this, "Unable to fetch location", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callBackResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openGoogleMapDirections(double latitude, double longitude) {
        try {
            // By Default set Google Map
            String mapRequest = "google.navigation:q=" + latitude + "," + longitude + "&avoid=tf";
            Uri gmmIntentUri = Uri.parse(mapRequest);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIssueOrComplaintDetails() {
        try {
            if (isComplaint) {
                if (complaintMaster != null) {
                    String complaintType = complaintMaster.getComplaintType() + " : " + complaintMaster.getComplaintPlacePhotoId();
                    textIssueOrComplainType.setText(complaintType);
                    textIssueOrComplaintHeader.setText(complaintMaster.getComplaintHeader());
                    textIssueOrComplaintDesc.setText(complaintMaster.getComplaintDescription());
                    textIssueOrComplaintPlace.setText(complaintMaster.getComplaintCity());

                    if (complaintMaster.getComplaintPlaceAddress() == null || complaintMaster.getComplaintPlaceAddress().isEmpty()) {
                        textIssueOrComplaintAddress.setText("Address Not Available");
                        textIssueOrComplaintAddress.setTextColor(getResources().getColor(R.color.colorError, null));
                    } else {
                        textIssueOrComplaintAddress.setText(complaintMaster.getComplaintPlaceAddress());
                        textIssueOrComplaintAddress.setTextColor(getResources().getColor(R.color.colorBlack, null));
                    }

                    String locationText = "Location Not Available";
                    if (complaintMaster.getComplaintPlaceLatitude() != 0.0) {
                        locationText = "Latitude: " + complaintMaster.getComplaintPlaceLatitude() + ", Longitude: " + complaintMaster.getComplaintPlaceLongitude();
                        textIssueOrComplaintLocation.setTextColor(getResources().getColor(R.color.colorBlack, null));
                    }else{
                        textIssueOrComplaintLocation.setTextColor(getResources().getColor(R.color.colorError, null));
                    }
                    textIssueOrComplaintLocation.setText(locationText);

                    imageComplaintOrIssuePhoto = findViewById(R.id.complaint_or_issue_photo);

                    Glide.with(imageComplaintOrIssuePhoto)
                            .load(complaintMaster.getComplaintPlacePhotoPath())
                            .fitCenter()
                            .into(imageComplaintOrIssuePhoto);
                } else {
                    AndroSocioToast.showErrorToast(ViewComplaintOrIssueActivity.this, "Failed to load Complaint details", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            } else {
                if (mnIssueMaster != null) {
                    String complaintType = mnIssueMaster.getMnIssueType() + " : " + mnIssueMaster.getMnIssuePlacePhotoId();
                    textIssueOrComplainType.setText(complaintType);
                    textIssueOrComplaintHeader.setText(mnIssueMaster.getMnIssueHeader());
                    textIssueOrComplaintDesc.setText(mnIssueMaster.getMnIssueDescription());
                    textIssueOrComplaintPlace.setText(mnIssueMaster.getMnIssueCity());

                    if (mnIssueMaster.getMnIssuePlaceAddress() == null || mnIssueMaster.getMnIssuePlaceAddress().isEmpty()) {
                        textIssueOrComplaintAddress.setText("Address Not Available");
                        textIssueOrComplaintAddress.setTextColor(getResources().getColor(R.color.colorError, null));
                    } else {
                        textIssueOrComplaintAddress.setText(mnIssueMaster.getMnIssuePlaceAddress());
                        textIssueOrComplaintAddress.setTextColor(getResources().getColor(R.color.colorBlack, null));
                    }

                    String locationText = "Location Not Available";
                    if (mnIssueMaster.getMnIssuePlaceLatitude() != 0.0) {
                        locationText = "Latitude: " + mnIssueMaster.getMnIssuePlaceLatitude() + ", Longitude: " + mnIssueMaster.getMnIssuePlaceLongitude();
                        textIssueOrComplaintLocation.setTextColor(getResources().getColor(R.color.colorBlack, null));
                    }else{
                        textIssueOrComplaintLocation.setTextColor(getResources().getColor(R.color.colorError, null));
                    }

                    textIssueOrComplaintLocation.setText(locationText);

                    imageComplaintOrIssuePhoto = findViewById(R.id.complaint_or_issue_photo);

                    Glide.with(imageComplaintOrIssuePhoto)
                            .load(mnIssueMaster.getMnIssuePlacePhotoPath())
                            .fitCenter()
                            .into(imageComplaintOrIssuePhoto);
                } else {
                    AndroSocioToast.showErrorToast(ViewComplaintOrIssueActivity.this, "Failed to load Municipal Issue details", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callBackResult() {
        try {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}