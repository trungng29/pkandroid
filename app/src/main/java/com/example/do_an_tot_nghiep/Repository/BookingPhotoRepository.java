package com.example.do_an_tot_nghiep.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.BookingPhotoReadAll;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingPhotoRepository {

    private final String TAG = "Booking Photo Repository";
    private final MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }


    /************************** READ ALL *******************************/
    private final SingleLiveEvent<BookingPhotoReadAll> readAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<BookingPhotoReadAll> getReadAllResponse() { return readAllResponse; }

    public void readAll (Map<String, String> header, String bookingId)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);
        Call<BookingPhotoReadAll> container = api.bookingPhotoReadAll(header, bookingId);

        container.enqueue(new Callback<BookingPhotoReadAll>() {
            @Override
            public void onResponse(@NonNull Call<BookingPhotoReadAll> call, @NonNull Response<BookingPhotoReadAll> response) {
                animation.setValue(false);
                if(response.isSuccessful()) {
                    readAllResponse.setValue(response.body());
                } else {
                    readAllResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingPhotoReadAll> call, @NonNull Throwable t) {
                animation.setValue(false);
                readAllResponse.setValue(null);
            }
        });
    }
}
