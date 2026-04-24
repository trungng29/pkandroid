package com.example.do_an_tot_nghiep.Bookingpage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.do_an_tot_nghiep.Configuration.Constant;
import com.example.do_an_tot_nghiep.Configuration.HTTPRequest;
import com.example.do_an_tot_nghiep.Configuration.HTTPService;
import com.example.do_an_tot_nghiep.Container.BookingCreate;
import com.example.do_an_tot_nghiep.Helper.Dialog;
import com.example.do_an_tot_nghiep.Helper.GlobalVariable;
import com.example.do_an_tot_nghiep.Helper.LoadingScreen;
import com.example.do_an_tot_nghiep.Helper.Tooltip;
import com.example.do_an_tot_nghiep.Homepage.HomepageActivity;
import com.example.do_an_tot_nghiep.Model.Doctor;
import com.example.do_an_tot_nghiep.Model.Service;
import com.example.do_an_tot_nghiep.Model.User;
import com.example.do_an_tot_nghiep.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @since 09-12-2022
 * flow: Fragment 1 -> Fragment 3 -> Fragment 2
 */
public class BookingFragment1 extends Fragment {

    private final String TAG = "BookingFragment1";

    private String serviceId;
    private String doctorId;
    private GlobalVariable globalVariable;
    private LoadingScreen loadingScreen;

    private Dialog dialog;

    private ImageView imgServiceAvatar;
    private TextView txtServiceName;

    private Activity activity;
    private Context context;
    private AppCompatButton btnConfirm;

    /*FORM*/
    private EditText txtBookingName;
    private EditText txtBookingPhone;
    private EditText txtPatientName;

    private RadioGroup rdPatientGender;
    private EditText txtPatientBirthday;

