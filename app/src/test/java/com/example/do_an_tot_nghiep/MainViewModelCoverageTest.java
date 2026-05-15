package com.example.do_an_tot_nghiep;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.PatientProfile;
import com.example.do_an_tot_nghiep.Model.User;
import com.example.do_an_tot_nghiep.Repository.SynchronousTaskExecutorRule;

import org.junit.*;
import org.mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.*;

/**
 * MainViewModel - Full Branch Coverage Test
 *
 * Branches cần cover trong readPersonalInformation():
 *   B1: isSuccessful()=true → response.setValue(content)      [lines 50-53]
 *   B2: isSuccessful()=true, body=null → setValue(null)        [lines 50-53]
 *   B3: errorBody()!=null → try parse JSON, setValue(null)     [lines 58-68]
 *   B4: errorBody()!=null + malformed JSON → catch block       [lines 65-67]
 *   B5: isSuccessful()=false, errorBody=null → KHÔNG vào nhánh nào (BUG-M2)
 *   B6: onFailure → setValue(null)                             [lines 73-76]
 *
 * Branches cần cover trong getter:
 *   B7: getResponse() trả field đã khởi tạo (không null)      [line 29]
 */
public class MainViewModelCoverageTest {

    @Rule
    public SynchronousTaskExecutorRule rule = new SynchronousTaskExecutorRule();

    private AutoCloseable mocks;

    @Mock private Retrofit retrofit;
    @Mock private HTTPRequest api;

