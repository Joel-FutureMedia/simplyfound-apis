package com.example.simplyfoundapis.controllers;

import com.example.simplyfoundapis.models.Status;
import com.example.simplyfoundapis.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/status")
@CrossOrigin(origins = {"https://simplyfound.vercel.app", "https://newgate.simplyfound.com.na","https://newgate.simplyfound.com.na/admin","https://newgateinvestments.simplyfound.com.na"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        exposedHeaders = {"Content-Disposition", "Content-Type"},
        allowCredentials = "true")
public class StatusController {
    @Autowired
    private StatusService statusService;

    //posting
    @PostMapping("/post")
    public Status post(@RequestBody Status status){
        return statusService.saveNewStatus(status);
    }

    //find all status
    @GetMapping("/all")
    public List<Status> findAll(){
        return statusService.findAll();
    }

    //find by Id
    @GetMapping("/{id}")
    public Optional<Status> findById(@PathVariable int id){
        return statusService.findById(id);
    }

    @PutMapping("/{id}")
    public Status update(@PathVariable int id, @RequestBody Status status){
        status.setId(id);
        return statusService.updateStatus(status);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable int id){
        statusService.deleteById(id);
    }

}
