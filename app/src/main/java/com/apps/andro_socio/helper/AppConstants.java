package com.apps.andro_socio.helper;

import android.Manifest;

public class AppConstants {

    /**
     * Location permission required for the app
     */
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int PERMISSION_REQUEST_LOCATION = 111;

    public static final String RECEIVER =".RECEIVER";

    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;
    /**
     * Storage permission required for the app
     */
    public static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int PERMISSION_REQUEST_STORAGE = 222;

    public static final String APP_PREFS = "App Prefs";

    // Stored Data
    public static final String USER_LIST = "User List";

    public static final String MN_ISSUE_STATUS_LIST = "Municipal Issue Status List";

    public static final String COMPLAINT_STATUS_LIST = "Complaint Status List";

    // Login Token as mobile Number
    public static final String LOGIN_TOKEN = "Login Token";
    public static final String USER_ROLE = "User Role";

    // Login User Details
    public static final String LOGIN_USER_DETAILS = "Login User Details";

    // Roles
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_USER = "User";
    public static final String ROLE_POLICE = "Police";
    public static final String ROLE_MUNICIPAL_OFFICER = "Municipal Officer";

    // User Type
    public static final String USER_TYPE_GENERAL = "General";
    public static final String USER_TYPE_ANONYMOUS = "Anonymous";

    // Issue/Complaint Status
    public static final String NEW_STATUS = "New";
    public static final String ACCEPTED_STATUS = "Accepted";
    public static final String COMPLETED_STATUS = "Completed";
    public static final String CANCELLED_STATUS = "Cancelled";
    public static final String REJECTED_STATUS = "Rejected";

    // Issue/Complaint Types
    public static final String COMPLAINT_TYPE = "Complaint";
    public static final String MUNICIPAL_ISSUE_TYPE = "Municipal Issue";

    // Issue Access Types
    public static final String ISSUE_ACCESS_TYPE_PRIVATE = "Private";
    public static final String ISSUE_ACCESS_TYPE_PUBLIC = "Public";

    // Gender Types
    public static final String MALE_GENDER = "Male";
    public static final String FEMALE_GENDER = "Female";
    public static final String OTHER_GENDER = "Other";

    // User Active Status
    public static final String ACTIVE_USER = "Active";
    public static final String IN_ACTIVE_USER = "InActive";


    // Generic Setting Options
    public static final String SETTINGS_MY_PROFILE = "My Profile";
    public static final String SETTINGS_UPDATE_MPIN = "Update MPin";

    // View Data Transfer
    public static final String VIEW_MUNICIPAL_ISSUE_DATA = "View Municipal Complaint Data";
    public static final String VIEW_COMPLAINT_DATA = "View Complaint Data";
    public static final String VIEW_COMPLAINT_OR_ISSUE_FLAG = "View Complaint Or Issue Flag";

    // Issue/Complaint Badges
    public static final String COMPLAINTS_BADGE = "Complaints";
    public static final String MUNICIPAL_ISSUES_BADGE = "Issues";

    public static final String COMPLAINT_STATUS = "Complaint Status";
    public static final String MUNICIPAL_ISSUE_STATUS = "Issue Status";

    public static final String RESULT_DATA_KEY = ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = ".LOCATION_DATA_EXTRA";


    public static final String LOCATION_DATA_STREET = ".LOCATION_DATA_STREET";
    public static final String LOCATION_DATA_AREA = ".LOCATION_DATA_AREA";
    public static final String LOCATION_DATA_CITY = ".LOCATION_DATA_CITY";
    public static final String LOCATION_DATA_STATE = ".LOCATION_DATA_STATE";
    public static final String LOCATION_DATA_ZIP_CODE = ".LOCATION_DATA_ZIP_CODE";
    public static final String LOCATION_DATA_COUNTRY = ".LOCATION_DATA_COUNTRY";
    public static final String LOCATION_REQUEST_TYPE = ".LOCATION_REQUEST_TYPE";
    public static final String LOCATION_SOURCE = ".LOCATION_SOURCE";
    public static final String LOCATION_DESTINATION = ".LOCATION_DESTINATION";
    public static final String SOURCE_ADDRESS = ".SOURCE_ADDRESS";
    public static final String LATITUDE = ".LATITUDE";
    public static final String LONGITUDE = ".LONGITUDE";
}
