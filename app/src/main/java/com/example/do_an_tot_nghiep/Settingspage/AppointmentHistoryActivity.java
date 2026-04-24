package com.example.do_an_tot_nghiep.Settingspage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.example.do_an_tot_nghiep.Helper.Dialog;
import com.example.do_an_tot_nghiep.Helper.GlobalVariable;
import com.example.do_an_tot_nghiep.Helper.LoadingScreen;
import com.example.do_an_tot_nghiep.Helper.Tooltip;
import com.example.do_an_tot_nghiep.Model.Appointment;
import com.example.do_an_tot_nghiep.R;
import com.example.do_an_tot_nghiep.RecyclerView.Appointment2RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentHistoryActivity extends AppCompatActivity {

    private final String TAG = "AppointmentHistory";
    private ImageButton btnBack;
    private RecyclerView appointmentRecyclerView;
    private GlobalVariable globalVariable;
    private Dialog dialog;
    private LoadingScreen loadingScreen;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    private void setupComponent() {
        globalVariable = (GlobalVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globalVariable.getSharedReferenceKey(), MODE_PRIVATE);

        btnBack = findViewById(R.id.btnBack);
        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView);

        dialog = new Dialog(this);
        loadingScreen = new LoadingScreen(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupViewModel() {
        SettingspageViewModel viewModel = new ViewModelProvider(this).get(SettingspageViewModel.class);
        viewModel.instantiate();

        /* Lấy header mới nhất từ GlobalVariable (chứa JWT Token) */
        Map<String, String> header = globalVariable.getHeaders();
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("order[dir]", "desc");
        parameters.put("order[column]", "date");

        viewModel.readAll(header, parameters);
        viewModel.getReadAllResponse().observe(this, response -> {
            if (isFinishing() || response == null) return;
            
            try {
                int result = response.getResult();
                if (result == 1) {
                    List<Appointment> list = response.getData();
                    setupRecyclerView(list);
                } else {
                    // Hiển thị lỗi thật từ server thay vì báo lỗi internet
                    Log.e(TAG, "Server Error: " + response.getMsg());
                    dialog.show(getString(R.string.attention), response.getMsg(), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(v -> dialog.close());
                }
            } catch (Exception ex) {
                Log.e(TAG, "Exception: " + ex.getMessage());
            }
        });

        viewModel.getAnimation().observe(this, aBoolean -> {
            if (aBoolean) loadingScreen.start();
            else loadingScreen.stop();
        });
    }

    private void setupEvent() {
        btnBack.setOnClickListener(view -> finish());
    }

    private void setupRecyclerView(List<Appointment> list) {
        Appointment2RecyclerView appointmentAdapter = new Appointment2RecyclerView(this, list);
        appointmentRecyclerView.setAdapter(appointmentAdapter);
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.close();
        }
        super.onDestroy();
    }
}
