package com.apps.andro_socio.model.issue;

import android.os.Parcel;
import android.os.Parcelable;

public class MnIssueSubDetails implements Parcelable {
    private String mnIssueId;
    private String mnIssueStatus;
    private String mnIssueAcceptedId;
    private String mnIssueAcceptedRole;
    private String mnIssueModifiedOn;
    private String mnIssueModifiedBy;

    public MnIssueSubDetails() {
    }

    protected MnIssueSubDetails(Parcel in) {
        mnIssueId = in.readString();
        mnIssueStatus = in.readString();
        mnIssueAcceptedId = in.readString();
        mnIssueAcceptedRole = in.readString();
        mnIssueModifiedOn = in.readString();
        mnIssueModifiedBy = in.readString();
    }

    public static final Creator<MnIssueSubDetails> CREATOR = new Creator<MnIssueSubDetails>() {
        @Override
        public MnIssueSubDetails createFromParcel(Parcel in) {
            return new MnIssueSubDetails(in);
        }

        @Override
        public MnIssueSubDetails[] newArray(int size) {
            return new MnIssueSubDetails[size];
        }
    };

    public String getMnIssueId() {
        return mnIssueId;
    }

    public void setMnIssueId(String mnIssueId) {
        this.mnIssueId = mnIssueId;
    }

    public String getMnIssueStatus() {
        return mnIssueStatus;
    }

    public void setMnIssueStatus(String mnIssueStatus) {
        this.mnIssueStatus = mnIssueStatus;
    }

    public String getMnIssueAcceptedId() {
        return mnIssueAcceptedId;
    }

    public void setMnIssueAcceptedId(String mnIssueAcceptedId) {
        this.mnIssueAcceptedId = mnIssueAcceptedId;
    }

    public String getMnIssueAcceptedRole() {
        return mnIssueAcceptedRole;
    }

    public void setMnIssueAcceptedRole(String mnIssueAcceptedRole) {
        this.mnIssueAcceptedRole = mnIssueAcceptedRole;
    }

    public String getMnIssueModifiedOn() {
        return mnIssueModifiedOn;
    }

    public void setMnIssueModifiedOn(String mnIssueModifiedOn) {
        this.mnIssueModifiedOn = mnIssueModifiedOn;
    }

    public String getMnIssueModifiedBy() {
        return mnIssueModifiedBy;
    }

    public void setMnIssueModifiedBy(String mnIssueModifiedBy) {
        this.mnIssueModifiedBy = mnIssueModifiedBy;
    }

    @Override
    public String toString() {
        return "MnIssueSubDetails{" +
                "mnIssueId='" + mnIssueId + '\'' +
                ", mnIssueStatus='" + mnIssueStatus + '\'' +
                ", mnIssueAcceptedId='" + mnIssueAcceptedId + '\'' +
                ", mnIssueAcceptedRole='" + mnIssueAcceptedRole + '\'' +
                ", mnIssueModifiedOn='" + mnIssueModifiedOn + '\'' +
                ", mnIssueModifiedBy='" + mnIssueModifiedBy + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mnIssueId);
        parcel.writeString(mnIssueStatus);
        parcel.writeString(mnIssueAcceptedId);
        parcel.writeString(mnIssueAcceptedRole);
        parcel.writeString(mnIssueModifiedOn);
        parcel.writeString(mnIssueModifiedBy);
    }
}
