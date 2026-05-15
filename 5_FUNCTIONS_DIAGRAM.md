# 5 CHỨC NĂNG CHÍNH - DIAGRAM & FLOW

## 1️⃣ LOGIN FUNCTION

```
┌─────────────────────────────────────┐
│     User Input Screen               │
│  Phone + Password / Google Login    │
└──────────────┬──────────────────────┘
               │
               ↓
      ┌────────────────────┐
      │ LoginViewModel     │
      │ - loginWithPhone() │
      │ - loginWithGoogle()│
      └────────┬───────────┘
               │
               ↓
    ┌──────────────────────┐
    │  HTTPService         │
    │  Retrofit API Call   │
    └────────┬─────────────┘
             │
             ↓
   ┌─────────────────────┐
   │  Backend API        │
   │  /api/login POST    │
   └────────┬────────────┘
            │
            ↓
  ┌──────────────────────┐
  │ Response: Login DTO  │
  │ - token              │
  │ - user_id            │
  │ - user_info          │
  └───────────┬──────────┘
              │
              ↓
   ┌──────────────────────┐
   │ LiveData Updated     │
   │ loginResponse.value  │
   └──────────────────────┘
```

**Test Cases:**
- TC-VM-001: Valid login with phone success
- TC-VM-002: Login failure handling
- TC-VM-003: Fresh ViewModel crash prevention
- TC-VM-004: Stale data on error handling

---

## 2️⃣ HOME DASHBOARD FUNCTION

```
┌─────────────────────────────────┐
│    Home Fragment Loaded         │
│    User logged in successfully  │
└──────────────┬──────────────────┘
               │
        ┌──────┴──────┐
        ↓             ↓
   ┌─────────┐  ┌──────────┐
   │ Doctors │  │Speciality│
   │ Request │  │ Request  │
   └────┬────┘  └────┬─────┘
        │             │
        ↓             ↓
┌─────────────────────────────────┐
│ HomepageViewModel               │
│ ├─ doctorReadAll()              │
│ └─ specialityReadAll()          │
└─────────────┬───────────────────┘
              │
              ↓
┌─────────────────────────────────┐
│ Repository Layer                │
│ ├─ DoctorRepository             │
│ └─ SpecialityRepository         │
└─────────────┬───────────────────┘
              │
              ↓
┌─────────────────────────────────┐
│ Backend API                     │
│ /api/doctors?page=1&limit=10    │
│ /api/specialities               │
└─────────────┬───────────────────┘
              │
              ↓
┌─────────────────────────────────┐
│ Response: DoctorReadAll         │
│ Response: SpecialityReadAll     │
└─────────────┬───────────────────┘
              │
              ↓
┌─────────────────────────────────┐
│ LiveData Updated with Results   │
│ ├─ doctorList                   │
│ └─ specialityList               │
└─────────────┬───────────────────┘
              │
              ↓
┌─────────────────────────────────┐
│ UI Rendered                     │
│ ├─ Doctor RecyclerView          │
│ └─ Speciality RecyclerView      │
└─────────────────────────────────┘
```

**Test Cases:**
- TC-VM-011: Repository interaction success
- TC-VM-012: Fresh ViewModel crash prevention

---

## 3️⃣ APPOINTMENT MANAGEMENT FUNCTION

```
┌──────────────────────────────────┐
│   Appointment Page Loaded        │
│   Display user appointments      │
└────────────┬─────────────────────┘
             │
             ↓
┌──────────────────────────────────┐
│ AppointmentpageViewModel         │
│ ├─ readAll()      [List Appt]    │
│ ├─ readByID()     [Detail Appt]  │
│ └─ readQueue()    [Queue Info]   │
└────────────┬─────────────────────┘
             │
      ┌──────┴────────┐
      ↓               ↓
┌────────────┐  ┌──────────────┐
│Appointment │  │AppointmentQ  │
│Repository  │  │Repository    │
└─────┬──────┘  └────┬─────────┘
      │              │
      ↓              ↓
┌──────────────────────────────────┐
│ Backend API                      │
│ /api/appointments?page=1         │
│ /api/appointments/{id}           │
│ /api/appointment-queue           │
└─────────┬────────────────────────┘
          │
          ↓
┌──────────────────────────────────┐
│ Response Data:                   │
│ ├─ AppointmentReadAll (List)    │
│ ├─ AppointmentReadByID (Detail) │
│ └─ AppointmentQueue (Queue Info)│
└─────────┬────────────────────────┘
          │
          ↓
┌──────────────────────────────────┐
│ UI Display:                      │
│ ├─ Appointment List             │
│ ├─ Selected Appointment Details │
│ └─ Queue Position Info          │
└──────────────────────────────────┘
```

**Test Cases:**
- TC-VM-009: Repository interaction success
- TC-VM-010: Fresh ViewModel crash prevention

---

## 4️⃣ BOOKING MANAGEMENT FUNCTION

