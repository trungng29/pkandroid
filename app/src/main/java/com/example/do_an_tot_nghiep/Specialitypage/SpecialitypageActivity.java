package com.example.do_an_tot_nghiep.Specialitypage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an_tot_nghiep.Configuration.Constant;
import com.example.do_an_tot_nghiep.Helper.Dialog;
import com.example.do_an_tot_nghiep.Helper.GlobalVariable;
import com.example.do_an_tot_nghiep.Helper.LoadingScreen;
import com.example.do_an_tot_nghiep.Helper.Tooltip;
import com.example.do_an_tot_nghiep.Model.Doctor;
import com.example.do_an_tot_nghiep.Model.Speciality;
import com.example.do_an_tot_nghiep.R;
import com.example.do_an_tot_nghiep.RecyclerView.DoctorRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialitypageActivity extends AppCompatActivity {

    private final String TAG = "Speciality-page Activity";
    private TextView txtName;
    private WebView wvwDescription;
    private RecyclerView recyclerViewDoctor;


    private GlobalVariable globalVariable;
    private String specialityId;

    private LoadingScreen loadingScreen;
    private Dialog dialog;
    private ImageView imgAvatar;

    private ImageButton btnBack;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialitypage);

        setupComponent();
        setupViewModel();
        setupEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    private void setupComponent()
    {
        txtName = findViewById(R.id.txtName);
        wvwDescription = findViewById(R.id.wvwDescription);
        recyclerViewDoctor = findViewById(R.id.recyclerViewDoctor);


        globalVariable = (GlobalVariable) this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globalVariable.getSharedReferenceKey(), MODE_PRIVATE);
        specialityId = getIntent().getStringExtra("specialityId");


        loadingScreen = new LoadingScreen(this);
        dialog = new Dialog(this);
        imgAvatar = findViewById(R.id.imgAvatar);

        btnBack = findViewById(R.id.btnBack);
    }

    private void setupViewModel()
    {
        SpecialitypageViewModel viewModel = new ViewModelProvider(this).get(SpecialitypageViewModel.class);
        viewModel.instantiate();


        Map<String, String> header = globalVariable.getHeaders();
        // FIX: Gọi đúng tên hàm trong ViewModel
        viewModel.readById(header, specialityId);


        Map<String, String> parameters = new HashMap<>();
        parameters.put("speciality_id", specialityId);
        viewModel.doctorReadAll(header, parameters);


        viewModel.getAnimation().observe(this, aBoolean -> {
            if(aBoolean) loadingScreen.start();
            else loadingScreen.stop();
        });



        viewModel.getDoctorReadAllResponse().observe(this, response->{
            if (response == null) return;
            int result = response.getResult();
            try
            {
                if( result == 1)
                {
                    List<Doctor> list = response.getData();
                    setupDoctorRecyclerView(list);
                }
                else
                {
                    Toast.makeText(this, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                finish();
            }
        });

        // FIX: Gọi đúng tên hàm lấy dữ liệu trong ViewModel
        viewModel.getResponse().observe(this, response->{
            if (response == null) return;
            int result = response.getResult();
            try
            {
                if( result == 1)
                {
                    Speciality speciality = response.getData();
                    printSpecialityInformation(speciality);
                }
                else
                {
                    Toast.makeText(this, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                finish();
            }
        });
    }

    private void setupEvent()
    {
        btnBack.setOnClickListener(view->finish());
    }

    private void setupDoctorRecyclerView(List<Doctor> list)
    {
        DoctorRecyclerView doctorAdapter = new DoctorRecyclerView(this, list);
        recyclerViewDoctor.setAdapter(doctorAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewDoctor.setLayoutManager(manager);
    }

    private void printSpecialityInformation(Speciality speciality)
    {
        String name = speciality.getName();
        String description = "<html><style>body{font-size: 11px}</style><body>"+  speciality.getDescription() +"</body></html>";
        String image = Constant.UPLOAD_URI() + speciality.getImage();
        txtName.setText(name);
        Picasso.get().load(image).into(imgAvatar);
        wvwDescription.loadDataWithBaseURL(null, description, "text/HTML", "UTF-8", null);
    }
}
