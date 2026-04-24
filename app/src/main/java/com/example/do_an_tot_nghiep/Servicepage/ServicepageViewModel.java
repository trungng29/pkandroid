package com.example.do_an_tot_nghiep.Servicepage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.DoctorReadAll;
import com.example.do_an_tot_nghiep.Container.ServiceReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.DoctorRepository;
import com.example.do_an_tot_nghiep.Repository.ServiceRepository;

import java.util.Map;

/**
 * @author  Phong-Kaster
 * @since 22-11-2022
 * Service-page view model
 */
public class ServicepageViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation;
    private SingleLiveEvent<ServiceReadByID> response = new SingleLiveEvent<>();
    private ServiceRepository repository;
    private DoctorRepository doctorRepository;
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    public SingleLiveEvent<ServiceReadByID> getResponse() {
        return response;
    }

    /**
     * @since 21-11-2022
     * create repository
     */
    public void instantiate()
    {
        if(repository == null)
        {
            repository = new ServiceRepository();
        }
        if( doctorRepository == null)
        {
            doctorRepository = new DoctorRepository();
        }
    }

    /**
     * @since 22-11-2022
     * @param header is the header of HTTP request
     * @param serviceId is the id of service
     */
    public void readById(Map<String, String> header, String serviceId)
    {
        repository.readByID(header, serviceId);
        response = repository.getReadByIDResponse();
        animation = repository.getAnimation();
    }

    /************ DOCTOR - READ ALL ******************/
    private SingleLiveEvent<DoctorReadAll> doctorReadAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<DoctorReadAll> getDoctorReadAllResponse() {
        return doctorReadAllResponse;
    }
    public void doctorReadAll(Map<String, String> header, Map<String, String> parameters)
    {
        doctorRepository.readAll(header, parameters);
        doctorReadAllResponse = doctorRepository.getReadAllResponse();
        animation = doctorRepository.getAnimation();
    }
}