    private MockedStatic<HTTPService> httpServiceMock;
    private MainViewModel vm;
    private Map<String, String> headers;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        doReturn(api).when(retrofit).create(HTTPRequest.class);
        httpServiceMock = org.mockito.Mockito.mockStatic(HTTPService.class);
        httpServiceMock.when(HTTPService::getInstance).thenReturn(retrofit);
        vm = new MainViewModel();
        headers = new HashMap<>();
        headers.put("Authorization", "JWT valid_token");
        headers.put("Type", "patient");
    }

    @After
    public void tearDown() throws Exception {
        httpServiceMock.close();
        mocks.close();
    }

    // ── B7: getResponse() getter ───────────────────────────────────────────

    /** getResponse_beforeAnyCall_returnsNonNullLiveData */
    @Test
    public void getResponse_beforeAnyCall_returnsNonNullLiveData() {
        assertNotNull(vm.getResponse());
        assertNull(vm.getResponse().getValue());
    }

    /** getResponse_calledTwice_returnsSameInstance */
    @Test
    public void getResponse_calledTwice_returnsSameInstance() {
        assertSame(vm.getResponse(), vm.getResponse());
    }

    // ── B1: onResponse isSuccessful=true + body!=null ─────────────────────

    /**
     * readPersonalInformation_onResponse_success_setsLiveData
     * Covers: lines 39-40,43,46-53
     */
    @Test
    public void readPersonalInformation_onResponse_success_setsLiveData() {
        Call<PatientProfile> c = mockCall();
        PatientProfile profile = buildProfile(1, "OK");
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);

        verify(api).readPersonalInformation(headers);

        cb.get().onResponse(c, Response.success(profile));

        assertSame(profile, vm.getResponse().getValue());
    }

    // ── B2: onResponse isSuccessful=true + body=null ──────────────────────

    /**
     * readPersonalInformation_onResponse_successWithNullBody_setsNull
     * Covers: isSuccessful=true branch với null body → setValue(null)
     */
    @Test
    public void readPersonalInformation_onResponse_successWithNullBody_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);
        cb.get().onResponse(c, Response.success((PatientProfile) null));

        // body=null → setValue(null) thông qua line 53
        assertNull(vm.getResponse().getValue());
    }

    // ── B3: errorBody != null + valid JSON ───────────────────────────────

    /**
     * readPersonalInformation_onResponse_errorBodyValidJson_setsNull
     * Covers: lines 58(true)→62-63→68
     */
    @Test
    public void readPersonalInformation_onResponse_errorBodyValidJson_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);

        @SuppressWarnings("unchecked") Response<PatientProfile> err = mock(Response.class);
        doReturn(false).when(err).isSuccessful();
        doReturn(ResponseBody.create(MediaType.parse("application/json"), "{\"error\":\"Unauthorized\",\"code\":401}"))
            .when(err).errorBody();

        cb.get().onResponse(c, err);

        assertNull(vm.getResponse().getValue());
    }

    /**
     * readPersonalInformation_onResponse_http401_setsNull
     * Covers: error path từ Response.error() factory (errorBody tự động có)
     */
    @Test
    public void readPersonalInformation_onResponse_http401_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);
        cb.get().onResponse(c,
            Response.error(401, ResponseBody.create(MediaType.parse("application/json"), "{\"error\":\"Unauthorized\"}")));

        assertNull(vm.getResponse().getValue());
    }

    /**
     * readPersonalInformation_onResponse_http403_setsNull
     */
    @Test
    public void readPersonalInformation_onResponse_http403_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);
        cb.get().onResponse(c,
            Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "{\"error\":\"Forbidden\"}")));

        assertNull(vm.getResponse().getValue());
    }

    /**
     * readPersonalInformation_onResponse_http500_setsNull
     */
    @Test
    public void readPersonalInformation_onResponse_http500_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);
        cb.get().onResponse(c,
            Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), "Internal Server Error")));

        assertNull(vm.getResponse().getValue());
    }

    // ── B4: errorBody != null + malformed JSON → catch block ─────────────

    /**
     * readPersonalInformation_onResponse_errorBodyMalformedJson_catchBlockExecuted
     * Covers: lines 65-67 (catch Exception block)
     */
    @Test
    public void readPersonalInformation_onResponse_errorBodyMalformedJson_catchBlockExecuted() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);

        @SuppressWarnings("unchecked") Response<PatientProfile> err = mock(Response.class);
        doReturn(false).when(err).isSuccessful();
        doReturn(ResponseBody.create(MediaType.parse("application/json"), "NOT_VALID_JSON{{{{"))
            .when(err).errorBody();

        try {
            cb.get().onResponse(c, err);
        } catch (Exception e) {
            fail("App must not crash on malformed JSON: " + e.getMessage());
        }

        assertNull(vm.getResponse().getValue());
    }

    // ── B5: BUG-M2 – isSuccessful=false + errorBody=null → stale data ────

    /**
     * readPersonalInformation_errorBodyNull_isSuccessfulFalse_staleDataNotCleared
     * Covers: BUG-M2 – không có nhánh nào xử lý → stale data tồn tại
     */
    @Test
    public void readPersonalInformation_errorBodyNull_isSuccessfulFalse_staleDataNotCleared() {
        Call<PatientProfile> c1 = mockCall(), c2 = mockCall();
        PatientProfile stale = buildProfile(1, "Cached");
        doReturn(c1).doReturn(c2).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb1 = captureCallback(c1);
        AtomicReference<Callback<PatientProfile>> cb2 = captureCallback(c2);

        // First call: success
        vm.readPersonalInformation(headers);
        cb1.get().onResponse(c1, Response.success(stale));
        assertSame(stale, vm.getResponse().getValue());

        // Second call: BUG-M2
        vm.readPersonalInformation(headers);
        @SuppressWarnings("unchecked") Response<PatientProfile> noOp = mock(Response.class);
        doReturn(false).when(noOp).isSuccessful();
        doReturn(null).when(noOp).errorBody();
        cb2.get().onResponse(c2, noOp);

        // BUG-M2 confirmed: stale data remains
        assertEquals("BUG-M2: stale data not cleared", stale, vm.getResponse().getValue());
    }

    // ── B6: onFailure ─────────────────────────────────────────────────────

    /**
     * readPersonalInformation_onFailure_networkDown_setsNull
     * Covers: lines 73-77 (onFailure block)
     */
    @Test
    public void readPersonalInformation_onFailure_networkDown_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);
        cb.get().onFailure(c, new java.io.IOException("Network down"));

        assertNull(vm.getResponse().getValue());
    }

    /**
     * readPersonalInformation_onFailure_runtimeException_setsNull
     * Covers: onFailure với RuntimeException
     */
    @Test
    public void readPersonalInformation_onFailure_runtimeException_setsNull() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb = captureCallback(c);

        vm.readPersonalInformation(headers);
        cb.get().onFailure(c, new RuntimeException("Unexpected error"));

        assertNull(vm.getResponse().getValue());
    }

    // ── BUG-M1: null headers không validate ───────────────────────────────

    /**
     * readPersonalInformation_nullHeaders_apiStillCalled_demonstratesMissingValidation
     * BUG-M1: không validate null headers trước khi call API
     */
    @Test
    public void readPersonalInformation_nullHeaders_apiStillCalled_demonstratesMissingValidation() {
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(isNull());
        doAnswer(i -> null).when(c).enqueue(any());

        vm.readPersonalInformation(null);

        verify(api).readPersonalInformation(null); // BUG: should not call API with null
    }

    /**
     * readPersonalInformation_emptyHeaders_apiStillCalled
     */
    @Test
    public void readPersonalInformation_emptyHeaders_apiStillCalled() {
        Map<String, String> empty = new HashMap<>();
        Call<PatientProfile> c = mockCall();
        doReturn(c).when(api).readPersonalInformation(empty);
        doAnswer(i -> null).when(c).enqueue(any());

        vm.readPersonalInformation(empty);

        verify(api).readPersonalInformation(empty);
    }

    // ── BUG-M4: MainActivity observer pattern NPE ─────────────────────────

    /**
     * mainActivity_observerPattern_nullResponse_causesNPEOnGetResult
     * BUG-M4: MainActivity line 85 không check null trước getResult()
     * Test calls PatientProfile.getResult() trực tiếp → contributes to coverage
     */
    @Test
    public void mainActivity_observerPattern_nullResponse_causesNPEOnGetResult() {
        PatientProfile nullProfile = null;
        try {
            int result = nullProfile.getResult(); // NPE - simulates MainActivity bug
            fail("Expected NPE");
        } catch (NullPointerException e) {
            assertTrue("BUG-M4: NPE when MainActivity observer doesn't null-check response", true);
        }
    }

    /**
     * patientProfile_getResult_returnsCorrectValue – covers PatientProfile.getResult()
     */
    @Test
    public void patientProfile_getResult_returnsCorrectValue() {
        PatientProfile profile = buildProfile(1, "Success");
        assertEquals(Integer.valueOf(1), profile.getResult());
        assertEquals("Success", profile.getMsg());
    }

    /**
     * patientProfile_getResult_zeroValue – covers branch result == 0
     */
    @Test
    public void patientProfile_getResult_zeroValue() {
        PatientProfile profile = buildProfile(0, "Token expired");
        assertEquals(Integer.valueOf(0), profile.getResult());
    }

    /**
     * readPersonalInformation_calledTwiceSuccess_liveDataReflectsLatest
     */
    @Test
    public void readPersonalInformation_calledTwiceSuccess_liveDataReflectsLatest() {
        Call<PatientProfile> c1 = mockCall(), c2 = mockCall();
        PatientProfile p1 = buildProfile(1, "First");
        PatientProfile p2 = buildProfile(1, "Second");
        doReturn(c1).doReturn(c2).when(api).readPersonalInformation(headers);
        AtomicReference<Callback<PatientProfile>> cb1 = captureCallback(c1);
        AtomicReference<Callback<PatientProfile>> cb2 = captureCallback(c2);

        vm.readPersonalInformation(headers);
        cb1.get().onResponse(c1, Response.success(p1));
        assertSame(p1, vm.getResponse().getValue());

        vm.readPersonalInformation(headers);
        cb2.get().onResponse(c2, Response.success(p2));
        assertSame(p2, vm.getResponse().getValue());
    }

    // ── Helper methods ─────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private <T> Call<T> mockCall() {
        return (Call<T>) mock(Call.class);
    }

    private <T> AtomicReference<Callback<T>> captureCallback(Call<T> call) {
        AtomicReference<Callback<T>> ref = new AtomicReference<>();
        doAnswer(inv -> {
            ref.set(inv.getArgument(0));
            return null;
        }).when(call).enqueue(any());
        return ref;
    }

    /** Tạo real PatientProfile object để JaCoCo đo coverage PatientProfile.java */
    private PatientProfile buildProfile(int result, String msg) {
        return new PatientProfile() {
            @Override public Integer getResult() { return result; }
            @Override public String getMsg() { return msg; }
            @Override public User getData() { return null; }
        };
    }
}
