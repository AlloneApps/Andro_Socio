package com.apps.andro_socio.model.issue;

import android.os.Parcel;
import android.os.Parcelable;

import com.apps.andro_socio.model.complaint.ComplaintSubDetails;

import java.util.ArrayList;
import java.util.List;

public class MnIssueMaster implements Parcelable {
    private String mnIssueHeader;
    private String mnIssueDescription;
    private String mnIssueType;
    private String mnIssueAccessType;
    private String mnIssueCity;
    private String mnIssueAcceptedOfficerId;
    private String mnIssueAcceptedOfficerName;
    private String mnIssuePlacePhotoId;
    private String mnIssuePlacePhotoPath;
    private String mnIssuePlacePhotoUploadedDate;
    private double mnIssuePlaceLatitude;
    private double mnIssuePlaceLongitude;
    private String mnIssueCreatedBy;
    private String mnIssueCreatedOn;

    private List<MnIssueSubDetails> mnIssueSubDetailsList = new ArrayList<>();

    public MnIssueMaster() {
    }

    protected MnIssueMaster(Parcel in) {
        mnIssueHeader = in.readString();
        mnIssueDescription = in.readString();
        mnIssueType = in.readString();
        mnIssueAccessType = in.readString();
        mnIssueCity = in.readString();
        mnIssueAcceptedOfficerId = in.readString();
        mnIssueAcceptedOfficerName = in.readString();
        mnIssuePlacePhotoId = in.readString();
        mnIssuePlacePhotoPath = in.readString();
        mnIssuePlacePhotoUploadedDate = in.readString();
        mnIssuePlaceLatitude = in.readDouble();
        mnIssuePlaceLongitude = in.readDouble();
        mnIssueCreatedBy = in.readString();
        mnIssueCreatedOn = in.readString();
        mnIssueSubDetailsList = in.createTypedArrayList(MnIssueSubDetails.CREATOR);
    }

    public static final Creator<MnIssueMaster> CREATOR = new Creator<MnIssueMaster>() {
        @Override
        public MnIssueMaster createFromParcel(Parcel in) {
            return new MnIssueMaster(in);
        }

        @Override
        public MnIssueMaster[] newArray(int size) {
            return new MnIssueMaster[size];
        }
    };

    public String getMnIssueHeader() {
        return mnIssueHeader;
    }

    public void setMnIssueHeader(String mnIssueHeader) {
        this.mnIssueHeader = mnIssueHeader;
    }

    public String getMnIssueDescription() {
        return mnIssueDescription;
    }

    public void setMnIssueDescription(String mnIssueDescription) {
        this.mnIssueDescription = mnIssueDescription;
    }

    public String getMnIssueType() {
        return mnIssueType;
    }

    public void setMnIssueType(String mnIssueType) {
        this.mnIssueType = mnIssueType;
    }

    public String getMnIssueAccessType() {
        return mnIssueAccessType;
    }

    public void setMnIssueAccessType(String mnIssueAccessType) {
        this.mnIssueAccessType = mnIssueAccessType;
    }

    public String getMnIssueCity() {
        return mnIssueCity;
    }

    public void setMnIssueCity(String mnIssueCity) {
        this.mnIssueCity = mnIssueCity;
    }

    public String getMnIssueAcceptedOfficerId() {
        return mnIssueAcceptedOfficerId;
    }

    public void setMnIssueAcceptedOfficerId(String mnIssueAcceptedOfficerId) {
        this.mnIssueAcceptedOfficerId = mnIssueAcceptedOfficerId;
    }

    public String getMnIssueAcceptedOfficerName() {
        return mnIssueAcceptedOfficerName;
    }

    public void setMnIssueAcceptedOfficerName(String mnIssueAcceptedOfficerName) {
        this.mnIssueAcceptedOfficerName = mnIssueAcceptedOfficerName;
    }

    public String getMnIssuePlacePhotoId() {
        return mnIssuePlacePhotoId;
    }

    public void setMnIssuePlacePhotoId(String mnIssuePlacePhotoId) {
        this.mnIssuePlacePhotoId = mnIssuePlacePhotoId;
    }

    public String getMnIssuePlacePhotoPath() {
        return mnIssuePlacePhotoPath;
    }

