package com.apps.andro_socio.helper.dataUtils;

import com.apps.andro_socio.helper.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {

    private static final String TAG = "DataUtils";

    public static List<String> getComplaintOrIssueTypeList() {
        List<String> taskTypeList = new ArrayList<>();
        taskTypeList.add(AppConstants.COMPLAINT_TYPE);
        taskTypeList.add(AppConstants.MUNICIPAL_ISSUE_TYPE);
        return taskTypeList;
    }

    public static List<String> getGenderType() {
        List<String> genderType = new ArrayList<>();
        genderType.add(AppConstants.MALE_GENDER);
        genderType.add(AppConstants.FEMALE_GENDER);
        genderType.add(AppConstants.OTHER_GENDER);
        return genderType;
    }

    public static List<String> getNextStatusBasedOnRole(String currentStatus, String role) {
        List<String> nextStatusList = new ArrayList<>();
        switch (currentStatus) {
            case AppConstants.NEW_STATUS: {
                if (role.equalsIgnoreCase(AppConstants.ROLE_USER)) {
                    nextStatusList.add(AppConstants.CANCELLED_STATUS);
                } else {
                    nextStatusList.add(AppConstants.ACCEPTED_STATUS);
                    nextStatusList.add(AppConstants.REJECTED_STATUS);
                }
                break;
            }

            case AppConstants.ACCEPTED_STATUS: {
                if (role.equalsIgnoreCase(AppConstants.ROLE_USER)) {
                    nextStatusList.add(AppConstants.CANCELLED_STATUS);
                } else {
                    nextStatusList.add(AppConstants.COMPLETED_STATUS);
                    nextStatusList.add(AppConstants.REJECTED_STATUS);
                }
                break;
            }
            case AppConstants.CANCELLED_STATUS: {
                if (role.equalsIgnoreCase(AppConstants.ROLE_USER)) {
                    nextStatusList.add(AppConstants.NEW_STATUS);
                }
                break;
            }
            case AppConstants.REJECTED_STATUS: {
                if (!(role.equalsIgnoreCase(AppConstants.ROLE_USER))) {
                    nextStatusList.add(AppConstants.ACCEPTED_STATUS);
                }
                break;
            }
        }

        return nextStatusList;
    }
}
