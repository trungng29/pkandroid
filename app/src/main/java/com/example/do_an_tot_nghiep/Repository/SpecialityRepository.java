package com.example.do_an_tot_nghiep.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.SpecialityReadAll;
import com.example.do_an_tot_nghiep.Container.SpecialityReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SpecialityRepository {

    private final String TAG = "SpecialityRepository";
    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() { return animation; }

    private final SingleLiveEvent<SpecialityReadAll> readAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<SpecialityReadAll> getReadAllResponse() { return readAllResponse; }

    public void readAll(Map<String, String> headers, Map<String,String> parameters)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<SpecialityReadAll> container = api.specialityReadAll(headers, parameters);

        container.enqueue(new Callback<SpecialityReadAll>() {
            @Override
            public void onResponse(@NonNull Call<SpecialityReadAll> call, @NonNull Response<SpecialityReadAll> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readAllResponse.setValue(response.body());
                } else {
                    readAllResponse.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<SpecialityReadAll> call, @NonNull Throwable t) {
                animation.setValue(false);
                readAllResponse.setValue(null);
            }
        });
    }

    private final SingleLiveEvent<SpecialityReadByID> readByIDResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<SpecialityReadByID> getReadByIdResponse() { return readByIDResponse; }

    public void readById(Map<String, String> headers, String specialityId)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<SpecialityReadByID> container = api.specialityReadByID(headers, specialityId);

        container.enqueue(new Callback<SpecialityReadByID>() {
            @Override
            public void onResponse(@NonNull Call<SpecialityReadByID> call, @NonNull Response<SpecialityReadByID> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readByIDResponse.setValue(response.body());
                } else {
                    readByIDResponse.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<SpecialityReadByID> call, @NonNull Throwable t) {
                animation.setValue(false);
                readByIDResponse.setValue(null);
            }
        });
    }
}
