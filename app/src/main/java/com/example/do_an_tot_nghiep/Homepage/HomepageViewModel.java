package com.example.do_an_tot_nghiep.Homepage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.DoctorReadAll;
import com.example.do_an_tot_nghiep.Container.SpecialityReadAll;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.DoctorRepository;
import com.example.do_an_tot_nghiep.Repository.SpecialityRepository;

import java.util.Map;

/**
 * @author Phong-Kaster
 * @since 17-11-20222
 * Homepage View Model
 */
public class HomepageViewModel extends ViewModel {

    private MutableLiveData<Boolean> animation;
    private SingleLiveEvent<SpecialityReadAll> specialityReadAllResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DoctorReadAll> doctorReadAllResponse = new SingleLiveEvent<>();

    private SpecialityRepository specialityRepository;
    private DoctorRepository doctorRepository;

    /**
     * @since 17-11-2022
     * @return MutableLiveData<Boolean> animation
     */
    public MutableLiveData<Boolean> getAnimation() {
        if( animation == null )
        {
            animation = new MutableLiveData<>();
        }
        return animation;
    }



    /**
     * @since 17-11-2022
     * create speciality repository
     */
    public void instantiate()
    {
        if(specialityRepository == null)
        {
            specialityRepository = new SpecialityRepository();
        }
        if(doctorRepository == null)
        {
            doctorRepository = new DoctorRepository();
        }
    }

    /* ***************************** SPECIALITY ***********************************/
    /**
     * @since 17-11-2022
     * @return SingleLiveEvent<SpecialityReadAll> specialityReadAllResponse
     */
    public SingleLiveEvent<SpecialityReadAll> getSpecialityReadAllResponse() {
        return specialityReadAllResponse;
    }

    /**
     * @since 17-11-2022
     * @param headers is the headers of HTTP request
     * @param parameters is conditions of HTTP request
     */
    public void specialityReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        specialityRepository.readAll(headers, parameters);
        specialityReadAllResponse = specialityRepository.getReadAllResponse();
        animation = specialityRepository.getAnimation();
    }



    /* ***************************** DOCTOR ***********************************/
    /**
     * @since 17-11-2022
     * @return SingleLiveEvent<DoctorReadAll> doctorReadAllResponse
     */
    public SingleLiveEvent<DoctorReadAll> getDoctorReadAllResponse() {
        return doctorReadAllResponse;
    }

    /**
     * @since 17-11-2022
     * @param headers is the headers of HTTP request
     * @param parameters is conditions of HTTP request
     */
    public void doctorReadAll(Map<String, String> headers, Map<String, String> parameters)
    {
        doctorRepository.readAll(headers, parameters);
        doctorReadAllResponse = doctorRepository.getReadAllResponse();
        animation = doctorRepository.getAnimation();
    }
}
