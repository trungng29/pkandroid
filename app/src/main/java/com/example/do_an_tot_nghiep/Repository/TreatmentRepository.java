package com.example.do_an_tot_nghiep.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.TreatmentReadAll;
import com.example.do_an_tot_nghiep.Container.TreatmentReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TreatmentRepository {

    private final String TAG = "Treatment Repository";
    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() { return animation; }

    private final SingleLiveEvent<TreatmentReadAll> readAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<TreatmentReadAll> getReadAllResponse() { return readAllResponse; }

    public void readAll(Map<String, String> headers, String appointmentId)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<TreatmentReadAll> container = api.treatmentReadAll(headers, appointmentId);

        container.enqueue(new Callback<TreatmentReadAll>() {
            @Override
            public void onResponse(@NonNull Call<TreatmentReadAll> call, @NonNull Response<TreatmentReadAll> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readAllResponse.setValue(response.body());
                } else {
                    readAllResponse.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<TreatmentReadAll> call, @NonNull Throwable t) {
                animation.setValue(false);
                readAllResponse.setValue(null);
            }
        });
    }

    private final SingleLiveEvent<TreatmentReadByID> readByIDResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<TreatmentReadByID> getReadByIDResponse() { return readByIDResponse; }

    public void readByID(Map<String, String> headers, String treatmentId)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<TreatmentReadByID> container = api.treatmentReadByID(headers, treatmentId);

        container.enqueue(new Callback<TreatmentReadByID>() {
            @Override
            public void onResponse(@NonNull Call<TreatmentReadByID> call, @NonNull Response<TreatmentReadByID> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readByIDResponse.setValue(response.body());
                } else {
                    readByIDResponse.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<TreatmentReadByID> call, @NonNull Throwable t) {
                animation.setValue(false);
                readByIDResponse.setValue(null);
            }
        });
    }
}
