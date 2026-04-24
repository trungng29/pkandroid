package com.example.do_an_tot_nghiep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.do_an_tot_nghiep.Helper.Dialog;
import com.example.do_an_tot_nghiep.Helper.GlobalVariable;
import com.example.do_an_tot_nghiep.Helper.Notification;
import com.example.do_an_tot_nghiep.Helper.Tooltip;
import com.example.do_an_tot_nghiep.Homepage.HomepageActivity;
import com.example.do_an_tot_nghiep.Loginpage.LoginActivity;
import com.example.do_an_tot_nghiep.Model.User;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN ACTIVITY";
    private SharedPreferences sharedPreferences;
    private GlobalVariable globalVariable;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Step 0 - declare sharedPreferences & globalVariable*/
        globalVariable = (GlobalVariable)this.getApplication();
        sharedPreferences = this.getApplication()
                .getSharedPreferences(globalVariable.getSharedReferenceKey(), MODE_PRIVATE);
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        dialog = new Dialog(this);


        //If we wanna use notification on Android 8 or higher, this function must be run
        Notification notification = new Notification(this);
        notification.createChannel();


        /*Step 1 - does the application connect to Internet?*/
        boolean isConnected = isInternetAvailable();
        if( !isConnected )
        {
            dialog.announce();
            dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
            dialog.btnOK.setOnClickListener(view->{
                dialog.close();
                finish();
            });
            return;
        }


        /*Step 2 - is dark mode turned on?*/
        int value = sharedPreferences.getInt("darkMode", 1);
        AppCompatDelegate.setDefaultNightMode(value);


        /*Step 4 - is AccessToken null?*/
        String accessToken = sharedPreferences.getString("accessToken", null);
        if(accessToken != null)
        {
            globalVariable.setAccessToken(accessToken);
            Map<String, String> headers = globalVariable.getHeaders();
            viewModel.readPersonalInformation(headers);

            viewModel.getResponse().observe(this, response->{
                try
                {
                    int result = response.getResult();
                    if( result == 1)
                    {
                        User user = response.getData();
                        globalVariable.setAuthUser( user );

                        Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    if( result == 0)
                    {
                        sharedPreferences.edit().putString("accessToken",null).apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
                catch(Exception ex)
                {
                    dialog.announce();
                    dialog.show(R.string.attention, getString(R.string.check_your_internet_connection), R.drawable.ic_info);
                    dialog.btnOK.setOnClickListener(view->{
                        dialog.close();
                        finish();
                    });
                }
            });

        }
        else
        {
            Handler handler = new Handler(Looper.myLooper());
            handler.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            },1000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tooltip.setLocale(this, sharedPreferences);
    }

    /**
     * Cải tiến hàm kiểm tra Internet để hoạt động tốt trên cả máy thật và máy ảo
     */
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            // Hỗ trợ các phiên bản Android cũ hơn
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}
