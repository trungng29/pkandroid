package com.example.do_an_tot_nghiep.Doctorpage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.DoctorReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.DoctorRepository;

import java.util.Map;

/**
 * @author Phong-Kaster
 * @since 20-11-2022
 */
public class DoctorpageViewModel extends ViewModel {

    private SingleLiveEvent<DoctorReadByID> response = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> animation;
    private DoctorRepository repository;

    public SingleLiveEvent<DoctorReadByID> getResponse() {
        return response;
    }

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }

    /**
     * @since 20-11-2022
     * create repository
     */
    public void instantiate()
    {
        if(repository == null)
        {
            repository = new DoctorRepository();
        }
    }

    /**
     * @since 20-11-2022
     * read by id
     */
    public void readById(Map<String, String> headers, String id)
    {
        repository.readById(headers, id);
        response = repository.getReadByIdResponse();
        animation = repository.getAnimation();
    }
}
