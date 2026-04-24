package com.example.do_an_tot_nghiep.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.ServiceReadAll;
import com.example.do_an_tot_nghiep.Container.ServiceReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceRepository{
    private final String TAG = "ServiceRepository";

    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() { return animation; }

    private final SingleLiveEvent<ServiceReadAll> readAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<ServiceReadAll> getReadAllResponse() { return readAllResponse; }

    public void readAll(Map<String, String> headers, Map<String,String> parameters)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<ServiceReadAll> container = api.serviceReadAll(headers, parameters);

        container.enqueue(new Callback<ServiceReadAll>() {
            @Override
            public void onResponse(@NonNull Call<ServiceReadAll> call, @NonNull Response<ServiceReadAll> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readAllResponse.setValue(response.body());
                } else {
                    readAllResponse.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ServiceReadAll> call, @NonNull Throwable t) {
                animation.setValue(false);
                readAllResponse.setValue(null);
            }
        });
    }

    private final SingleLiveEvent<ServiceReadByID> readByIDResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<ServiceReadByID> getReadByIDResponse() { return readByIDResponse; }

    public void readByID(Map<String, String> headers, String serviceId)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<ServiceReadByID> container = api.serviceReadByID(headers, serviceId);

        container.enqueue(new Callback<ServiceReadByID>() {
            @Override
            public void onResponse(@NonNull Call<ServiceReadByID> call, @NonNull Response<ServiceReadByID> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readByIDResponse.setValue(response.body());
                } else {
                    readByIDResponse.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ServiceReadByID> call, @NonNull Throwable t) {
                animation.setValue(false);
                readByIDResponse.setValue(null);
            }
        });
    }
}
