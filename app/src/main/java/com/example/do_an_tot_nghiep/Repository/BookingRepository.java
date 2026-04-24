package com.example.do_an_tot_nghiep.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.BookingCreate;
import com.example.do_an_tot_nghiep.Container.BookingReadAll;
import com.example.do_an_tot_nghiep.Container.BookingReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingRepository {

    private final String TAG = "Booking Repository";
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }


    /************************** CREATE*******************************/
    private SingleLiveEvent<BookingCreate> bookingCreate = new SingleLiveEvent<>();

    public SingleLiveEvent<BookingCreate> getBookingCreate() {
        return bookingCreate;
    }
    public void create (Map<String, String> header, Map<String, String> body)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        String doctorId = body.get("doctorId");
        String serviceId = body.get("serviceId");
        String bookingName = body.get("bookingName");
        String bookingPhone = body.get("bookingPhone");
        String name = body.get("name");
        String gender = body.get("gender");
        String address = body.get("address");
        String reason = body.get("reason");
        String birthday = body.get("birthday");
        String appointmentTime = body.get("appointmentTime");
        String appointmentDate = body.get("appointmentDate");

        Call<BookingCreate> container = api.bookingCreate(header,doctorId, serviceId,
                bookingName, bookingPhone, name, gender, address, reason, birthday, appointmentTime, appointmentDate);

        container.enqueue(new Callback<BookingCreate>() {
            @Override
            public void onResponse(@NonNull Call<BookingCreate> call, @NonNull Response<BookingCreate> response) {
                animation.setValue(false);
                if(response.isSuccessful())
                {
                    BookingCreate content = response.body();
                    assert content != null;
                    bookingCreate.setValue(content);
                }
                else
                {
                    bookingCreate.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingCreate> call, @NonNull Throwable t) {
                animation.setValue(false);
                bookingCreate.setValue(null);
            }
        });
    }

    /************************** READ BY ID *******************************/
    private SingleLiveEvent<BookingReadByID> bookingReadByID = new SingleLiveEvent<>();
    public SingleLiveEvent<BookingReadByID> getReadByIDResponse() {
        return bookingReadByID;
    }
    public void readByID(Map<String, String> header, String bookingId)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        Call<BookingReadByID> container = api.bookingReadByID(header, bookingId);

        container.enqueue(new Callback<BookingReadByID>() {
            @Override
            public void onResponse(@NonNull Call<BookingReadByID> call, @NonNull Response<BookingReadByID> response) {
                animation.setValue(false);
                if(response.isSuccessful())
                {
                    BookingReadByID content = response.body();
                    assert content != null;
                    bookingReadByID.setValue(content);
                }
                else
                {
                    bookingReadByID.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingReadByID> call, @NonNull Throwable t) {
                animation.setValue(false);
                bookingReadByID.setValue(null);
            }
        });
    }

    /************************** READ ALL *******************************/
    private SingleLiveEvent<BookingReadAll> bookingReadAll = new SingleLiveEvent<>();
    public SingleLiveEvent<BookingReadAll> getReadAllResponse() {
        return bookingReadAll;
    }
    public void readAll(Map<String, String> header, Map<String, String> parameters)
    {
        animation.setValue(true);
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        Call<BookingReadAll> container = api.bookingReadAll(header, parameters);

        container.enqueue(new Callback<BookingReadAll>() {
            @Override
            public void onResponse(@NonNull Call<BookingReadAll> call, @NonNull Response<BookingReadAll> response) {
                animation.setValue(false);
                if(response.isSuccessful())
                {
                    BookingReadAll content = response.body();
                    assert content != null;
                    bookingReadAll.setValue(content);
                }
                else
                {
                    bookingReadAll.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingReadAll> call, @NonNull Throwable t) {
                animation.setValue(false);
                bookingReadAll.setValue(null);
            }
        });
    }
}
