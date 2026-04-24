package com.example.do_an_tot_nghiep.Specialitypage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.DoctorReadAll;
import com.example.do_an_tot_nghiep.Container.SpecialityReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.DoctorRepository;
import com.example.do_an_tot_nghiep.Repository.SpecialityRepository;

import java.util.Map;

/**
 * @author Phong-Kaster
 * @since 19-11-2022
 */
public class SpecialitypageViewModel extends ViewModel {

    private SingleLiveEvent<SpecialityReadByID> response = new SingleLiveEvent<>();
    private SingleLiveEvent<DoctorReadAll> doctorReadAllResponse = new SingleLiveEvent<>();

    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    private SpecialityRepository repository;
    private DoctorRepository doctorRepository;

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    public SingleLiveEvent<SpecialityReadByID> getResponse() {
        return response;
    }

    public SingleLiveEvent<DoctorReadAll> getDoctorReadAllResponse() {
        return doctorReadAllResponse;
    }

    /**
     * @since 21-11-2022
     * create repository
     */
    public void instantiate()
    {
        if(repository == null)
        {
            repository = new SpecialityRepository();
        }
        if( doctorRepository == null)
        {
            doctorRepository = new DoctorRepository();
        }
    }

    /**
     * @since 19-11-2022
     * @param headers is the header of HTTP request
     * @param specialityId is the id of speciality
     */
    public void readById(Map<String, String> headers, String specialityId)
    {
        repository.readById(headers, specialityId);
        response = repository.getReadByIdResponse();
        animation = repository.getAnimation();
    }

    /**
     * @since 19-12-2022
     * @param headers is header
     * @param parameters is parameters
     */
    public void doctorReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        doctorRepository.readAll(headers, parameters);
        doctorReadAllResponse = doctorRepository.getReadAllResponse();
        animation = doctorRepository.getAnimation();
    }
}
