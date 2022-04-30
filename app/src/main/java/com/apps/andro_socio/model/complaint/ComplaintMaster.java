package com.apps.andro_socio.model.complaint;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ComplaintMaster implements Parcelable {
    private String complaintHeader;
    private String complaintDescription;
    private String complaintCreatedBy;
    private String complaintCreatedOn;
    private String complaintType;
    private String complaintAccessType;
    private String complaintCity;
    private String complaintAcceptedOfficerId;
    private String complaintAcceptedOfficerName;
    private String complaintPlacePhotoId;
    private String complaintPlacePhotoPath;
    private String complaintPlacePhotoUploadedDate;
    private double complaintPlaceLatitude;
    private double complaintPlaceLongitude;
    private String complaintPlaceAddress;

    private List<ComplaintSubDetails> complaintsSubDetailsList = new ArrayList<>();

    public ComplaintMaster() {
    }

    protected ComplaintMaster(Parcel in) {
        complaintHeader = in.readString();
        complaintDescription = in.readString();
        complaintCreatedBy = in.readString();
        complaintCreatedOn = in.readString();
        complaintType = in.readString();
        complaintAccessType = in.readString();
        complaintCity = in.readString();
        complaintAcceptedOfficerId = in.readString();
        complaintAcceptedOfficerName = in.readString();
        complaintPlacePhotoId = in.readString();
        complaintPlacePhotoPath = in.readString();
        complaintPlacePhotoUploadedDate = in.readString();
        complaintPlaceLatitude = in.readDouble();
        complaintPlaceLongitude = in.readDouble();
        complaintPlaceAddress = in.readString();
        complaintsSubDetailsList = in.createTypedArrayList(ComplaintSubDetails.CREATOR);
    }

    public static final Creator<ComplaintMaster> CREATOR = new Creator<ComplaintMaster>() {
        @Override
        public ComplaintMaster createFromParcel(Parcel in) {
            return new ComplaintMaster(in);
        }

        @Override
        public ComplaintMaster[] newArray(int size) {
            return new ComplaintMaster[size];
        }
    };

    public String getComplaintHeader() {
        return complaintHeader;
    }

    public void setComplaintHeader(String complaintHeader) {
        this.complaintHeader = complaintHeader;
    }

    public String getComplaintDescription() {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription) {
        this.complaintDescription = complaintDescription;
    }

    public String getComplaintCreatedBy() {
        return complaintCreatedBy;
    }

    public void setComplaintCreatedBy(String complaintCreatedBy) {
        this.complaintCreatedBy = complaintCreatedBy;
    }

    public String getComplaintCreatedOn() {
        return complaintCreatedOn;
    }

    public void setComplaintCreatedOn(String complaintCreatedOn) {
        this.complaintCreatedOn = complaintCreatedOn;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getComplaintAccessType() {
        return complaintAccessType;
    }

    public void setComplaintAccessType(String complaintAccessType) {
        this.complaintAccessType = complaintAccessType;
    }

    public String getComplaintCity() {
        return complaintCity;
    }

    public void setComplaintCity(String complaintCity) {
        this.complaintCity = complaintCity;
    }

    public String getComplaintAcceptedOfficerId() {
        return complaintAcceptedOfficerId;
    }

    public void setComplaintAcceptedOfficerId(String complaintAcceptedOfficerId) {
        this.complaintAcceptedOfficerId = complaintAcceptedOfficerId;
    }

    public String getComplaintAcceptedOfficerName() {
        return complaintAcceptedOfficerName;
    }

    public void setComplaintAcceptedOfficerName(String complaintAcceptedOfficerName) {
        this.complaintAcceptedOfficerName = complaintAcceptedOfficerName;
    }

    public String getComplaintPlacePhotoId() {
        return complaintPlacePhotoId;
    }

    public void setComplaintPlacePhotoId(String complaintPlacePhotoId) {
        this.complaintPlacePhotoId = complaintPlacePhotoId;
    }

    public String getComplaintPlacePhotoPath() {
        return complaintPlacePhotoPath;
    }

    public void setComplaintPlacePhotoPath(String complaintPlacePhotoPath) {
        this.complaintPlacePhotoPath = complaintPlacePhotoPath;
    }

    public String getComplaintPlacePhotoUploadedDate() {
        return complaintPlacePhotoUploadedDate;
    }

    public void setComplaintPlacePhotoUploadedDate(String complaintPlacePhotoUploadedDate) {
        this.complaintPlacePhotoUploadedDate = complaintPlacePhotoUploadedDate;
    }

    public double getComplaintPlaceLatitude() {
        return complaintPlaceLatitude;
    }

    public void setComplaintPlaceLatitude(double complaintPlaceLatitude) {
        this.complaintPlaceLatitude = complaintPlaceLatitude;
    }

    public double getComplaintPlaceLongitude() {
        return complaintPlaceLongitude;
    }

    public void setComplaintPlaceLongitude(double complaintPlaceLongitude) {
        this.complaintPlaceLongitude = complaintPlaceLongitude;
    }

    public String getComplaintPlaceAddress() {
        return complaintPlaceAddress;
    }

    public void setComplaintPlaceAddress(String complaintPlaceAddress) {
        this.complaintPlaceAddress = complaintPlaceAddress;
    }

    public List<ComplaintSubDetails> getComplaintsSubDetailsList() {
        return complaintsSubDetailsList;
    }

    public void setComplaintsSubDetailsList(List<ComplaintSubDetails> complaintsSubDetailsList) {
        this.complaintsSubDetailsList = complaintsSubDetailsList;
    }

    @Override
    public String toString() {
        return "ComplaintMaster{" +
                "complaintHeader='" + complaintHeader + '\'' +
                ", complaintDescription='" + complaintDescription + '\'' +
                ", complaintCreatedBy='" + complaintCreatedBy + '\'' +
                ", complaintCreatedOn='" + complaintCreatedOn + '\'' +
                ", complaintType='" + complaintType + '\'' +
                ", complaintAccessType='" + complaintAccessType + '\'' +
                ", complaintCity='" + complaintCity + '\'' +
                ", complaintAcceptedOfficerId='" + complaintAcceptedOfficerId + '\'' +
                ", complaintAcceptedOfficerName='" + complaintAcceptedOfficerName + '\'' +
                ", complaintPlacePhotoId='" + complaintPlacePhotoId + '\'' +
                ", complaintPlacePhotoPath='" + complaintPlacePhotoPath + '\'' +
                ", complaintPlacePhotoUploadedDate='" + complaintPlacePhotoUploadedDate + '\'' +
                ", complaintPlaceLatitude=" + complaintPlaceLatitude +
                ", complaintPlaceLongitude=" + complaintPlaceLongitude +
                ", complaintPlaceAddress='" + complaintPlaceAddress + '\'' +
                ", complaintsSubDetailsList=" + complaintsSubDetailsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(complaintHeader);
        parcel.writeString(complaintDescription);
        parcel.writeString(complaintCreatedBy);
        parcel.writeString(complaintCreatedOn);
        parcel.writeString(complaintType);
        parcel.writeString(complaintAccessType);
        parcel.writeString(complaintCity);
        parcel.writeString(complaintAcceptedOfficerId);
        parcel.writeString(complaintAcceptedOfficerName);
        parcel.writeString(complaintPlacePhotoId);
        parcel.writeString(complaintPlacePhotoPath);
        parcel.writeString(complaintPlacePhotoUploadedDate);
        parcel.writeDouble(complaintPlaceLatitude);
        parcel.writeDouble(complaintPlaceLongitude);
        parcel.writeString(complaintPlaceAddress);
        parcel.writeTypedList(complaintsSubDetailsList);
    }
}
