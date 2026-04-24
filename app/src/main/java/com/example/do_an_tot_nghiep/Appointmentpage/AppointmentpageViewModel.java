package com.example.do_an_tot_nghiep.Appointmentpage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.AppointmentQueue;
import com.example.do_an_tot_nghiep.Container.AppointmentReadAll;
import com.example.do_an_tot_nghiep.Container.AppointmentReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.AppointmentQueueRepository;
import com.example.do_an_tot_nghiep.Repository.AppointmentRepository;

import java.util.Map;

/**
 * @author Phong-Kaster
 * @since 26-11-2022
 * Appointment-page view model
 */
public class AppointmentpageViewModel extends ViewModel {


    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    private AppointmentRepository repository;
    private AppointmentQueueRepository queueRepository;
    public void instantiate()
    {
        if( repository == null)
        {
            repository = new AppointmentRepository();
        }
        if( queueRepository == null)
        {
            queueRepository = new AppointmentQueueRepository();
        }
    }

    /************************ APPOINTMENTS - READ ALL ***************************/
    // FIX: Dùng SingleLiveEvent để tránh lặp lại Dialog lỗi
    private SingleLiveEvent<AppointmentReadAll> readAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<AppointmentReadAll> getReadAllResponse() {
        return readAllResponse;
    }
    public void readAll(Map<String, String> header, Map<String, String> parameters)
    {
        animation = repository.getAnimation();
        repository.readAll(header, parameters);
        readAllResponse = repository.getReadAllResponse();
    }

    /************************ APPOINTMENTS - READ BY ID ***************************/
    private SingleLiveEvent<AppointmentReadByID> readByIDResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<AppointmentReadByID> getReadByIDResponse(){return readByIDResponse;}
    public void readByID(Map<String, String> header, String appointmentID)
    {
        repository.readByID(header, appointmentID);
        readByIDResponse = repository.getReadByIDResponse();
    }

    /************************ QUEUE - READ BY ID ***************************/
    private MutableLiveData<AppointmentQueue> appointmentQueueResponse = new MutableLiveData<>();
    public MutableLiveData<AppointmentQueue> getAppointmentQueueResponse(){ return appointmentQueueResponse;}
    public void getQueue(Map<String, String> header, Map<String, String> parameter)
    {
        appointmentQueueResponse = queueRepository.getAppointmentQueue(header, parameter);
    }
}
