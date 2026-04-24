package com.example.do_an_tot_nghiep.Searchpage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.DoctorReadAll;
import com.example.do_an_tot_nghiep.Container.ServiceReadAll;
import com.example.do_an_tot_nghiep.Container.SpecialityReadAll;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.DoctorRepository;
import com.example.do_an_tot_nghiep.Repository.ServiceRepository;
import com.example.do_an_tot_nghiep.Repository.SpecialityRepository;

import java.util.Map;

/**
 * @author Phong-Kaster
 * @since 21-11-2022
 * Search-page view model
 */
public class SearchpageViewModel extends ViewModel {

    private SingleLiveEvent<SpecialityReadAll> specialityReadAll = new SingleLiveEvent<>();
    private SingleLiveEvent<DoctorReadAll> doctorReadAllResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<ServiceReadAll> serviceReadAllResponse = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> animation = new MutableLiveData<>();


    private SpecialityRepository specialityRepository;
    private DoctorRepository doctorRepository;
    private ServiceRepository serviceRepository;

    /*GETTER*/
    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    public SingleLiveEvent<SpecialityReadAll> getSpecialityReadAll() {
        return specialityReadAll;
    }

    public SingleLiveEvent<DoctorReadAll> getDoctorReadAllResponse() {
        return doctorReadAllResponse;
    }

    public SingleLiveEvent<ServiceReadAll> getServiceReadAllResponse() {
        return serviceReadAllResponse;
    }

    /**
     * @since 21-11-2022
     * create repository
     */
    public void instantiate()
    {
        if(specialityRepository == null)
        {
            specialityRepository = new SpecialityRepository();
        }
        if( doctorRepository == null)
        {
            doctorRepository = new DoctorRepository();
        }
        if( serviceRepository == null )
        {
            serviceRepository = new ServiceRepository();
        }
    }


    /* ******************** DOCTOR READ ALL ********************/
    public void doctorReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        doctorRepository.readAll(headers, parameters);
        doctorReadAllResponse = doctorRepository.getReadAllResponse();
        animation = doctorRepository.getAnimation();
    }

    /* ******************** SPECIALITY READ ALL ********************/
    public void specialityReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        specialityRepository.readAll(headers, parameters);
        specialityReadAll = specialityRepository.getReadAllResponse();
        animation = specialityRepository.getAnimation();
    }

    /* ******************** SERVICE READ ALL ********************/
    public void serviceReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        serviceRepository.readAll(headers, parameters);
        serviceReadAllResponse = serviceRepository.getReadAllResponse();
        animation = serviceRepository.getAnimation();
    }
}
