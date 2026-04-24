package com.example.do_an_tot_nghiep.Helper;

import android.app.Application;
import android.util.Log;

import com.example.do_an_tot_nghiep.Model.Option;
import com.example.do_an_tot_nghiep.Model.User;
import com.example.do_an_tot_nghiep.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalVariable extends Application {
    private String accessToken;
    private User AuthUser;
    private final String SHARED_PREFERENCE_KEY = "doantotnghiep";
    private String contentType = "application/x-www-form-urlencoded";

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType );
        headers.put("Authorization", accessToken);
        headers.put("type", "patient");
        
        Log.d("API_DEBUG", "Sending Headers: " + headers.toString());
        return headers;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getAuthUser() {
        return AuthUser;
    }

    public void setAuthUser(User authUser) {
        AuthUser = authUser;
    }

    public String getSharedReferenceKey() {
        return SHARED_PREFERENCE_KEY;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<Option> getFilterOptions()
    {
        List<Option> list = new ArrayList<>();
        Option option1 = new Option();
        option1.setIcon(R.drawable.ic_service);
        option1.setName(getString(R.string.service));

        Option option2 = new Option();
        option2.setIcon(R.drawable.ic_speciality);
        option2.setName(getString(R.string.speciality));

        Option option3 = new Option();
        option3.setIcon(R.drawable.ic_doctor);
        option3.setName(getString(R.string.doctor));

        list.add(option1);
        list.add(option2);
        list.add(option3);
        return list;
    }
}
