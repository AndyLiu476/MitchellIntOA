package com.andy.application.MitchellOA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}