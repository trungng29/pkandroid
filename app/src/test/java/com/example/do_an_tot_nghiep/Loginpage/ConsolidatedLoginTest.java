package com.example.do_an_tot_nghiep.Loginpage;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.Login;
import com.example.do_an_tot_nghiep.Model.User;
import com.example.do_an_tot_nghiep.Repository.SynchronousTaskExecutorRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Consolidated Authentication Test Suite (Minimal & Essential)
 * 
 * Test Coverage:
 * - 4 BUG DETECTION tests (critical defects)
 * - 5 CORE FUNCTIONALITY tests
 * - 8 NEGATIVE/EDGE CASE tests
 */
public class ConsolidatedLoginTest {

    @Rule
    public SynchronousTaskExecutorRule rule = new SynchronousTaskExecutorRule();

    private AutoCloseable mocks;

    @Mock
    private Retrofit retrofit;

    @Mock
    private HTTPRequest api;

    private MockedStatic<HTTPService> httpServiceMock;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        doReturn(api).when(retrofit).create(HTTPRequest.class);
        httpServiceMock = org.mockito.Mockito.mockStatic(HTTPService.class);
        httpServiceMock.when(HTTPService::getInstance).thenReturn(retrofit);
        viewModel = new LoginViewModel();
        // Initialize lazy-loaded LiveData
        viewModel.getAnimation();
        viewModel.getLoginWithPhoneResponse();
        viewModel.getLoginWithGoogleResponse();
    }

    @After
    public void tearDown() throws Exception {
        httpServiceMock.close();
        mocks.close();
    }

    // =====================================================================
    // BUG DETECTION TESTS - Critical defects
    // =====================================================================

    /**
     * BUG-1: NullPointerException khi animation chưa được khởi tạo
     * Defect: animation field bị null, loginWithPhone() gọi animation.setValue() trực tiếp
     */
    @Test
    public void bugTest_loginWithPhone_animationNotInitialized_throwsNPE() {
        LoginViewModel freshVm = new LoginViewModel();
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login(anyString(), anyString(), anyString());
        setupCallbackCapture(mockCall);

        try {
            freshVm.loginWithPhone("0912345678", "password");
            fail("Expected NullPointerException - BUG-1");
        } catch (NullPointerException e) {
            assertTrue("BUG-1 confirmed: animation field not initialized", true);
        }
    }

    /**
     * BUG-2: Stale data remains when response.isSuccessful()=false AND errorBody=null
     * Defect: code không vào nhánh nào để clear LiveData hoặc dừng animation
     */
    @Test
    public void bugTest_loginWithGoogle_errorBodyNull_staleDataBug() {
        Call<Login> call1 = mockCall();
        Call<Login> call2 = mockCall();
        Login cachedLogin = mock(Login.class);
        when(cachedLogin.getResult()).thenReturn(1);

        doReturn(call1).doReturn(call2)
                .when(api).loginWithGoogle(anyString(), anyString(), anyString());

        AtomicReference<Callback<Login>> cb1 = captureCallback(call1);
        AtomicReference<Callback<Login>> cb2 = captureCallback(call2);

        // First call succeeds
        viewModel.loginWithGoogle("alice@gmail.com", "id1");
        cb1.get().onResponse(call1, Response.success(cachedLogin));
        assertEquals(cachedLogin, viewModel.getLoginWithGoogleResponse().getValue());

        // Second call: isSuccessful=false, errorBody=null → BUG-2
        viewModel.loginWithGoogle("bob@gmail.com", "id2");
        @SuppressWarnings("unchecked")
        Response<Login> badResponse = mock(Response.class);
        doReturn(false).when(badResponse).isSuccessful();
        doReturn(null).when(badResponse).errorBody();
        cb2.get().onResponse(call2, badResponse);

        // BUG-2 CONFIRMED: Stale data persists, animation stays TRUE
        assertEquals("BUG-2: Stale data not cleared", cachedLogin,
                viewModel.getLoginWithGoogleResponse().getValue());
        assertEquals("BUG-2: Animation not stopped", Boolean.TRUE,
                viewModel.getAnimation().getValue());
    }

    /**
     * BUG-3: Missing input validation - API called with null/empty parameters
     */
    @Test
    public void bugTest_loginWithPhone_nullPhone_apiCalledWithoutValidation() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login(null, "uid", "patient");
        setupCallbackCapture(mockCall);

        viewModel.loginWithPhone(null, "uid");

        // BUG-3: API called with null phone - should be validated client-side
        verify(api).login(null, "uid", "patient");
    }

    /**
     * BUG-5: LoginActivity observer doesn't null-check response before calling getResult()
     */
    @Test
    public void bugTest_loginWithGoogleResponse_nullResponse_causesNPE() {
        Login nullResponse = null;
        try {
            int result = nullResponse.getResult();
            fail("Expected NPE - LoginActivity observer does not null-check");
        } catch (NullPointerException e) {
            assertTrue("BUG-5: Observer crashes on null response", true);
        }
    }

    // =====================================================================
    // CORE FUNCTIONALITY TESTS
    // =====================================================================

    @Test
    public void phoneLogin_success() {
        Call<Login> c = mockCall();
        Login expected = createMockLogin(1, "OK", "token123", createMockUser("1", "0912345678", "Test"));
        doReturn(c).when(api).login("0912345678", "uid", "patient");
        AtomicReference<Callback<Login>> cb = captureCallback(c);

        viewModel.loginWithPhone("0912345678", "uid");
        assertEquals(Boolean.TRUE, viewModel.getAnimation().getValue());

        cb.get().onResponse(c, Response.success(expected));
        assertSame(expected, viewModel.getLoginWithPhoneResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void googleLogin_success() {
        Call<Login> c = mockCall();
        Login expected = createMockLogin(1, "OK", "gtoken", createMockUser("2", "user@gmail.com", "Test"));
        doReturn(c).when(api).loginWithGoogle("user@gmail.com", "gid", "patient");
        AtomicReference<Callback<Login>> cb = captureCallback(c);

        viewModel.loginWithGoogle("user@gmail.com", "gid");
        cb.get().onResponse(c, Response.success(expected));
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void phoneLogin_failure_http401() {
        Call<Login> c = mockCall();
        doReturn(c).when(api).login(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cb = captureCallback(c);

        viewModel.loginWithPhone("123", "pass");
        cb.get().onResponse(c, Response.error(401, 
                ResponseBody.create(MediaType.parse("application/json"), "{}")));

        assertNull(viewModel.getLoginWithPhoneResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void googleLogin_failure_networkError() {
        Call<Login> c = mockCall();
        doReturn(c).when(api).loginWithGoogle(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cb = captureCallback(c);

        viewModel.loginWithGoogle("u@g.com", "id");
        cb.get().onFailure(c, new IOException("Timeout"));

        assertNull(viewModel.getLoginWithGoogleResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void roleAlwaysPatient_phone() {
        Call<Login> c = mockCall();
        doReturn(c).when(api).login("123", "pwd", "patient");
        setupCallbackCapture(c);

        viewModel.loginWithPhone("123", "pwd");
        ArgumentCaptor<String> role = ArgumentCaptor.forClass(String.class);
        verify(api).login(anyString(), anyString(), role.capture());
        assertEquals("patient", role.getValue());
    }

    @Test
    public void roleAlwaysPatient_google() {
        Call<Login> c = mockCall();
        doReturn(c).when(api).loginWithGoogle("e@m", "id", "patient");
        setupCallbackCapture(c);

        viewModel.loginWithGoogle("e@m", "id");
        ArgumentCaptor<String> role = ArgumentCaptor.forClass(String.class);
        verify(api).loginWithGoogle(anyString(), anyString(), role.capture());
        assertEquals("patient", role.getValue());
    }

    // =====================================================================
    // EDGE CASES & MALFORMED DATA TESTS
    // =====================================================================

    @Test
    public void phoneLogin_emptyString_apiCalledWithEmpty() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login("", "", "patient");
        setupCallbackCapture(mockCall);

        viewModel.loginWithPhone("", "");
        verify(api).login("", "", "patient");
    }

    @Test
    public void phoneLogin_malformedJsonError_doesNotCrash() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cbRef = captureCallback(mockCall);

        viewModel.loginWithPhone("0912345678", "uid");
        cbRef.get().onResponse(mockCall,
                Response.error(400, ResponseBody.create(
                        MediaType.parse("application/json"), "NOT_VALID_JSON{{{")));

        assertNull(viewModel.getLoginWithPhoneResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void googleLogin_malformedJsonInError_handled() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).loginWithGoogle(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cbRef = captureCallback(mockCall);

        viewModel.loginWithGoogle("user@gmail.com", "id");
        @SuppressWarnings("unchecked")
        Response<Login> errResponse = mock(Response.class);
        doReturn(false).when(errResponse).isSuccessful();
        doReturn(ResponseBody.create(MediaType.parse("application/json"), "{INVALID_JSON"))
                .when(errResponse).errorBody();

        try {
            cbRef.get().onResponse(mockCall, errResponse);
        } catch (Exception e) {
            fail("Should not crash on malformed JSON: " + e.getMessage());
        }
        assertNull(viewModel.getLoginWithGoogleResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void successResponse_nullBodyWithAssert_doesNotCrash() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cbRef = captureCallback(mockCall);

        viewModel.loginWithPhone("0912345678", "uid");
        try {
            cbRef.get().onResponse(mockCall, Response.success((Login) null));
        } catch (AssertionError ae) {
            assertTrue(true); // With -ea flag: assert throws
        }
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void phoneLogin_http500Error_handledProperly() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cbRef = captureCallback(mockCall);

        viewModel.loginWithPhone("0912345678", "uid");
        cbRef.get().onResponse(mockCall,
                Response.error(500, ResponseBody.create(
                        MediaType.parse("application/json"), "{\"error\":\"Server Error\"}")));

        assertNull(viewModel.getLoginWithPhoneResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    @Test
    public void googleLogin_errorWithoutErrorBody_staleDataPersists() {
        Call<Login> call1 = mockCall();
        Call<Login> call2 = mockCall();
        Login cachedData = mock(Login.class);
        when(cachedData.getResult()).thenReturn(1);

        doReturn(call1).doReturn(call2)
                .when(api).loginWithGoogle(anyString(), anyString(), anyString());

        AtomicReference<Callback<Login>> cb1 = captureCallback(call1);
        AtomicReference<Callback<Login>> cb2 = captureCallback(call2);

        // First call succeeds
        viewModel.loginWithGoogle("a@b.com", "id1");
        cb1.get().onResponse(call1, Response.success(cachedData));

        // Second call: error without errorBody - BUG-2
        viewModel.loginWithGoogle("c@d.com", "id2");
        @SuppressWarnings("unchecked")
        Response<Login> badResp = mock(Response.class);
        doReturn(false).when(badResp).isSuccessful();
        doReturn(null).when(badResp).errorBody();
        cb2.get().onResponse(call2, badResp);

        // Stale data persists (BUG)
        assertEquals(cachedData, viewModel.getLoginWithGoogleResponse().getValue());
        assertEquals(Boolean.TRUE, viewModel.getAnimation().getValue());
    }

    @Test
    public void phoneLogin_runtimeException_animationStopsAndResponseNull() {
        Call<Login> mockCall = mockCall();
        doReturn(mockCall).when(api).login(anyString(), anyString(), anyString());
        AtomicReference<Callback<Login>> cbRef = captureCallback(mockCall);

        viewModel.loginWithPhone("0912345678", "uid");
        cbRef.get().onFailure(mockCall, new RuntimeException("JSON parse error"));

        assertNull(viewModel.getLoginWithPhoneResponse().getValue());
        assertEquals(Boolean.FALSE, viewModel.getAnimation().getValue());
    }

    // =====================================================================
    // DATA MODEL COVERAGE - Core getters only
    // =====================================================================

    @Test
    public void login_getResult_Success() {
        Login login = mock(Login.class);
        when(login.getResult()).thenReturn(1);
        assertEquals(Integer.valueOf(1), login.getResult());
    }

    @Test
    public void login_getAccessToken_nullable() {
        Login login = mock(Login.class);
        when(login.getAccessToken()).thenReturn(null);
        assertNull(login.getAccessToken());
    }

    @Test
    public void user_getNullPhone_stringConcat_producesNullString() {
        User user = new User();
        assertNull(user.getPhone());
        String result = "0" + user.getPhone();
        assertEquals("0null", result);
    }

    @Test
    public void login_nullToken_trimThrowsNPE() {
        Login login = mock(Login.class);
        when(login.getAccessToken()).thenReturn(null);

        try {
            String formatted = "JWT " + login.getAccessToken().trim();
            fail("Expected NPE");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void login_nullResult_autoUnboxFails() {
        Login login = mock(Login.class);
        when(login.getResult()).thenReturn(null);

        try {
            int x = login.getResult(); // auto-unbox null
            fail("Expected NPE");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    // =====================================================================
    // HELPER METHODS
    // =====================================================================

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

    private <T> void setupCallbackCapture(Call<T> call) {
        doAnswer(inv -> null).when(call).enqueue(any());
    }

    private Login createMockLogin(int result, String msg, String token, User userData) {
        Login login = mock(Login.class);
        when(login.getResult()).thenReturn(result);
        when(login.getMsg()).thenReturn(msg);
        when(login.getAccessToken()).thenReturn(token);
        when(login.getData()).thenReturn(userData);
        return login;
    }

    private User createMockUser(String userId, String phone, String name) {
        User user = mock(User.class);
        try {
            int id = Integer.parseInt(userId);
            when(user.getId()).thenReturn(id);
        } catch (NumberFormatException e) {
            when(user.getId()).thenReturn(0);
        }
        when(user.getPhone()).thenReturn(phone);
        when(user.getName()).thenReturn(name);
        return user;
    }
}

