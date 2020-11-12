package com.andy.application.MitchellOA;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Controller class
@RestController
@NoRepositoryBean
public class VehicleController {
    private final int YEAR_START = 1950;
    private final int YEAR_END = 2050;

    private final VehicleRepository vehicleRepository;

    // VehicleController Constructor
    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /* Get vehicles from repo via one or more vehicle properties */
    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getVehicles(
            @RequestParam(value = "yearStart", required = false) Integer yearStart,
            @RequestParam(value =  "yearEnd", required = false) Integer yearEnd,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "make", required = false) String make )  {

        // Turn list of all vehicles into a stream
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        Stream<Vehicle> s = allVehicles.stream();

        // Return if empty - no input
        if(yearStart==null && yearEnd==null && model==null && make==null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(allVehicles);
        }

        // Filter vehicles by yearStart and yearEnd
        if (yearStart != null && yearEnd != null && (yearStart <= yearEnd)) {
            s = s.filter(v->(v.getYear() >= yearStart && v.getYear() <= yearEnd));
        }

        // Filter vehicles by model
        if (model != null) {
            List<String> models = Arrays.asList(model.split(","));
            s = s.filter(v->models.contains(v.getModel()));
        }

        // Filter vehicles by make
        if (make != null) {
            List<String> makes = Arrays.asList(make.split(","));
            s = s.filter(v->makes.contains(v.getMake()));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(s.collect(Collectors.toList()));
    }


    /* Gets vehicle object from repo via ID*/
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> getVehiclesById(@PathVariable(value="id") int vehicleID)
            throws ResourceNotFoundException {
        Vehicle vehicle = vehicleRepository
                .findById(vehicleID)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid ID"));

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(vehicle);
    }

    /* Add a brand new vehicle to the repo */
    @PostMapping("/vehicles")
    public ResponseEntity createVehicle(@RequestBody Vehicle vehicle) {
        // reset vehicle ID
        vehicle.setId(0);
        boolean errorVehicle = false;

        // Make sure new vehicle years is within range of YEAR_START and YEAR_END
        if (!(vehicle.getYear() >= YEAR_START && vehicle.getYear() <= YEAR_END)) {
            System.err.println("ERROR: Vehicle year not within range");
            errorVehicle = true;
        }

        // Make sure new vehicle make is not null and has a valid make
        if (vehicle.getMake() == null || vehicle.getMake().length() == 0) {
            System.err.println("ERROR: Make was not valid");
            errorVehicle = true;
        }

        // Make sure new vehicle model is not null and has a valid model
        if (vehicle.getModel() == null || vehicle.getModel().length() == 0) {
            System.err.println("ERROR: Model was not valid");
            errorVehicle = true;
        }

        // successfully saves vehicle to repo if not error
        if(!errorVehicle) {
            vehicleRepository.save(vehicle);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(vehicle);
    }

    /* Updates the information of an existing vehicle */
    @PutMapping("/vehicles")
    public ResponseEntity updateVehicle(@RequestBody Vehicle vehicleSpecs) throws ResourceNotFoundException {
        Vehicle vehicle =
                vehicleRepository.findById(vehicleSpecs.getId())
                .orElseThrow(()->new ResourceNotFoundException("Invalid ID"));

        // ensure update years are valid
        if (vehicleSpecs.getYear() >= YEAR_START && vehicleSpecs.getYear() <= YEAR_END) {
            vehicle.setYear(vehicleSpecs.getYear());
        } else {
            System.err.println("ERROR: Vehicle year not within range. Year was not updated.");
        }

        // ensure update make is not null and is valid
        if (vehicleSpecs.getMake() != null && vehicleSpecs.getMake().length() != 0) {
            vehicle.setMake(vehicleSpecs.getMake());
        } else {
            System.err.println("ERROR: Make was not valid. Make was not updated.");

        }

        // ensure update model is not null and is valid
        if (vehicleSpecs.getModel() != null && vehicleSpecs.getModel().length() != 0) {
            vehicle.setModel(vehicleSpecs.getModel());
        } else {
            System.err.println("ERROR: Model was not valid. Model was not updated.");
        }

        // save updated vehicle to repository
        vehicleRepository.save(vehicle);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(vehicle);
    }


    /* Delete vehicle from repo via ID value*/
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable(value = "id") int vehicleID)
            throws ResourceNotFoundException {
        // obtain vehicle
        Vehicle vehicle =
                vehicleRepository
                        .findById(vehicleID)
                        .orElseThrow(() -> new ResourceNotFoundException("Invalid ID"));

        // delete vehicle
        vehicleRepository.delete(vehicle);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Vehicle Deleted");
    }

}
