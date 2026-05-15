# Authentication Test Cases - Quick Index (Part 1)

**Fast Reference for AuthenticationTest.java**

---

## Summary Table - All 35 Test Cases

| TC ID | Type | Description | Expected | Bug |
|-------|------|-------------|----------|-----|
| TC_S1_01 | ✅ Positive | Valid phone login | Token + user | - |
| TC_S1_02 | ✅ Positive | Animation stops | animation=false | - |
| TC_S1_03 | ✅ Positive | Valid Google login | Token + user | - |
| TC_S1_04 | ✅ Positive | Extract user data | User object OK | - |
| TC_S1_05 | ✅ Positive | Extract token | Token preserved | - |
| TC_S1_06 | ✅ Positive | Phone formats | 0xxx & 84xxx | - |
| TC_S1_07 | ✅ Positive | Sequential logins | Latest kept | - |
| TC_S1_16 | ❌ Negative | Invalid credentials | Response=null | Expected |
| TC_S1_17 | ❌ Negative | HTTP 401 error | Response=null | Expected |
| TC_S1_18 | ❌ Negative | Network timeout | null+animation stop | Expected |
| TC_S1_19 | ❌ Negative | Google 400 error | Response=null | Expected |
| TC_S1_20 | ❌ Negative | ErrorBody non-null | Response=null | Expected |
| TC_S1_21 | ⚠️ Bug | Empty phone | No API call | **BUG-HIGH** |
| TC_S1_22 | ⚠️ **CRITICAL** | Null phone | No crash | **BUG-CRITICAL** |
| TC_S1_23 | ⚠️ Bug | Null password | Validate | **BUG-HIGH** |
| TC_S1_24 | ⚠️ Bug | Null email | Validate | **BUG-HIGH** |
| TC_S1_25 | ⚠️ Edge | Success+null body | Response=null | Handled |
| TC_S1_26 | ⚠️ Edge | Null token | token=null | Alert |
| TC_S1_27 | ⚠️ Edge | Null user | user=null | Alert |
| TC_S1_28 | ✅ Positive | Animation on error | animation=false | - |
| TC_S1_29 | ✅ Positive | Animation on error | animation=false | - |
| TC_S1_30 | ⚠️ **CRITICAL** | Stale data bug | Clear previous | **BUG-CRITICAL** |
| TC_S1_31 | ✅ Boundary | Max length phone | Accept | - |
| TC_S1_32 | ✅ Boundary | Special chars pwd | Accept | - |
| TC_S1_33 | ✅ Boundary | Long email | Accept | - |
| TC_S1_34 | ✅ Config | Phone role param | "patient" | - |
| TC_S1_35 | ✅ Config | Google role param | "patient" | - |

---

## Critical Bugs Detected

### 🔴 TC_S1_22: Null Phone Causes Crash

**Problem:**
```java
public void loginWithPhone(String phone, String password) {
    animation.setValue(true);
    // ... NO null check for phone ...
    Call<Login> container = api.login(phone, password, "patient");  // ← Passes null!
    container.enqueue(...);
}
```

**Impact:**
- NullPointerException if `phone == null`
- Crash in production
- App closes unexpectedly

**Fix:**
```java
if (phone == null || phone.isEmpty()) {
    animation.setValue(false);
    loginWithPhoneResponse.setValue(null);
    return;
}
```

---

### 🔴 TC_S1_30: Stale User Data (SECURITY!)

**Problem:**
```
User A logs in → Alice data in cache
User B fails to login (error, no error body)
↓
User B still sees Alice's data on screen!
↓
SECURITY BREACH - Private medical data exposed!
```

**Root Cause:**
```java
@Override
public void onResponse(..., Response<Login> dataResponse) {
    if(dataResponse.isSuccessful()) {
        // Handle success
    }
    if(dataResponse.errorBody() != null) {
        // Handle error
    }
    // ← BUG: If errorBody == null, stale data remains!
}
```

**Fix:**
```java
if(dataResponse.isSuccessful()) {
    // ... success ...
} else {  // ← ADD THIS
    loginWithGoogleResponse.setValue(null);  // Always clear
    animation.setValue(false);
    // ... then handle error details ...
}
```

---

## Test Categories

### Positive Tests (Happy Path) ✅
- TC_S1_01 - Valid phone login
- TC_S1_02 - Animation stops
- TC_S1_03 - Valid Google login
- TC_S1_04 - User extraction
- TC_S1_05 - Token extraction
- TC_S1_06 - Phone formats
- TC_S1_07 - Sequential logins
- TC_S1_28 - Animation on error
- TC_S1_29 - Animation on error

### Negative Tests (Error Handling) ❌
- TC_S1_16 - Invalid credentials
- TC_S1_17 - HTTP 401
- TC_S1_18 - Network timeout
- TC_S1_19 - Google error
- TC_S1_20 - Error body handling

### Bug Detection ⚠️
- TC_S1_21 - Empty phone validation
- TC_S1_22 - **CRITICAL** Null phone
- TC_S1_23 - Null password
- TC_S1_24 - Null email
- TC_S1_30 - **CRITICAL** Stale data

### Edge Cases
- TC_S1_25 - Success + null body
- TC_S1_26 - Null token
- TC_S1_27 - Null user
- TC_S1_31 - Max length phone
- TC_S1_32 - Special chars
- TC_S1_33 - Long email

### Configuration
- TC_S1_34 - Role parameter (phone)
- TC_S1_35 - Role parameter (google)

---

## Running Tests

```bash
# Run all authentication tests
./gradlew testDebugUnitTest --tests AuthenticationTest

# Run specific test
./gradlew testDebugUnitTest --tests \
  AuthenticationTest#givenValidPhoneAndPasswordWhenLoginSuccessThenLiveDataContainsLogin

# Generate coverage report
./gradlew jacocoTestReport
# Report: app/build/reports/jacoco/index.html
```

---

## Recommended Fixes (Priority Order)

### 🔴 CRITICAL (Sprint 1)
1. **TC_S1_22** - Add null check for phone parameter
2. **TC_S1_30** - Fix stale data on error response

### 🟠 HIGH (Sprint 1-2)
1. **TC_S1_21** - Validate empty phone
2. **TC_S1_23** - Validate password input
3. **TC_S1_24** - Validate email input

### 🟡 MEDIUM (Sprint 2-3)
1. **TC_S1_25** - Handle null response body
2. **TC_S1_26** - Validate token presence
3. **TC_S1_27** - Validate user data

---

**Location:** `app/src/test/java/com/example/do_an_tot_nghiep/Loginpage/AuthenticationTest.java`

**Total Test Cases:** 35 (7 Positive + 18 Negative + 10 Boundary)

**Critical Bugs Found:** 2

**Last Updated:** May 9, 2026