```
┌──────────────────────────────────┐
│   Booking Page Loaded            │
│   User wants to book appointment │
└────────────┬─────────────────────┘
             │
    ┌────────┴────────┐
    ↓                 ↓
┌────────┐      ┌──────────┐
│Create  │      │View      │
│Booking │      │Bookings  │
└───┬────┘      └────┬─────┘
    │                │
    ↓                ↓
┌──────────────────────────────────┐
│ BookingpageViewModel             │
│ ├─ create(data)                  │
│ ├─ cancel(bookingId)             │
│ ├─ readAll()                     │
│ └─ readByID(bookingId)           │
└────────┬──────────────────────────┘
         │
    ┌────┴─────────┐
    ↓              ↓
┌──────────┐  ┌────────────────┐
│Booking   │  │BookingPhoto    │
│Repository│  │Repository      │
└────┬─────┘  └───┬────────────┘
     │            │
     │    ┌───────┼──────────┐
     │    ↓       ↓          ↓
     │ ┌──────────────────────────┐
     │ │ File Upload (Photos)     │
     │ │ ├─ upload()              │
     │ │ ├─ readAll()             │
     │ │ └─ delete()              │
     │ └────────┬─────────────────┘
     │          │
     ↓          ↓
┌──────────────────────────────────┐
│ Backend API:                     │
│ POST   /api/bookings             │
│ DELETE /api/bookings/{id}        │
│ GET    /api/bookings?page=1      │
│ GET    /api/bookings/{id}        │
│ POST   /api/booking-photos       │
│ GET    /api/booking-photos       │
│ DELETE /api/booking-photos/{id}  │
└─────────┬────────────────────────┘
          │
          ↓
┌──────────────────────────────────┐
│ Response DTOs:                   │
│ ├─ BookingCreate                 │
│ ├─ BookingCancel                 │
│ ├─ BookingReadAll                │
│ ├─ BookingReadByID               │
│ ├─ BookingPhotoUpload            │
│ ├─ BookingPhotoReadAll           │
│ └─ BookingPhotoDelete            │
└─────────┬────────────────────────┘
          │
          ↓
┌──────────────────────────────────┐
│ UI Updates:                      │
│ ├─ Booking List                  │
│ ├─ Booking Details               │
│ ├─ Upload Photo Success          │
│ └─ Booking Status                │
└──────────────────────────────────┘
```

**Test Cases:**
- TC-VM-007: Service interaction success
- TC-VM-008: Fresh ViewModel crash prevention

---

## 5️⃣ DOCTOR MANAGEMENT FUNCTION

```
┌──────────────────────────────────┐
│   Doctor Page Loaded             │
│   Browse doctors & speciality     │
└────────────┬─────────────────────┘
             │
       ┌─────┴──────┐
       ↓            ↓
  ┌────────┐  ┌──────────┐
  │List    │  │Detail    │
  │Doctors │  │Doctor    │
  └───┬────┘  └────┬─────┘
      │            │
      ↓            ↓
┌──────────────────────────────────┐
│ DoctorpageViewModel              │
│ ├─ readAll()    [List doctors]   │
│ └─ readByID()   [Doctor details] │
└────────────┬─────────────────────┘
             │
             ↓
┌──────────────────────────────────┐
│ DoctorRepository                 │
│ ├─ readAll(headers, params)      │
│ └─ readById(headers, doctorId)   │
└────────────┬─────────────────────┘
             │
             ↓
┌──────────────────────────────────┐
│ Backend API:                     │
│ GET /api/doctors?page=1&limit=10 │
│ GET /api/doctors/{id}            │
└────────────┬─────────────────────┘
             │
             ↓
┌──────────────────────────────────┐
│ Response:                        │
│ DoctorReadAll {                  │
│   List<Doctor> [                 │
│     {                            │
│       id, name, email, phone,    │
│       description, price,        │
│       avatar, role,              │
│       speciality, room           │
│     }, ...                       │
│   ]                              │
│ }                                │
└────────────┬─────────────────────┘
             │
             ↓
┌──────────────────────────────────┐
│ UI Display:                      │
│ ├─ Doctor List (RecyclerView)    │
│ ├─ Doctor Avatar                 │
│ ├─ Doctor Name & Speciality      │
│ ├─ Doctor Price                  │
│ ├─ Doctor Contact Info           │
│ └─ Doctor Details Page           │
└──────────────────────────────────┘
```

**Models Used:**
```
Doctor Model {
  int id
  String name
  String email
  String phone
  String description
  String price
  String role
  String avatar
  String active
  String createAt
  String updateAt
  Speciality speciality
  Room room
}
```

---

## 📊 DATA FLOW SUMMARY

```
All 5 Functions → Same Architecture Pattern:

┌─────────────┐    ┌────────────┐    ┌──────────────┐    ┌──────────┐    ┌─────────┐
│ User Action │→   │ ViewModel  │→   │ Repository   │→   │ Retrofit │→   │ Backend │
└─────────────┘    └────────────┘    └──────────────┘    └──────────┘    └─────────┘
                         ↓                    ↓                  ↓
                   Update LiveData    Call API Endpoint  Handle Response
                         ↓
                   ┌──────────────┐
                   │   UI Update  │
                   │ (RecyclerView│
                   │  MutableData)│
                   └──────────────┘
```

---

## ✅ CRITERIA VERIFICATION

| Criteria | Requirement | Status | Details |
|----------|-------------|--------|---------|
| **5 Functions** | ✓ | ✅ | Login, Home, Appointment, Booking, Doctor |
| **Database** | ≥5 tables | ✅ | 15 entities: User, Doctor, Appointment, Booking, Service + 10 supporting |
| **Testing** | Testable | ✅ | 47+ test cases with JUnit, Mockito, JaCoCo |
| **Architecture** | Clean | ✅ | MVVM + Repository + Retrofit pattern |
| **Coverage** | Tracked | ✅ | JaCoCo HTML reports available |

---

**Ready for submission ✅**