    public void setMnIssuePlacePhotoPath(String mnIssuePlacePhotoPath) {
        this.mnIssuePlacePhotoPath = mnIssuePlacePhotoPath;
    }

    public String getMnIssuePlacePhotoUploadedDate() {
        return mnIssuePlacePhotoUploadedDate;
    }

    public void setMnIssuePlacePhotoUploadedDate(String mnIssuePlacePhotoUploadedDate) {
        this.mnIssuePlacePhotoUploadedDate = mnIssuePlacePhotoUploadedDate;
    }

    public double getMnIssuePlaceLatitude() {
        return mnIssuePlaceLatitude;
    }

    public void setMnIssuePlaceLatitude(double mnIssuePlaceLatitude) {
        this.mnIssuePlaceLatitude = mnIssuePlaceLatitude;
    }

    public double getMnIssuePlaceLongitude() {
        return mnIssuePlaceLongitude;
    }

    public void setMnIssuePlaceLongitude(double mnIssuePlaceLongitude) {
        this.mnIssuePlaceLongitude = mnIssuePlaceLongitude;
    }

    public String getMnIssueCreatedBy() {
        return mnIssueCreatedBy;
    }

    public void setMnIssueCreatedBy(String mnIssueCreatedBy) {
        this.mnIssueCreatedBy = mnIssueCreatedBy;
    }

    public String getMnIssueCreatedOn() {
        return mnIssueCreatedOn;
    }

    public void setMnIssueCreatedOn(String mnIssueCreatedOn) {
        this.mnIssueCreatedOn = mnIssueCreatedOn;
    }

    public List<MnIssueSubDetails> getMnIssueSubDetailsList() {
        return mnIssueSubDetailsList;
    }

    public void setMnIssueSubDetailsList(List<MnIssueSubDetails> mnIssueSubDetailsList) {
        this.mnIssueSubDetailsList = mnIssueSubDetailsList;
    }

    @Override
    public String toString() {
        return "MnIssueMaster{" +
                "mnIssueHeader='" + mnIssueHeader + '\'' +
                ", mnIssueDescription='" + mnIssueDescription + '\'' +
                ", mnIssueType='" + mnIssueType + '\'' +
                ", mnIssueAccessType='" + mnIssueAccessType + '\'' +
                ", mnIssueCity='" + mnIssueCity + '\'' +
                ", mnIssueAcceptedOfficerId='" + mnIssueAcceptedOfficerId + '\'' +
                ", mnIssueAcceptedOfficerName='" + mnIssueAcceptedOfficerName + '\'' +
                ", mnIssuePlacePhotoId='" + mnIssuePlacePhotoId + '\'' +
                ", mnIssuePlacePhotoPath='" + mnIssuePlacePhotoPath + '\'' +
                ", mnIssuePlacePhotoUploadedDate='" + mnIssuePlacePhotoUploadedDate + '\'' +
                ", mnIssuePlaceLatitude=" + mnIssuePlaceLatitude +
                ", mnIssuePlaceLongitude=" + mnIssuePlaceLongitude +
                ", mnIssueCreatedBy='" + mnIssueCreatedBy + '\'' +
                ", mnIssueCreatedOn='" + mnIssueCreatedOn + '\'' +
                ", mnIssueSubDetailsList=" + mnIssueSubDetailsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mnIssueHeader);
        parcel.writeString(mnIssueDescription);
        parcel.writeString(mnIssueType);
        parcel.writeString(mnIssueAccessType);
        parcel.writeString(mnIssueCity);
        parcel.writeString(mnIssueAcceptedOfficerId);
        parcel.writeString(mnIssueAcceptedOfficerName);
        parcel.writeString(mnIssuePlacePhotoId);
        parcel.writeString(mnIssuePlacePhotoPath);
        parcel.writeString(mnIssuePlacePhotoUploadedDate);
        parcel.writeDouble(mnIssuePlaceLatitude);
        parcel.writeDouble(mnIssuePlaceLongitude);
        parcel.writeString(mnIssueCreatedBy);
        parcel.writeString(mnIssueCreatedOn);
        parcel.writeTypedList(mnIssueSubDetailsList);
    }
}
