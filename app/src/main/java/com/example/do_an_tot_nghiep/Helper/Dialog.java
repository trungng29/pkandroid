package com.example.do_an_tot_nghiep.Helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.do_an_tot_nghiep.R;

public class Dialog {
    private View viewAlert;
    private AlertDialog alert;
    private Context context;
    private TextView msgText, alertTitle;
    private ImageView iconAlert;
    public Button btnOK;
    public Button btnCancel;

    public Dialog(Context context) {
        this.context = context;
    }

    public Dialog(Context context, int type) {
        this.context = context;
        if(type == 1){
            this.announce();
        }else{
            this.confirm();
        }
    }

    public void announce(){
        viewAlert = View.inflate(context, R.layout.dialog_annouce, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewAlert);

        alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCancelable(false);

        setControl();
    }

    public void confirm(){
        viewAlert = View.inflate(context, R.layout.dialog_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewAlert);

        alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCancelable(false);

        setControl();
    }

    private void setControl(){
        msgText = viewAlert.findViewById(R.id.msgText);
        alertTitle = viewAlert.findViewById(R.id.alertTitle);
        iconAlert = viewAlert.findViewById(R.id.iconAlert);
        btnOK = viewAlert.findViewById(R.id.btnOK);
        btnCancel = viewAlert.findViewById(R.id.btnCancel);
    }

    @SuppressLint("NonConstantResourceId")
    public void show(String title, String msg, Integer ico){
        if (alert == null) announce();
        switch (ico){
            case R.drawable.ic_close:
                iconAlert.setBackgroundResource(R.drawable.dialog_background_danger);
                break;
            case R.drawable.ic_info:
                iconAlert.setBackgroundResource(R.drawable.dialog_background_info);
                break;
            case R.drawable.ic_check:
                iconAlert.setBackgroundResource(R.drawable.dialog_background_success);
                break;
        }
        iconAlert.setImageResource(ico);
        msgText.setText(msg);
        alertTitle.setText(title);
        alert.show();
    }

    @SuppressLint("NonConstantResourceId")
    public void show(Integer resid, String msg, Integer ico){
        String title = context.getResources().getString(resid);
        show(title, msg, ico);
    }

    public void close(){
        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
    }

    public boolean isShowing() {
        return alert != null && alert.isShowing();
    }
}
