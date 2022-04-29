package com.apps.andro_socio.ui.roledetails;

import android.content.Intent;
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
    private TextView textTitle, textComplainType, textComplaintHeader, textComplaintDesc, textComplaintPlace;
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

            loadPhotoDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        textTitle = findViewById(R.id.title);
        textComplainType = findViewById(R.id.text_complaint_or_issue_type);
        textComplaintHeader = findViewById(R.id.text_complaint_issue_title_value);
        textComplaintDesc = findViewById(R.id.text_complaint_issue_desc);
        textComplaintPlace = findViewById(R.id.text_complaint_issue_place);

        imageComplaintOrIssuePhoto = findViewById(R.id.photo_image);
        btnBack = findViewById(R.id.btn_back);

        if (isComplaint) {
            textTitle.setText("View Complaint Details");
        } else {
            textTitle.setText("View Municipal Issue Details");
        }

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

    private void loadPhotoDetails() {
        try {
            if (isComplaint) {
                if (complaintMaster != null) {
                    String complaintType = complaintMaster.getComplaintType() + " : " + complaintMaster.getComplaintPlacePhotoId();
                    textComplainType.setText(complaintType);
                    textComplaintHeader.setText(complaintMaster.getComplaintHeader());
                    textComplaintDesc.setText(complaintMaster.getComplaintDescription());
                    textComplaintPlace.setText(complaintMaster.getComplaintCity());

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
                    textComplainType.setText(complaintType);
                    textComplaintHeader.setText(mnIssueMaster.getMnIssueHeader());
                    textComplaintDesc.setText(mnIssueMaster.getMnIssueDescription());
                    textComplaintPlace.setText(mnIssueMaster.getMnIssueCity());

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