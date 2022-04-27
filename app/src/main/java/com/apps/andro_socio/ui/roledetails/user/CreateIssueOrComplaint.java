package com.apps.andro_socio.ui.roledetails.user;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.apps.andro_socio.BuildConfig;
import com.apps.andro_socio.R;
import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.helper.FireBaseDatabaseConstants;
import com.apps.andro_socio.helper.NetworkUtil;
import com.apps.andro_socio.helper.Utils;
import com.apps.andro_socio.helper.androSocioToast.AndroSocioToast;
import com.apps.andro_socio.model.User;
import com.apps.andro_socio.model.citydetails.City;
import com.apps.andro_socio.model.complaint.ComplaintMaster;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.apps.andro_socio.ui.roledetails.MainActivityInteractor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.rxbinding.view.RxView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import rx.functions.Action1;

public class CreateIssueOrComplaint extends Fragment {
    private static final String TAG = CreateIssueOrComplaint.class.getSimpleName();
    private View rootView;
    private CoordinatorLayout issueOrComplaintCoordinator;

    private TextView textCity, textIssueAccessTypeHeader;
    private EditText editIssueOrComplaintTitle, editIssueOrComplaintDesc;
    private ImageView imageSelectedPhoto, imageCameraIcon;
    private Button btnSubmit;
    private ProgressDialog progressDialog;
    private long MAX_2_MB = 2000000;
    private String issueOrComplaintType = "";
    private String issueAccessType = AppConstants.ISSUE_ACCESS_TYPE_PRIVATE;

    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    final private int MULTIPLE_PERMISSIONS = 124;


    private static final int GALLERY_REQUEST_CODE = 111;
    private static final int CAMERA_REQUEST_CODE = 222;
    private Uri cropImageUri, photoUploadUri;
    private String cameraFilePath;

    // Firebase Storage
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserReferenceComplaint;
    private DatabaseReference mUserReferenceIssue;

    private StorageReference storageReference;

    private List<City> cityList = new ArrayList<>();
    private List<String> cityStringList = new ArrayList<>();

    private RadioGroup radioGroupIssueOrComplaint, radioIssueAccessType;
    private MainActivityInteractor mainActivityInteractor;

    public CreateIssueOrComplaint() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for requireContext() fragment
        rootView = inflater.inflate(R.layout.fragment_create_issue_or_complaint, container, false);
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
            mainActivityInteractor.setScreenTitle(getString(R.string.create_complaint_issue));

            progressDialog = new ProgressDialog(requireContext());

