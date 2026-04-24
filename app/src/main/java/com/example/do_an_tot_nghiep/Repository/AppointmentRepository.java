package com.example.do_an_tot_nghiep.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.AppointmentReadAll;
import com.example.do_an_tot_nghiep.Container.AppointmentReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppointmentRepository {

    private final String TAG = "Appointment Repository";
    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    /************************** READ ALL *******************************/
    // FIX 1: Dùng SingleLiveEvent để tránh sticky data phát lại lỗi khi quay lại màn hình
    private final SingleLiveEvent<AppointmentReadAll> readAllResponse = new SingleLiveEvent<>();

    public SingleLiveEvent<AppointmentReadAll> getReadAllResponse() {
        return readAllResponse;
    }

    public void readAll(Map<String, String> header, Map<String, String> parameters)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        // FIX 2: Đảm bảo header (chứa Authorization Token) được truyền vào call
        Call<AppointmentReadAll> container = api.appointmentReadAll(header, parameters);

        container.enqueue(new Callback<AppointmentReadAll>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentReadAll> call, @NonNull Response<AppointmentReadAll> response) {
                animation.setValue(false);
                if(response.isSuccessful())
                {
                    AppointmentReadAll content = response.body();
                    assert content != null;
                    readAllResponse.setValue(content);
                }
                else
                {
                    try
                    {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            System.out.println(TAG + " - Error: " + jObjError);
                        }
                    }
                    catch (Exception e) {
                        System.out.println(TAG + " - Exception: " + e.getMessage());
                    }
                    readAllResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentReadAll> call, @NonNull Throwable t) {
                animation.setValue(false);
                System.out.println("Appointment Repository - Read All - error: " + t.getMessage());
                readAllResponse.setValue(null);
            }
        });
    }

    /************************** READ BY ID *******************************/
    private final SingleLiveEvent<AppointmentReadByID> readByIDResponse = new SingleLiveEvent<>();
    
    public SingleLiveEvent<AppointmentReadByID> getReadByIDResponse() {
        return readByIDResponse;
    }

    public void readByID(Map<String, String> header, String appointmentID)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        Call<AppointmentReadByID> container = api.appointmentReadByID(header, appointmentID);

        container.enqueue(new Callback<AppointmentReadByID>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentReadByID> call, @NonNull Response<AppointmentReadByID> response) {
                animation.setValue(false);
                if(response.isSuccessful())
                {
                    AppointmentReadByID content = response.body();
                    assert content != null;
                    readByIDResponse.setValue(content);
                }
                else
                {
                    readByIDResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentReadByID> call, @NonNull Throwable t) {
                animation.setValue(false);
                System.out.println("Appointment Repository - Read By ID - error: " + t.getMessage());
                readByIDResponse.setValue(null);
            }
        });
    }
}
