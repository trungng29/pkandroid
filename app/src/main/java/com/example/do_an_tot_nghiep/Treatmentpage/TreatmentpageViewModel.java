package com.example.do_an_tot_nghiep.Treatmentpage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.AppointmentReadAll;
import com.example.do_an_tot_nghiep.Container.TreatmentReadAll;
import com.example.do_an_tot_nghiep.Container.TreatmentReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.AppointmentRepository;
import com.example.do_an_tot_nghiep.Repository.TreatmentRepository;

import java.util.Map;

public class TreatmentpageViewModel extends ViewModel {

    private AppointmentRepository appointmentRepository;
    private TreatmentRepository treatmentRepository;
    public void instantiate()
    {
        if( treatmentRepository == null)
        {
            treatmentRepository = new TreatmentRepository();
        }
        if( appointmentRepository == null)
        {
            appointmentRepository = new AppointmentRepository();
        }
    }


    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation(){
        return animation;
    }

    /**************** APPOINTMENT - READ ALL********************/
    private SingleLiveEvent<AppointmentReadAll> appointmentReadAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<AppointmentReadAll> getAppointmentReadAllResponse()
    {
        return appointmentReadAllResponse;
    }
    public void appointmentReadAll(Map<String, String> header, Map<String, String> parameters)
    {
        appointmentRepository.readAll(header, parameters);
        appointmentReadAllResponse = appointmentRepository.getReadAllResponse();
        animation = appointmentRepository.getAnimation();
    }

    /**************** TREATMENT - READ ALL of AN APPOINTMENT********************/
    private SingleLiveEvent<TreatmentReadAll> treatmentReadAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<TreatmentReadAll> getTreatmentReadAllResponse() {
        return treatmentReadAllResponse;
    }
    public void treatmentReadAll(Map<String, String> header, String appointmentId)
    {
        treatmentRepository.readAll(header, appointmentId);
        treatmentReadAllResponse = treatmentRepository.getReadAllResponse();
        animation = treatmentRepository.getAnimation();
    }


    /**************** TREATMENT - READ BY ID of a treatment from AN APPOINTMENT********************/
    private SingleLiveEvent<TreatmentReadByID> treatmentReadByIDResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<TreatmentReadByID> getTreatmentReadByIDResponse() {
        return treatmentReadByIDResponse;
    }
    public void treatmentReadByID(Map<String, String> header, String treatmentId)
    {
        treatmentRepository.readByID(header, treatmentId);
        treatmentReadByIDResponse = treatmentRepository.getReadByIDResponse();
        animation = treatmentRepository.getAnimation();
    }
}
