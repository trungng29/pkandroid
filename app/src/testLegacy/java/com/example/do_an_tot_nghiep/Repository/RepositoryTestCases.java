package com.example.do_an_tot_nghiep.Repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.AppointmentQueue;
import com.example.do_an_tot_nghiep.Container.AppointmentReadAll;
import com.example.do_an_tot_nghiep.Container.AppointmentReadByID;
import com.example.do_an_tot_nghiep.Container.BookingCreate;
import com.example.do_an_tot_nghiep.Container.BookingPhotoReadAll;
import com.example.do_an_tot_nghiep.Container.BookingReadAll;
import com.example.do_an_tot_nghiep.Container.DoctorReadAll;
import com.example.do_an_tot_nghiep.Container.NotificationReadAll;
import com.example.do_an_tot_nghiep.Container.RecordReadByID;
import com.example.do_an_tot_nghiep.Container.ServiceReadByID;
import com.example.do_an_tot_nghiep.Container.SpecialityReadAll;
import com.example.do_an_tot_nghiep.Container.TreatmentReadByID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RepositoryTestCases {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Retrofit retrofit;
    private HTTPRequest api;
    private MockedStatic<HTTPService> httpServiceMock;
    private Map<String, String> headers;
    private Map<String, String> params;

    @Before
    public void setUp() {
        retrofit = mock(Retrofit.class);
        api = mock(HTTPRequest.class);
        when(retrofit.create(HTTPRequest.class)).thenReturn(api);

        httpServiceMock = org.mockito.Mockito.mockStatic(HTTPService.class);
        httpServiceMock.when(HTTPService::getInstance).thenReturn(retrofit);

        headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");
        headers.put("Type", "patient");

        params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "10");
    }

    @After
    public void tearDown() {
        httpServiceMock.close();
    }

    @Test
    public void appointment_readAll_success_updatesDataAndStopsAnimation() {
        AppointmentRepository repository = new AppointmentRepository();
        Call<AppointmentReadAll> call = mock(Call.class);
        AppointmentReadAll body = mock(AppointmentReadAll.class);

        when(api.appointmentReadAll(headers, params)).thenReturn(call);
        AtomicReference<Callback<AppointmentReadAll>> callbackRef = captureCallback(call);

        repository.readAll(headers, params);
        assertEquals(Boolean.TRUE, repository.getAnimation().getValue());

        callbackRef.get().onResponse(call, Response.success(body));

        assertSame(body, repository.getReadAllResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void appointment_readById_error_setsNullAndStopsAnimation() {
        AppointmentRepository repository = new AppointmentRepository();
        Call<AppointmentReadByID> call = mock(Call.class);

        when(api.appointmentReadByID(headers, "A1")).thenReturn(call);
        AtomicReference<Callback<AppointmentReadByID>> callbackRef = captureCallback(call);

        repository.readByID(headers, "A1");
        callbackRef.get().onResponse(call, errorResponse());

        assertEquals(null, repository.getReadByIDResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void booking_create_mapsBodyAndUpdatesEventOnSuccess() {
        BookingRepository repository = new BookingRepository();
        Call<BookingCreate> call = mock(Call.class);
        BookingCreate body = mock(BookingCreate.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("doctorId", "D1");
        requestBody.put("serviceId", "S1");
        requestBody.put("bookingName", "Nguyen Van A");
        requestBody.put("bookingPhone", "0123456789");
        requestBody.put("name", "Patient A");
        requestBody.put("gender", "male");
        requestBody.put("address", "Ha Noi");
        requestBody.put("reason", "Headache");
        requestBody.put("birthday", "2000-01-01");
        requestBody.put("appointmentTime", "08:30");
        requestBody.put("appointmentDate", "2026-04-30");

        when(api.bookingCreate(eq(headers), eq("D1"), eq("S1"), eq("Nguyen Van A"), eq("0123456789"),
                eq("Patient A"), eq("male"), eq("Ha Noi"), eq("Headache"), eq("2000-01-01"),
                eq("08:30"), eq("2026-04-30"))).thenReturn(call);

        AtomicReference<Callback<BookingCreate>> callbackRef = captureCallback(call);

        repository.create(headers, requestBody);
        callbackRef.get().onResponse(call, Response.success(body));

        assertSame(body, repository.getBookingCreate().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void booking_readAll_failure_setsNullAndStopsAnimation() {
        BookingRepository repository = new BookingRepository();
        Call<BookingReadAll> call = mock(Call.class);

        when(api.bookingReadAll(headers, params)).thenReturn(call);
        AtomicReference<Callback<BookingReadAll>> callbackRef = captureCallback(call);

        repository.readAll(headers, params);
        callbackRef.get().onFailure(call, new RuntimeException("network"));

        assertEquals(null, repository.getReadAllResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void bookingPhoto_readAll_error_setsNullAndStopsAnimation() {
        BookingPhotoRepository repository = new BookingPhotoRepository();
        Call<BookingPhotoReadAll> call = mock(Call.class);

        when(api.bookingPhotoReadAll(headers, "B1")).thenReturn(call);
        AtomicReference<Callback<BookingPhotoReadAll>> callbackRef = captureCallback(call);

        repository.readAll(headers, "B1");
        callbackRef.get().onResponse(call, errorResponse());

        assertEquals(null, repository.getReadAllResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void doctor_readAll_success_updatesEventAndStopsAnimation() {
        DoctorRepository repository = new DoctorRepository();
        Call<DoctorReadAll> call = mock(Call.class);
        DoctorReadAll body = mock(DoctorReadAll.class);

        when(api.doctorReadAll(headers, params)).thenReturn(call);
        AtomicReference<Callback<DoctorReadAll>> callbackRef = captureCallback(call);

        repository.readAll(headers, params);
        callbackRef.get().onResponse(call, Response.success(body));

        assertSame(body, repository.getReadAllResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void service_readById_failure_setsNullAndStopsAnimation() {
        ServiceRepository repository = new ServiceRepository();
        Call<ServiceReadByID> call = mock(Call.class);

        when(api.serviceReadByID(headers, "SV1")).thenReturn(call);
        AtomicReference<Callback<ServiceReadByID>> callbackRef = captureCallback(call);

        repository.readByID(headers, "SV1");
        callbackRef.get().onFailure(call, new RuntimeException("timeout"));

        assertEquals(null, repository.getReadByIDResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void speciality_readAll_error_setsNullAndStopsAnimation() {
        SpecialityRepository repository = new SpecialityRepository();
        Call<SpecialityReadAll> call = mock(Call.class);

        when(api.specialityReadAll(headers, params)).thenReturn(call);
        AtomicReference<Callback<SpecialityReadAll>> callbackRef = captureCallback(call);

        repository.readAll(headers, params);
        callbackRef.get().onResponse(call, errorResponse());

        assertEquals(null, repository.getReadAllResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void treatment_readById_success_updatesEventAndStopsAnimation() {
        TreatmentRepository repository = new TreatmentRepository();
        Call<TreatmentReadByID> call = mock(Call.class);
        TreatmentReadByID body = mock(TreatmentReadByID.class);

        when(api.treatmentReadByID(headers, "T1")).thenReturn(call);
        AtomicReference<Callback<TreatmentReadByID>> callbackRef = captureCallback(call);

        repository.readByID(headers, "T1");
        callbackRef.get().onResponse(call, Response.success(body));

        assertSame(body, repository.getReadByIDResponse().getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void appointmentQueue_onFailure_keepsOldDataAndStopsAnimation() {
        AppointmentQueueRepository repository = new AppointmentQueueRepository();
        Call<AppointmentQueue> successCall = mock(Call.class);
        Call<AppointmentQueue> failureCall = mock(Call.class);
        AppointmentQueue firstBody = mock(AppointmentQueue.class);

        when(api.appointmentQueue(headers, params)).thenReturn(successCall, failureCall);
        AtomicReference<Callback<AppointmentQueue>> successCallback = captureCallback(successCall);
        AtomicReference<Callback<AppointmentQueue>> failureCallback = captureCallback(failureCall);

        MutableLiveData<AppointmentQueue> liveData1 = repository.getAppointmentQueue(headers, params);
        successCallback.get().onResponse(successCall, Response.success(firstBody));
        assertSame(firstBody, liveData1.getValue());

        MutableLiveData<AppointmentQueue> liveData2 = repository.getAppointmentQueue(headers, params);
        failureCallback.get().onFailure(failureCall, new RuntimeException("network down"));

        assertSame(liveData1, liveData2);
        assertSame(firstBody, liveData2.getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void notification_onFailure_keepsOldDataAndStopsAnimation() {
        NotificationRepository repository = new NotificationRepository();
        Call<NotificationReadAll> successCall = mock(Call.class);
        Call<NotificationReadAll> failureCall = mock(Call.class);
        NotificationReadAll firstBody = mock(NotificationReadAll.class);

        when(api.notificationReadAll(headers)).thenReturn(successCall, failureCall);
        AtomicReference<Callback<NotificationReadAll>> successCallback = captureCallback(successCall);
        AtomicReference<Callback<NotificationReadAll>> failureCallback = captureCallback(failureCall);

        MutableLiveData<NotificationReadAll> liveData = repository.readAll(headers);
        successCallback.get().onResponse(successCall, Response.success(firstBody));
        assertSame(firstBody, liveData.getValue());

        repository.readAll(headers);
        failureCallback.get().onFailure(failureCall, new RuntimeException("offline"));

        assertSame(firstBody, liveData.getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    @Test
    public void record_onFailure_keepsOldDataAndStopsAnimation() {
        RecordRepository repository = new RecordRepository();
        Call<RecordReadByID> successCall = mock(Call.class);
        Call<RecordReadByID> failureCall = mock(Call.class);
        RecordReadByID firstBody = mock(RecordReadByID.class);

        when(api.recordReadById(headers, "R1")).thenReturn(successCall, failureCall);
        AtomicReference<Callback<RecordReadByID>> successCallback = captureCallback(successCall);
        AtomicReference<Callback<RecordReadByID>> failureCallback = captureCallback(failureCall);

        MutableLiveData<RecordReadByID> liveData = repository.readByID(headers, "R1");
        successCallback.get().onResponse(successCall, Response.success(firstBody));
        assertSame(firstBody, liveData.getValue());

        repository.readByID(headers, "R1");
        failureCallback.get().onFailure(failureCall, new RuntimeException("no internet"));

        assertSame(firstBody, liveData.getValue());
        assertEquals(Boolean.FALSE, repository.getAnimation().getValue());
    }

    private <T> AtomicReference<Callback<T>> captureCallback(Call<T> call) {
        AtomicReference<Callback<T>> callbackRef = new AtomicReference<>();
        doAnswer(invocation -> {
            callbackRef.set(invocation.getArgument(0));
            return null;
        }).when(call).enqueue(any());
        return callbackRef;
    }

    private <T> Response<T> errorResponse() {
        return Response.error(
                400,
                ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{\"message\":\"error\"}"
                )
        );
    }
}


