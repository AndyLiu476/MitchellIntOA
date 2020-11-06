package com.andy.application.MitchellOA;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VehicleControllerTest {
    // Auto inject Object instances
    @Autowired
    private TestRestTemplate restTemplate;

    // Injected HTTP port that got allocated
    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    // Runs before every @Test method
    @Before
    public void beforeEach() {
        // Sample cars being saved to database
        int[] years = {1951, 2000, 2000, 2001, 2002, 2004, 2005, 1950, 2050};
        String[] makes = {"Make1", "Make2", "Make3", "Make4", "Make5", "Make6", "Make7", "Make8", "Make9"};
        String[] models = {"Model1", "Model2", "Model3", "Model4", "Model5", "Model6", "Model7", "Model8", "Model9"};

        // save vehicles to database
        for (int i = 0; i < makes.length; i++) {
            Vehicle vehicle = new Vehicle(years[i], makes[i], models[i]);
            restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle, String.class);
        }
    }

    @Test
    @Order(2)
    public void testGetVehicleById() {
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/1", Vehicle.class);

        Assert.assertEquals(1951, vehicle.getYear());
        Assert.assertEquals( "Make1", vehicle.getMake());
        Assert.assertEquals("Model1", vehicle.getModel());
    }


    /*  TEST FAILS BECAUSE UPDATE TESTS OCCUR BEFORE
        RUN TEST INDIVIDUALLY TO CHECK */
    /*
    @Test
    @Order(1)
    public void testGetVehicles() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        int[] years = {1951, 2000, 2000, 2001, 2002, 2004, 2005, 1950, 2050};
        String[] makes = {"Make1", "Make2", "Make3", "Make4", "Make5", "Make6", "Make7", "Make8", "Make9"};
        String[] models = {"Model1", "Model2", "Model3", "Model4", "Model5", "Model6", "Model7", "Model8", "Model9"};

        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        Assert.assertNotNull(response.getBody());

        for(int i = 0; i < makes.length; i++) {
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake()); // check make
            Assert.assertEquals(models[i], response.getBody().get(i).getModel()); // check model
            Assert.assertEquals(years[i], response.getBody().get(i).getYear()); // check years
        }
    }*/

    @Test
    @Order(12)
    public void testUpdateVehicle() {
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/3", Vehicle.class);
        vehicle.setModel("Model10"); // set different model
        vehicle.setYear(2001); // set different year
        vehicle.setMake("Make10"); // set different make

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Vehicle> entity = new HttpEntity<>(vehicle, headers);

        ResponseEntity<Vehicle> updatedVehicle = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.PUT, entity, Vehicle.class); // update vehicle

        Assert.assertEquals(vehicle.getId(), updatedVehicle.getBody().getId()); // get vehicle via ID
        Assert.assertEquals(2001, updatedVehicle.getBody().getYear()); // check updated year
        Assert.assertEquals("Model10", updatedVehicle.getBody().getModel()); // check updated model
        Assert.assertEquals( "Make10", updatedVehicle.getBody().getMake()); // check updated make
    }

    @Test
    @Order(13)
    public void testInvalidUpdateVehicleYear() {
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/7", Vehicle.class);
        vehicle.setModel("Model10"); // set different model
        vehicle.setYear(1949); // set invalid year
        vehicle.setMake("Make10"); // set different make

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Vehicle> entity = new HttpEntity<>(vehicle, headers);

        ResponseEntity<Vehicle> updatedVehicle = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.PUT, entity, Vehicle.class); // update vehicle

        Assert.assertEquals(vehicle.getId(), updatedVehicle.getBody().getId()); // get vehicle via ID
        Assert.assertEquals(2005, updatedVehicle.getBody().getYear()); // check updated year
        Assert.assertEquals("Model10", updatedVehicle.getBody().getModel()); // check updated model
        Assert.assertEquals( "Make10", updatedVehicle.getBody().getMake()); // check updated make

        vehicle.setYear(2051); // set invalid year
        ResponseEntity<Vehicle> updatedVehicle2 = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.PUT, entity, Vehicle.class); // update vehicle #2

        Assert.assertEquals(vehicle.getId(), updatedVehicle2.getBody().getId()); // get vehicle via ID
        Assert.assertEquals(2005, updatedVehicle2.getBody().getYear()); // check updated year
    }

    @Test
    @Order(14)
    public void testInvalidUpdateVehicleMake() {
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/8", Vehicle.class);
        vehicle.setModel("Model10"); // set different model
        vehicle.setYear(2000); // set invalid year
        vehicle.setMake(""); // set different make

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Vehicle> entity = new HttpEntity<>(vehicle, headers);

        ResponseEntity<Vehicle> updatedVehicle = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.PUT, entity, Vehicle.class); // update vehicle

        Assert.assertEquals(vehicle.getId(), updatedVehicle.getBody().getId()); // get vehicle via ID
        Assert.assertEquals(2000, updatedVehicle.getBody().getYear()); // check updated year
        Assert.assertEquals("Model10", updatedVehicle.getBody().getModel()); // check updated model
        Assert.assertEquals( "Make8", updatedVehicle.getBody().getMake()); // check updated make
    }

    @Test
    @Order(15)
    public void testInvalidUpdateVehicleModel() {
        Vehicle vehicle = restTemplate.getForObject(getRootUrl() + "/vehicles/9", Vehicle.class);
        vehicle.setModel(""); // set different model
        vehicle.setYear(2000); // set invalid year
        vehicle.setMake("Make10"); // set different make

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Vehicle> entity = new HttpEntity<>(vehicle, headers);

        ResponseEntity<Vehicle> updatedVehicle = restTemplate.exchange(getRootUrl() + "/vehicles",
                HttpMethod.PUT, entity, Vehicle.class); // update vehicle

        Assert.assertEquals(vehicle.getId(), updatedVehicle.getBody().getId()); // get vehicle via ID
        Assert.assertEquals(2000, updatedVehicle.getBody().getYear()); // check updated year
        Assert.assertEquals("Model9", updatedVehicle.getBody().getModel()); // check updated model
        Assert.assertEquals( "Make10", updatedVehicle.getBody().getMake()); // check updated make
    }

    @Test
    @Order(3)
    public void testDeleteVehicleById()  {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        Vehicle vehicle = new Vehicle(1951, "Make10", "Model10"); // create vehicle

        ResponseEntity<Vehicle> created = restTemplate.postForEntity(getRootUrl() +
                "/vehicles", vehicle, Vehicle.class); // add vehicle

        ResponseEntity<List<Vehicle>> list = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=1951&yearEnd=1951&make=Make10&model=Model10",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}); // attempt get vehicle

        Assert.assertEquals(1, list.getBody().size()); // check vehicle is added

        restTemplate.exchange(getRootUrl() + "/vehicles/" + created.getBody().getId(), HttpMethod.DELETE, entity, String.class); // delete

        list = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=1951&yearEnd=1951&make=Make10&model=Model10",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}); // attempt to get

        Assert.assertEquals(0, list.getBody().size()); // check deletion successful
    }

    @Test
    @Order(4)
    public void testDeleteAndGetDuplicateVehicles() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        Vehicle vehicle = new Vehicle(1951, "Make10", "Model10"); // create vehicle
        Vehicle vehicle2 = new Vehicle(1951, "Make10", "Model10"); // create vehicle2

        ResponseEntity<Vehicle> created1 = restTemplate.postForEntity(getRootUrl() +
                "/vehicles", vehicle, Vehicle.class); // add vehicle

        restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle2, Vehicle.class); // add vehicle

        ResponseEntity<List<Vehicle>> list = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=1951&yearEnd=1951&make=Make10&model=Model10",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}); // attempt get vehicle

        Assert.assertEquals(2, list.getBody().size()); // check both vehicles are added

        restTemplate.exchange(getRootUrl() + "/vehicles/" + created1.getBody().getId(),
                HttpMethod.DELETE, entity, String.class); // delete one car via ID

        list = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=1951&yearEnd=1951&make=Make10&model=Model10",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}); // attempt to get

        Assert.assertEquals(1, list.getBody().size()); // check deletion successful
    }

    @Test
    @Order(5)
    public void testFilterVehicleByYearMakeModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?model=Accord&yearStart=2015&yearEnd=2019&make=Honda",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        for(int i = 0; i < response.getBody().size(); i++) {
            Assert.assertEquals(2018, response.getBody().get(i).getYear());
            Assert.assertEquals("Accord", response.getBody().get(i).getModel());
            Assert.assertEquals("Honda", response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(6)
    public void testFilterVehicleByYear() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        int[] years = {1951, 2000, 2000, 2001, 2002, 2004, 2005, 1950, 2050};
        String[] makes = {"Make1", "Make2", "Make3", "Make4", "Make5", "Make6", "Make7", "Make8", "Make9"};
        String[] models = {"Model1", "Model2", "Model3", "Model4", "Model5", "Model6", "Model7", "Model8", "Model9"};

        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=1950&yearEnd=2000",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        for(int i = 0; i < 3; i++) {
            Assert.assertEquals(years[i], response.getBody().get(i).getYear());
            Assert.assertEquals(models[i], response.getBody().get(i).getModel());
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(7)
    public void testFilterVehicleByMake() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        int[] years = {1951, 2000, 2000, 2001, 2002, 2004, 2005, 1950, 2050};
        String[] makes = {"Make1", "Make2", "Make3", "Make4", "Make5", "Make6", "Make7", "Make8", "Make9"};
        String[] models = {"Model1", "Model2", "Model3", "Model4", "Model5", "Model6", "Model7", "Model8", "Model9"};
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?make=Make1,Make2,Make3",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        for(int i = 0; i < 3; i++) {
            Assert.assertEquals(years[i], response.getBody().get(i).getYear());
            Assert.assertEquals(models[i], response.getBody().get(i).getModel());
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(8)
    public void testFilterVehicleByModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        int[] years = {1951, 2000, 2000, 2001, 2002, 2004, 2005, 1950, 2050};
        String[] makes = {"Make1", "Make2", "Make3", "Make4", "Make5", "Make6", "Make7", "Make8", "Make9"};
        String[] models = {"Model1", "Model2", "Model3", "Model4", "Model5", "Model6", "Model7", "Model8", "Model9"};

        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?model=Model5,Model6",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        for (int i = 4; i < 6; i++) {
            Assert.assertEquals(years[i], response.getBody().get(i).getYear());
            Assert.assertEquals(models[i], response.getBody().get(i).getModel());
            Assert.assertEquals(makes[i], response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(9)
    public void testFilterVehicleByYearMake() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=2016&yearEnd=2016&make=Kia",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        for(int i = 0; i < response.getBody().size(); i++) {
            Assert.assertEquals(2016, response.getBody().get(i).getYear());
            Assert.assertEquals("Sorrento", response.getBody().get(i).getModel());
            Assert.assertEquals("Kia", response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(10)
    public void testFilterVehicleByYearModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?yearStart=2018&yearEnd=2018&model=Odyssey",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        for(int i = 0; i < response.getBody().size(); i++) {
            Assert.assertEquals(2018, response.getBody().get(i).getYear());
            Assert.assertEquals("Odyssey", response.getBody().get(i).getModel());
            Assert.assertEquals("Honda", response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(11)
    public void testFilterVehicleByMakeModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<Vehicle>> response = restTemplate.exchange(getRootUrl() +
                        "/vehicles?model=Sienna&make=Toyota",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        for(int i = 0; i < response.getBody().size(); i++) {
            Assert.assertEquals(2018, response.getBody().get(i).getYear());
            Assert.assertEquals("Sienna", response.getBody().get(i).getModel());
            Assert.assertEquals("Toyota", response.getBody().get(i).getMake());
        }
    }

    @Test
    @Order(16)
    public void testCreateInvalidVehicleYear() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        Vehicle vehicle = new Vehicle(1949, "Make10", "Model10");

        ResponseEntity<Vehicle> created =
                restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle, Vehicle.class); // add vehicle
        ResponseEntity<Vehicle> vehicle2 = restTemplate.exchange(getRootUrl() + "/vehicles/" +
                created.getBody().getId(), HttpMethod.GET, entity, Vehicle.class);

        Assert.assertEquals(0, vehicle2.getBody().getYear());
        Assert.assertNull( vehicle2.getBody().getMake());
        Assert.assertNull( vehicle2.getBody().getModel());
    }

    @Test
    @Order(17)
    public void testCreateInvalidVehicleMake() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        Vehicle vehicle = new Vehicle(1949, "", "Model10");

        ResponseEntity<Vehicle> created =
                restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle, Vehicle.class); // add vehicle
        ResponseEntity<Vehicle> vehicle2 = restTemplate.exchange(getRootUrl() + "/vehicles/" +
                created.getBody().getId(), HttpMethod.GET, entity, Vehicle.class);

        Assert.assertEquals(0, vehicle2.getBody().getYear());
        Assert.assertNull( vehicle2.getBody().getMake());
        Assert.assertNull( vehicle2.getBody().getModel());
    }

    @Test
    @Order(18)
    public void testCreateInvalidVehicleModel() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        Vehicle vehicle = new Vehicle(1949, "Make10", "");

        ResponseEntity<Vehicle> created =
                restTemplate.postForEntity(getRootUrl() + "/vehicles", vehicle, Vehicle.class); // add vehicle
        ResponseEntity<Vehicle> vehicle2 = restTemplate.exchange(getRootUrl() + "/vehicles/" +
                created.getBody().getId(), HttpMethod.GET, entity, Vehicle.class);

        Assert.assertEquals(0, vehicle2.getBody().getYear());
        Assert.assertNull( vehicle2.getBody().getMake());
        Assert.assertNull( vehicle2.getBody().getModel());
    }

}
