package com.apps.andro_socio.helper.dataUtils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.apps.andro_socio.helper.AppConstants;
import com.apps.andro_socio.model.issue.MnIssueMaster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    public static List<MnIssueMaster> getMnIssueStatusList(Context context) {
        List<MnIssueMaster> mnIssueStatusList = new ArrayList<>();
        try {
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.MN_ISSUE_STATUS_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<MnIssueMaster>>() {
                }.getType();
                mnIssueStatusList = gson.fromJson(readString, type);
                return mnIssueStatusList;
            } else {
                return mnIssueStatusList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mnIssueStatusList;
        }
    }

  /*  public static List<String> getMnStatusStatusStringList(Context context) {
        List<String> mnIssueStatusStringList = new ArrayList<>();
        List<MnIssueMaster> mnIssueStatusList = new ArrayList<>();
        try {
            // load tasks from preference
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.APP_PREFS, MODE_PRIVATE);
            Gson gson = new Gson();
            String readString = prefs.getString(AppConstants.MN_ISSUE_STATUS_LIST, "");
            if (!TextUtils.isEmpty(readString)) {
                Type type = new TypeToken<ArrayList<MnIssueMaster>>() {
                }.getType();
                mnIssueStatusList = gson.fromJson(readString, type);
                for (MnIssueMaster taskStatus : mnIssueStatusList) {
                    mnIssueStatusStringList.add(taskStatus.getIssue());
                }
                return mnIssueStatusStringList;
            } else {
                return mnIssueStatusStringList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mnIssueStatusStringList;
        }
    }*/
}
