package com.apps.andro_socio.model.complaint;

import android.os.Parcel;
import android.os.Parcelable;

public class ComplaintSubDetails implements Parcelable {
    private String complaintId;
    private String complaintStatus;
    private String complaintAcceptedId;
    private String complaintAcceptedRole;
    private String modifiedOn;
    private String modifiedBy;

    public ComplaintSubDetails() {
    }

    protected ComplaintSubDetails(Parcel in) {
        complaintId = in.readString();
        complaintStatus = in.readString();
        complaintAcceptedId = in.readString();
        complaintAcceptedRole = in.readString();
        modifiedOn = in.readString();
        modifiedBy = in.readString();
    }

    public static final Creator<ComplaintSubDetails> CREATOR = new Creator<ComplaintSubDetails>() {
        @Override
        public ComplaintSubDetails createFromParcel(Parcel in) {
            return new ComplaintSubDetails(in);
        }

        @Override
        public ComplaintSubDetails[] newArray(int size) {
            return new ComplaintSubDetails[size];
        }
    };

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintAcceptedId() {
        return complaintAcceptedId;
    }

    public void setComplaintAcceptedId(String complaintAcceptedId) {
        this.complaintAcceptedId = complaintAcceptedId;
    }

    public String getComplaintAcceptedRole() {
        return complaintAcceptedRole;
    }

    public void setComplaintAcceptedRole(String complaintAcceptedRole) {
        this.complaintAcceptedRole = complaintAcceptedRole;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "ComplaintSubDetails{" +
                "complaintId='" + complaintId + '\'' +
                ", complaintStatus='" + complaintStatus + '\'' +
                ", complaintAcceptedId='" + complaintAcceptedId + '\'' +
                ", complaintAcceptedRole='" + complaintAcceptedRole + '\'' +
                ", modifiedOn='" + modifiedOn + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(complaintId);
        parcel.writeString(complaintStatus);
        parcel.writeString(complaintAcceptedId);
        parcel.writeString(complaintAcceptedRole);
        parcel.writeString(modifiedOn);
        parcel.writeString(modifiedBy);
    }
}