    private EditText txtPatientAddress;
    private EditText txtPatientReason;
    private EditText txtAppointmentDate;
    private EditText txtAppointmentTime;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking1, container, false);

        setupComponent(view);
        setupViewModel();
        setupEvent(view);

        return view;
    }

    private void setupComponent(View view)
    {
        activity = requireActivity();
        context = requireContext();

        globalVariable = (GlobalVariable) activity.getApplication();
        loadingScreen = new LoadingScreen(activity);
        dialog = new Dialog(context);
        User user = globalVariable.getAuthUser();

        Bundle bundle = getArguments();
        assert bundle != null;
        serviceId = bundle.getString("serviceId") != null ? bundle.getString("serviceId") : "";
        doctorId = bundle.getString("doctorId") != null ? bundle.getString("doctorId") : "0";

        imgServiceAvatar = view.findViewById(R.id.imgServiceAvatar);
        txtServiceName = view.findViewById(R.id.txtServiceName);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        txtBookingName = view.findViewById(R.id.txtBookingName);
        txtBookingPhone = view.findViewById(R.id.txtBookingPhone);
        txtPatientName = view.findViewById(R.id.txtPatientName);
        rdPatientGender = view.findViewById(R.id.rdPatientGender);
        txtPatientBirthday = view.findViewById(R.id.txtPatientBirthday);
        txtPatientAddress = view.findViewById(R.id.txtPatientAddress);
        txtPatientReason = view.findViewById(R.id.txtPatientReason);
        txtAppointmentDate = view.findViewById(R.id.txtAppointmentDate);
        txtAppointmentTime = view.findViewById(R.id.txtAppointmentTime);

        /* CHỈ CHO PHÉP CHỌN TỪ DIALOG */
        txtPatientBirthday.setFocusable(false);
        txtAppointmentDate.setFocusable(false);
        txtAppointmentTime.setFocusable(false);

        /* KHỞI TẠO GIÁ TRỊ MẶC ĐỊNH */
        txtBookingPhone.setText(user.getPhone());
        txtPatientBirthday.setText(user.getBirthday());
        txtPatientAddress.setText(user.getAddress());
        txtAppointmentDate.setText(Tooltip.getToday());
        txtAppointmentTime.setText(getString(R.string.default_appointment_time));
    }

    private void setupViewModel()
    {
        BookingpageViewModel viewModel = new ViewModelProvider(this).get(BookingpageViewModel.class);
        viewModel.instantiate();

        Map<String, String> header = globalVariable.getHeaders();

        if(!Objects.equals(doctorId, "0")) {
            viewModel.doctorReadByID(header, doctorId);
        } else {
            viewModel.serviceReadById(header, serviceId);
        }

        viewModel.getAnimation().observe((LifecycleOwner) context, aBoolean -> {
            if( aBoolean ) loadingScreen.start();
            else loadingScreen.stop();
        });

        viewModel.getServiceReadByIdResponse().observe((LifecycleOwner) context, response->{
            if (response != null && response.getResult() == 1) {
                printServiceInformation(response.getData());
            }
        });

        viewModel.getDoctorReadByIdResponse().observe((LifecycleOwner) context, response->{
            if (response != null && response.getResult() == 1) {
                printDoctorInformation(response.getData());
            }
        });
    }

    private void printServiceInformation(Service service)
    {
        txtServiceName.setText(service.getName());
        if(service.getImage() != null && service.getImage().length() > 0) {
            Picasso.get().load(Constant.UPLOAD_URI() + service.getImage()).into(imgServiceAvatar);
        }
    }

    private void printDoctorInformation(Doctor doctor)
    {
        String name = getString(R.string.create_booking) + " " + getString(R.string.with) + " " + getString(R.string.doctor) + " " + doctor.getName();
        txtServiceName.setText(name);
        if(doctor.getAvatar() != null && doctor.getAvatar().length() > 0) {
            Picasso.get().load(Constant.UPLOAD_URI() + doctor.getAvatar()).into(imgServiceAvatar);
        }
    }

    private void setupEvent(View view)
    {
        /* LISTENER CHỌN NGÀY SINH */
        DatePickerDialog.OnDateSetListener birthdayListener = (v, year, month, day) -> {
            String monthFormatted = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String dayFormatted = day < 10 ? "0" + day : String.valueOf(day);
            txtPatientBirthday.setText(year + "-" + monthFormatted + "-" + dayFormatted);
        };

        /* LISTENER CHỌN NGÀY HẸN */
        DatePickerDialog.OnDateSetListener appointmentDateListener = (v, year, month, day) -> {
            String monthFormatted = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String dayFormatted = day < 10 ? "0" + day : String.valueOf(day);
            txtAppointmentDate.setText(year + "-" + monthFormatted + "-" + dayFormatted);
        };

        /* LISTENER CHỌN GIỜ HẸN */
        TimePickerDialog.OnTimeSetListener appointmentTimeListener = (v, hour, minute) -> {
            String hourFormatted = hour < 10 ? "0" + hour : String.valueOf(hour);
            String minuteFormatted = minute < 10 ? "0" + minute : String.valueOf(minute);
            txtAppointmentTime.setText(hourFormatted + ":" + minuteFormatted);
        };

        txtPatientBirthday.setOnClickListener(v -> {
            new DatePickerDialog(context, birthdayListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        txtAppointmentDate.setOnClickListener(v -> {
            new DatePickerDialog(context, appointmentDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        txtAppointmentTime.setOnClickListener(v -> {
            new TimePickerDialog(context, appointmentTimeListener, hourToInt(txtAppointmentTime.getText().toString()), minuteToInt(txtAppointmentTime.getText().toString()), true).show();
        });

        btnConfirm.setOnClickListener(v1 -> {
            if (!areMandatoryFieldsFilledUp()) return;

            String bookingName = txtBookingName.getText().toString();
            String bookingPhone = txtBookingPhone.getText().toString();
            String patientName = txtPatientName.getText().toString();

            int selectedId = rdPatientGender.getCheckedRadioButtonId();
            RadioButton radioButton = view.findViewById(selectedId);
            String patientGender = radioButton.getHint().toString();

            Map<String, String> header = globalVariable.getHeaders();
            Map<String, String> body = new HashMap<>();
            body.put("serviceId", serviceId);
            body.put("doctorId", doctorId);
            body.put("bookingName", bookingName);
            body.put("bookingPhone", bookingPhone);
            body.put("name", patientName);
            body.put("gender", patientGender);
            body.put("address", txtPatientAddress.getText().toString());
            body.put("reason", txtPatientReason.getText().toString());
            body.put("birthday", txtPatientBirthday.getText().toString());
            body.put("appointmentTime", txtAppointmentTime.getText().toString());
            body.put("appointmentDate", txtAppointmentDate.getText().toString());

            loadingScreen.start();
            sendBookingCreate(header, body);
        });
    }

    private int hourToInt(String time) {
        try { return Integer.parseInt(time.split(":")[0]); } catch (Exception e) { return 9; }
    }

    private int minuteToInt(String time) {
        try { return Integer.parseInt(time.split(":")[1]); } catch (Exception e) { return 0; }
    }

    private boolean areMandatoryFieldsFilledUp()
    {
        String[] fields = {
            txtBookingName.getText().toString(),
            txtBookingPhone.getText().toString(),
            txtPatientName.getText().toString(),
            txtAppointmentTime.getText().toString(),
            txtAppointmentDate.getText().toString()
        };

        for (String element : fields) {
            if (TextUtils.isEmpty(element)) {
                dialog.announce();
                dialog.show(R.string.attention, context.getString(R.string.you_do_not_fill_mandatory_field_try_again), R.drawable.ic_info);
                dialog.btnOK.setOnClickListener(v -> dialog.close());
                return false;
            }
        }
        return true;
    }

    private void sendBookingCreate(Map<String, String> header, Map<String,String> body)
    {
        Retrofit service = HTTPService.getInstance();
        HTTPRequest api = service.create(HTTPRequest.class);

        Call<BookingCreate> container = api.bookingCreate(header, body.get("doctorId"), body.get("serviceId"),
                body.get("bookingName"), body.get("bookingPhone"), body.get("name"), body.get("gender"),
                body.get("address"), body.get("reason"), body.get("birthday"),
                body.get("appointmentTime"), body.get("appointmentDate"));

        container.enqueue(new Callback<BookingCreate>() {
            @Override
            public void onResponse(@NonNull Call<BookingCreate> call, @NonNull Response<BookingCreate> response) {
                loadingScreen.stop();
                if(response.isSuccessful() && response.body() != null) {
                    processWithPOSTResponse(response.body());
                } else {
                    Toast.makeText(context, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingCreate> call, @NonNull Throwable t) {
                loadingScreen.stop();
                Log.e(TAG, "Create booking error: " + t.getMessage());
            }
        });
    }

    private void processWithPOSTResponse(BookingCreate response)
    {
        if( response.getResult() == 1)
        {
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("bookingId", String.valueOf(response.getData().getId()));
            BookingFragment3 nextFragment = new BookingFragment3();
            nextFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, nextFragment, "bookingFragment3")
                    .addToBackStack("bookingFragment3")
                    .commit();

            HomepageActivity.getInstance().setNumberOnNotificationIcon();
        }
        else
        {
            dialog.announce();
            dialog.show(R.string.attention, response.getMsg(), R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(v -> dialog.close());
        }
    }
}
