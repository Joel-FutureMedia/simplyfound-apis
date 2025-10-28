package com.example.simplyfoundapis.services;

import com.example.simplyfoundapis.models.Status;
import com.example.simplyfoundapis.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    //post Method
    public Status saveNewStatus(Status newStatus){
        return statusRepository.save(newStatus);
    }
    //Get All status
    public List<Status> findAll(){
        return statusRepository.findAll();
    }

    //get by Id
    public Optional<Status> findById(int id){
        return statusRepository.findById(id);
    }

    public void deleteById(int id){
        statusRepository.deleteById(id);
    }

    public Status updateStatus(Status  newStatus){
       Status oldStatus = statusRepository.findById(newStatus.getId()).get();
       oldStatus.setStatusname(newStatus.getStatusname());
        return statusRepository.save(oldStatus);
    }



}