            firebaseDatabase = FirebaseDatabase.getInstance();
            mUserReferenceIssue = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.MN_ISSUE_LIST_TABLE);
            mUserReferenceComplaint = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.COMPLAINT_LIST_TABLE);
            storageReference = FirebaseStorage.getInstance().getReference();

            getCityList();

            checkPermissions();

            setUpViews();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpViews() {
        try {
            issueOrComplaintCoordinator = rootView.findViewById(R.id.create_issue_or_complaint_coordinate);
            textCity = rootView.findViewById(R.id.text_city);
            editIssueOrComplaintTitle = rootView.findViewById(R.id.edit_issue_or_complaint);
            editIssueOrComplaintDesc = rootView.findViewById(R.id.edit_complaint_or_issue_desc);

            radioGroupIssueOrComplaint = rootView.findViewById(R.id.radio_issue_or_complaint);
            radioIssueAccessType = rootView.findViewById(R.id.radio_issue_access_type);

            textIssueAccessTypeHeader = rootView.findViewById(R.id.text_issue_access_type_header);

            radioIssueAccessType.setVisibility(View.GONE);
            textIssueAccessTypeHeader.setVisibility(View.GONE);

            imageSelectedPhoto = rootView.findViewById(R.id.place_image);
            imageCameraIcon = rootView.findViewById(R.id.image_camera_icon);
            btnSubmit = rootView.findViewById(R.id.btn_submit);

            radioGroupIssueOrComplaint.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    if (id != 0) {
                        switch (id) {
                            case R.id.radio_issue: {
                                issueOrComplaintType = AppConstants.MUNICIPAL_ISSUE_TYPE;
                                radioIssueAccessType.setVisibility(View.VISIBLE);
                                textIssueAccessTypeHeader.setVisibility(View.VISIBLE);
                                break;
                            }
                            case R.id.radio_complaint: {
                                radioIssueAccessType.setVisibility(View.GONE);
                                radioIssueAccessType.clearCheck();
                                textIssueAccessTypeHeader.setVisibility(View.GONE);
                                issueOrComplaintType = AppConstants.COMPLAINT_TYPE;
                                break;
                            }
                        }
                    }
                }
            });

            radioIssueAccessType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    if (id != 0) {
                        switch (id) {
                            case R.id.radio_issue_access_private: {
                                issueAccessType = AppConstants.ISSUE_ACCESS_TYPE_PRIVATE;
                                break;
                            }
                            case R.id.radio_issue_access_public: {
                                issueAccessType = AppConstants.ISSUE_ACCESS_TYPE_PUBLIC;
                                break;
                            }
                        }
                    }
                }
            });

            imageSelectedPhoto.setImageDrawable(ResourcesCompat.getDrawable(requireContext().getResources(), R.drawable.empty_image, null));

            RxView.touches(textCity).subscribe(motionEvent -> {
                try {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(requireContext());
                        builderSingle.setTitle("Select City");

                        final ArrayAdapter<String> citySelectionAdapter = new ArrayAdapter<String>(requireContext(),
                                android.R.layout.select_dialog_singlechoice, cityStringList) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        builderSingle.setNegativeButton("Cancel", (dialog, position) -> dialog.dismiss());

                        builderSingle.setAdapter(citySelectionAdapter, (dialog, position) -> {
                            textCity.setText(citySelectionAdapter.getItem(position));
                        });
                        builderSingle.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkUtil.getConnectivityStatus(requireContext())) {
                        if (validateFields()) {
                            switch (radioGroupIssueOrComplaint.getCheckedRadioButtonId()) {
                                case R.id.radio_issue: {
                                    if (radioIssueAccessType.getCheckedRadioButtonId() == R.id.radio_issue_access_public) {
                                        submitMnIssue(AppConstants.ISSUE_ACCESS_TYPE_PUBLIC);
                                    } else {
                                        submitMnIssue(AppConstants.ISSUE_ACCESS_TYPE_PRIVATE);
                                    }
                                    break;
                                }
                                case R.id.radio_complaint: {
                                    submitPoliceComplaint();
                                    break;
                                }
                            }
                        }
                    } else {
                        AndroSocioToast.showErrorToast(requireContext(), getString(R.string.no_internet), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                    }
                }
            });

            RxView.clicks(imageCameraIcon).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    try {
                        showProfilePicChooser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitMnIssue(String issueAccessType) {
        try {
            User loginUser = Utils.getLoginUserDetails(requireContext());

            String userId = loginUser.getMobileNumber();
            String userName = loginUser.getFullName();
            String selectedCity = textCity.getText().toString().trim();

            MnIssueMaster mnIssueMaster = new MnIssueMaster();
            mnIssueMaster.setMnIssueCity(selectedCity);
            mnIssueMaster.setMnIssueType(AppConstants.MUNICIPAL_ISSUE_TYPE);
            mnIssueMaster.setMnIssueAccessType(issueAccessType);
            mnIssueMaster.setMnIssueHeader(editIssueOrComplaintTitle.getText().toString().trim());
            mnIssueMaster.setMnIssueDescription(editIssueOrComplaintDesc.getText().toString().trim());
            mnIssueMaster.setMnIssueAcceptedOfficerId(userId);
            mnIssueMaster.setMnIssueAcceptedOfficerName(userName);
            mnIssueMaster.setMnIssuePlacePhotoId(Utils.getCurrentTimeStampWithSecondsAsId());
            mnIssueMaster.setMnIssuePlacePhotoUploadedDate(Utils.getCurrentTimeStampWithSeconds());
            // Initial PhotoPath is Empty
            mnIssueMaster.setMnIssuePlacePhotoPath("");
            mnIssueMaster.setMnIssueCreatedOn(Utils.getCurrentTimeStampWithSeconds());
            mnIssueMaster.setMnIssuePlaceLatitude(0.0);
            mnIssueMaster.setMnIssuePlaceLongitude(0.0);

            long photoSize = getFileSize(photoUploadUri);

            Log.d(TAG, "onClick: photoSize:" + photoSize);
            if (photoSize > MAX_2_MB) {
                int scaleDivider = 4;

                try {
                    // 1. Convert uri to bitmap
                    Bitmap fullBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUploadUri);

                    // 2. Get the downsized image content as a byte[]
                    int scaleWidth = fullBitmap.getWidth() / scaleDivider;
                    int scaleHeight = fullBitmap.getHeight() / scaleDivider;
                    byte[] downsizedImageBytes =
                            getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight);

                    if (downsizedImageBytes != null) {
                        Log.d(TAG, "onClick: mnIssueMaster down: " + mnIssueMaster);
                        Log.d(TAG, "onClick: mnIssueMaster down: " + downsizedImageBytes);
                        upLoadPlacePhotoMoreSizeMnIssue(mnIssueMaster, downsizedImageBytes, userId);
                    } else {
                        AndroSocioToast.showErrorToast(requireContext(), "Failed to reduce photo size, try again.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                    }
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            } else {
                Log.d(TAG, "onClick: mnIssueMaster: " + mnIssueMaster);
                Log.d(TAG, "onClick: photoUploadUri: " + photoUploadUri);
                upLoadPlacePhotoOfMnIssue(mnIssueMaster, photoUploadUri, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitPoliceComplaint() {
        try {
            User loginUser = Utils.getLoginUserDetails(requireContext());

            String userId = loginUser.getMobileNumber();
            String userName = loginUser.getFullName();
            String selectedCity = textCity.getText().toString().trim();

            ComplaintMaster complaintMaster = new ComplaintMaster();
            complaintMaster.setComplaintCity(selectedCity);
            complaintMaster.setComplaintType(AppConstants.COMPLAINT_TYPE);
            complaintMaster.setComplaintAccessType(AppConstants.ISSUE_ACCESS_TYPE_PRIVATE);
            complaintMaster.setComplaintHeader(editIssueOrComplaintTitle.getText().toString().trim());
            complaintMaster.setComplaintDescription(editIssueOrComplaintDesc.getText().toString().trim());
            complaintMaster.setComplaintAcceptedOfficerId(userId);
            complaintMaster.setComplaintAcceptedOfficerName(userName);
            complaintMaster.setComplaintPlacePhotoId(Utils.getCurrentTimeStampWithSecondsAsId());
            complaintMaster.setComplaintPlacePhotoUploadedDate(Utils.getCurrentTimeStampWithSeconds());
            // Initial PhotoPath is Empty
            complaintMaster.setComplaintPlacePhotoPath("");
            complaintMaster.setComplaintCreatedOn(Utils.getCurrentTimeStampWithSeconds());
            complaintMaster.setComplaintPlaceLatitude(0.0);
            complaintMaster.setComplaintPlaceLongitude(0.0);

            long photoSize = getFileSize(photoUploadUri);

            Log.d(TAG, "onClick: photoSize:" + photoSize);
            if (photoSize > MAX_2_MB) {
                int scaleDivider = 4;

                try {
                    // 1. Convert uri to bitmap
                    Bitmap fullBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUploadUri);

                    // 2. Get the downsized image content as a byte[]
                    int scaleWidth = fullBitmap.getWidth() / scaleDivider;
                    int scaleHeight = fullBitmap.getHeight() / scaleDivider;
                    byte[] downsizedImageBytes =
                            getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight);

                    if (downsizedImageBytes != null) {
                        Log.d(TAG, "onClick: complaintMaster down: " + complaintMaster);
                        Log.d(TAG, "onClick: complaintMaster down: " + downsizedImageBytes);
                        upLoadPlacePhotoMoreSizeComplaint(complaintMaster, downsizedImageBytes, userId);
                    } else {
                        AndroSocioToast.showErrorToast(requireContext(), "Failed to reduce photo size, try again.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                    }
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            } else {
                Log.d(TAG, "onClick: complaintMaster: " + complaintMaster);
                Log.d(TAG, "onClick: photoUploadUri: " + photoUploadUri);
                upLoadPlacePhotoOfComplaint(complaintMaster, photoUploadUri, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        try {
            if (textCity.getText().toString().trim().isEmpty()) {
                AndroSocioToast.showErrorToast(requireContext(), "Please select city", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            } else if ((!(radioGroupIssueOrComplaint.getCheckedRadioButtonId() == R.id.radio_issue || radioGroupIssueOrComplaint.getCheckedRadioButtonId() == R.id.radio_complaint))) {
                AndroSocioToast.showErrorToast(requireContext(), "Please select option Issue or Complaint", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            } else if ((radioGroupIssueOrComplaint.getCheckedRadioButtonId() == R.id.radio_issue) && (!(radioIssueAccessType.getCheckedRadioButtonId() == R.id.radio_issue_access_private || radioIssueAccessType.getCheckedRadioButtonId() == R.id.radio_issue_access_public))) {
                AndroSocioToast.showErrorToast(requireContext(), "Please select Issue Access Type", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            } else if (editIssueOrComplaintTitle.getText().toString().trim().isEmpty()) {
                AndroSocioToast.showErrorToast(requireContext(), "Please enter issue or complaint title", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            } else if (editIssueOrComplaintDesc.getText().toString().trim().isEmpty()) {
                AndroSocioToast.showErrorToast(requireContext(), "Please enter issue or complaint description", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            } else if (photoUploadUri == null) {
                AndroSocioToast.showErrorToast(requireContext(), "Please select photo", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AndroSocioToast.showErrorToast(requireContext(), "Exception occurred, please try again.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            return false;
        }
    }

    public void getCityList() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FireBaseDatabaseConstants.CITY_TABLE);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        cityList.clear();
                        cityStringList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            City city = postSnapshot.getValue(City.class);
                            if (city != null) {
                                cityList.add(city);
                                cityStringList.add(city.getCityName());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: error: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upLoadPlacePhotoOfMnIssue(MnIssueMaster mnIssueMaster, Uri photoUploadUri, String userId) {
        try {
            showProgressDialog("Processing your request..");

            String photoExt = mnIssueMaster.getMnIssueCity() + "_" + userId + "_" + mnIssueMaster.getMnIssuePlacePhotoId() + "." + getFileExtension(photoUploadUri);
            StorageReference fileRef = storageReference.child(photoExt);
            fileRef.putFile(photoUploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: uri: " + uri);
                            mnIssueMaster.setMnIssuePlacePhotoPath(uri.toString());
                            hideProgressDialog();
                            submitPhotoMnIssueDetails(mnIssueMaster, userId);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    hideProgressDialog();
                    AndroSocioToast.showErrorToast(requireContext(), "Failed to upload photo", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    private void upLoadPlacePhotoOfComplaint(ComplaintMaster complaintMaster, Uri photoUploadUri, String userId) {
        try {
            showProgressDialog("Processing your request..");

            String photoExt = complaintMaster.getComplaintCity() + "_" + userId + "_" + complaintMaster.getComplaintPlacePhotoId() + "." + getFileExtension(photoUploadUri);
            StorageReference fileRef = storageReference.child(photoExt);
            fileRef.putFile(photoUploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: uri: " + uri);
                            complaintMaster.setComplaintPlacePhotoPath(uri.toString());
                            hideProgressDialog();
                            submitPhotoComplaintDetails(complaintMaster, userId);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    hideProgressDialog();
                    AndroSocioToast.showErrorToast(requireContext(), "Failed to upload photo", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    private void upLoadPlacePhotoMoreSizeMnIssue(MnIssueMaster mnIssueMaster, byte[] downsizedImageBytes, String userId) {
        try {
            showProgressDialog("Processing your request..");
            String photoExt = mnIssueMaster.getMnIssueCity() + "_" + userId + "_" + mnIssueMaster.getMnIssuePlacePhotoId() + "." + getFileExtension(photoUploadUri);
            StorageReference fileRef = storageReference.child(photoExt);
            fileRef.putBytes(downsizedImageBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: 2: " + uri);
                            mnIssueMaster.setMnIssuePlacePhotoPath(uri.toString());
                            hideProgressDialog();
                            submitPhotoMnIssueDetails(mnIssueMaster, userId);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    hideProgressDialog();
                    AndroSocioToast.showErrorToast(requireContext(), "Failed to upload photo", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }
    private void upLoadPlacePhotoMoreSizeComplaint(ComplaintMaster complaintMaster, byte[] downsizedImageBytes, String userId) {
        try {
            showProgressDialog("Processing your request..");
            String photoExt = complaintMaster.getComplaintCity() + "_" + userId + "_" + complaintMaster.getComplaintPlacePhotoId() + "." + getFileExtension(photoUploadUri);
            StorageReference fileRef = storageReference.child(photoExt);
            fileRef.putBytes(downsizedImageBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: 2: " + uri);
                            complaintMaster.setComplaintPlacePhotoPath(uri.toString());
                            hideProgressDialog();
                            submitPhotoComplaintDetails(complaintMaster, userId);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    hideProgressDialog();
                    AndroSocioToast.showErrorToast(requireContext(), "Failed to upload photo", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    public byte[] getDownsizedImageBytes(Bitmap fullBitmap, int scaleWidth, int scaleHeight) throws IOException {
        try {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true);

            // 2. Instantiate the downsized image content as a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void submitPhotoMnIssueDetails(MnIssueMaster mnIssueMaster, String userId) {
        try {
            showProgressDialog("Submitting please wait.");

            mUserReferenceIssue.child(mnIssueMaster.getMnIssueCity()).child(mnIssueMaster.getMnIssueType()).child(userId).child(mnIssueMaster.getMnIssuePlacePhotoId()).setValue(mnIssueMaster)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            AndroSocioToast.showSuccessToast(requireContext(), "Municipal issue submitted successfully.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_LONG);
                            clearAllFields();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            AndroSocioToast.showErrorToast(requireContext(), "Failed to submit municipal issue", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    public void submitPhotoComplaintDetails(ComplaintMaster complaintMaster, String userId) {
        try {
            showProgressDialog("Submitting please wait.");

            mUserReferenceComplaint.child(complaintMaster.getComplaintCity()).child(complaintMaster.getComplaintType()).child(userId).child(complaintMaster.getComplaintPlacePhotoId()).setValue(complaintMaster)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            AndroSocioToast.showSuccessToast(requireContext(), "Police complaint submitted successfully.", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_LONG);
                            clearAllFields();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            AndroSocioToast.showErrorToast(requireContext(), "Failed to submit police complaint", AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
                        }
                    });
        } catch (Exception e) {
            hideProgressDialog();
            AndroSocioToast.showErrorToast(requireContext(), e.getMessage(), AndroSocioToast.ANDRO_SOCIO_TOAST_LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    private void clearAllFields() {
        try {
            textCity.setText("");
            radioGroupIssueOrComplaint.clearCheck();
            radioIssueAccessType.clearCheck();
            photoUploadUri = null;
            cropImageUri = null;
            editIssueOrComplaintTitle.setText("");
            editIssueOrComplaintDesc.setText("");
            imageSelectedPhoto.setImageURI(null);
            imageSelectedPhoto.setImageDrawable(ResourcesCompat.getDrawable(requireContext().getResources(), R.drawable.empty_image, null));

            radioIssueAccessType.setVisibility(View.GONE);
            textIssueAccessTypeHeader.setVisibility(View.GONE);
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

    private String getFileExtension(Uri profilePicUri) {
        try {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            if (mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(profilePicUri)) != null) {
                return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(profilePicUri));
            } else {
                return "jpg";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "jpg";
        }
    }

    private long getFileSize(Uri profilePicUri) {
        long fileSize = 0;
        ContentResolver contentResolver = requireActivity().getContentResolver();
        AssetFileDescriptor afd = null;
        try {
            afd = contentResolver.openAssetFileDescriptor(profilePicUri, "r");
            fileSize = afd.getLength();
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSize;
    }


    private void showProfilePicChooser() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());// , android.R.style.Theme_Translucent
            View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_with_centered_icon_profile_pic_chooser, null);

            ImageView imageCamera = dialogView.findViewById(R.id.image_camera);
            TextView textCamera = dialogView.findViewById(R.id.text_camera);

            ImageView imageGallery = dialogView.findViewById(R.id.image_gallery);
            TextView textGallery = dialogView.findViewById(R.id.text_gallery);

            TextView textCancel = dialogView.findViewById(R.id.text_cancel_button);

            builder.setCancelable(false);
            builder.setView(dialogView);
            final android.app.AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            // Camera Option Clicked
            imageCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    captureImageFromCamera();
                }
            });

            textCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    captureImageFromCamera();
                }
            });

            // GalleryOptionClick
            imageGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    chooseImageFromGallery();
                }
            });

            textGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    chooseImageFromGallery();
                }
            });

            //Cancel Button Click
            textCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureImageFromCamera() {
        try {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                requestStoragePermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            //This is the directory in which the file will be created. This is the default location of Camera photos
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "Camera");
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            cameraFilePath = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void chooseImageFromGallery() {
        try {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // region Permission requests
    private void requestStoragePermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Display a SnackBar with an explanation and a button to trigger the request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Snackbar.make(issueOrComplaintCoordinator, requireActivity().getResources().getString(R.string.permission_mandatory_alert),
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Allow", view -> requestPermissions(AppConstants.PERMISSIONS_STORAGE, AppConstants.PERMISSION_REQUEST_STORAGE))
                            .show();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(AppConstants.PERMISSIONS_STORAGE, AppConstants.PERMISSION_REQUEST_STORAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        try {
                            Uri imageUri = CropImage.getPickImageResultUri(requireContext(), data);

                            // For API >= 23 we need to check specifically that we have permissions to read external storage.
                            if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)) {
                                // request permissions and handle the result in onRequestPermissionsResult()
                                cropImageUri = imageUri;

                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                            } else {
                                // no permissions required or already grunted, can start crop image activity
                                cropImage(imageUri);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        photoUploadUri = result.getUri();
                        try {
                            Bitmap selectedImage = decodeBitmapUri(requireActivity(), photoUploadUri);
                            imageSelectedPhoto.setImageBitmap(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Log.d(TAG, "onActivityResult: Error in cropping");
                    }
                    break;

                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            cropImageUri = selectedImage;
                            CropImage.activity(selectedImage)
                                    .start(requireActivity());
                        } else {
                            Toast.makeText(requireContext(), "Failed to load image from gallery.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    File imgFileCamera = new File(cameraFilePath);
                    if (imgFileCamera.exists()) {
                        cropImageUri = Uri.fromFile(imgFileCamera);
                        CropImage.activity(cropImageUri)
                                .start(requireActivity());
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        try {
            int targetW = 300;
            int targetH = 300;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            return BitmapFactory.decodeStream(ctx.getContentResolver()
                    .openInputStream(uri), null, bmOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            Log.d(TAG, "onRequestPermissionsResult: Request Code: " + requestCode);

            if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.startPickImageActivity(requireActivity());
                } else {
                    Toast.makeText(requireContext(), requireActivity().getResources().getString(R.string.canceling_permission_not_granted), Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
                if (cropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    cropImage(cropImageUri);
                } else {
                    Toast.makeText(requireContext(), requireActivity().getResources().getString(R.string.canceling_permission_not_granted), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cropImage(Uri imageUri) {
        try {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(requireContext(), this);
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

}