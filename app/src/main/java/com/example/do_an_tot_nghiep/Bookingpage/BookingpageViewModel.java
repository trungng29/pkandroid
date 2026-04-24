package com.example.do_an_tot_nghiep.Bookingpage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.do_an_tot_nghiep.Container.BookingPhotoReadAll;
import com.example.do_an_tot_nghiep.Container.BookingReadByID;
import com.example.do_an_tot_nghiep.Container.DoctorReadByID;
import com.example.do_an_tot_nghiep.Container.ServiceReadByID;
import com.example.do_an_tot_nghiep.Helper.SingleLiveEvent;
import com.example.do_an_tot_nghiep.Repository.BookingPhotoRepository;
import com.example.do_an_tot_nghiep.Repository.BookingRepository;
import com.example.do_an_tot_nghiep.Repository.DoctorRepository;
import com.example.do_an_tot_nghiep.Repository.ServiceRepository;

import java.util.Map;

/**
 * @author Phong-Kaster
 * @since 23-11-2022
 * Booking-page ViewModel
 */
public class BookingpageViewModel extends ViewModel {

    private SingleLiveEvent<ServiceReadByID> serviceReadByIdResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<BookingReadByID> bookingReadByIdResponse = new SingleLiveEvent<>();

    private MutableLiveData<Boolean> animation = new MutableLiveData<>();
    private ServiceRepository serviceRepository;
    private BookingRepository bookingRepository;
    private BookingPhotoRepository bookingPhotoRepository;
    private DoctorRepository doctorRepository;

    public MutableLiveData<Boolean> getAnimation() {
        return animation;
    }


    /**
     * @since 23-11-2022
     * instantiate repository
     */
    public void instantiate()
    {
        if( serviceRepository == null)
        {
            serviceRepository = new ServiceRepository();
        }
        if( bookingRepository == null)
        {
            bookingRepository = new BookingRepository();
        }
        if( bookingPhotoRepository == null)
        {
            bookingPhotoRepository = new BookingPhotoRepository();
        }
        if( doctorRepository == null)
        {
            doctorRepository = new DoctorRepository();
        }
    }

    /************************SERVICE READ BY ID***************************/
    public SingleLiveEvent<ServiceReadByID> getServiceReadByIdResponse() {
        return serviceReadByIdResponse;
    }

    public void serviceReadById(Map<String, String> header, String serviceId)
    {
        serviceRepository.readByID(header, serviceId);
        serviceReadByIdResponse = serviceRepository.getReadByIDResponse();
        animation = serviceRepository.getAnimation();
    }

    /************************BOOKING READ BY ID***************************/
    public SingleLiveEvent<BookingReadByID> getBookingReadByIdResponse() {
        return bookingReadByIdResponse;
    }
    public void bookingReadByID(Map<String, String> header, String bookingId)
    {
        bookingRepository.readByID(header, bookingId);
        bookingReadByIdResponse = bookingRepository.getReadByIDResponse();
        animation = bookingRepository.getAnimation();
    }


    /************************BOOKING PHOTO - READ ALL***************************/
    private SingleLiveEvent<BookingPhotoReadAll> bookingPhotoReadAllResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<BookingPhotoReadAll> getBookingPhotoReadAllResponse(){
        return bookingPhotoReadAllResponse;
    }
    public void bookingPhotoReadAll(Map<String, String> header, String bookingId)
    {
        bookingPhotoRepository.readAll(header, bookingId);
        bookingPhotoReadAllResponse = bookingPhotoRepository.getReadAllResponse();
        animation = bookingPhotoRepository.getAnimation();
    }

    /************************DOCTOR - READ BY ID***************************/
    private SingleLiveEvent<DoctorReadByID> doctorReadById = new SingleLiveEvent<>();
    public SingleLiveEvent<DoctorReadByID> getDoctorReadByIdResponse() {
        return doctorReadById;
    }
    public void doctorReadByID(Map<String, String> header, String doctorId)
    {
        doctorRepository.readById(header, doctorId);
        doctorReadById = doctorRepository.getReadByIdResponse();
        animation = doctorRepository.getAnimation();
    }
}
