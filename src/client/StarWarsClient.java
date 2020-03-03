package client;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import model.AllPlanets;
import model.StarWarsPlanet;

public class StarWarsClient {
    
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("https://swapi.co/api/planets/{i}");

    public StarWarsPlanet findStarWarsPlanetById(String id) {

        try {
            StarWarsPlanet p = target.resolveTemplate("i", id)
                    .request(MediaType.APPLICATION_JSON).get(StarWarsPlanet.class);
            return id.trim().isEmpty() ? null : p;
        } catch (NotFoundException e) {
            return null;
        }

    }

    public List<StarWarsPlanet> getAll() {
        String next = "https://swapi.co/api/planets/";
        List<StarWarsPlanet> planetResult = new ArrayList<>();

        while (next != null) {
            WebTarget localTarget = client.target(next);
            AllPlanets planets = localTarget.request(MediaType.APPLICATION_JSON).get(AllPlanets.class);

            try {
                next = planets.getNext();
            } catch (NullPointerException e) {
                next = null;
            }

            planetResult.addAll(planets.getResults());

        }

        return planetResult;
    }

    public List<StarWarsPlanet> getAllSearched(String search) {
        String next = "https://swapi.co/api/planets/?search={i}";
        
        WebTarget localTarget = client.target(next).resolveTemplate("i", search);
        
        List<StarWarsPlanet> planetResult = new ArrayList<>();
        
        while (next != null) {

            AllPlanets planets = localTarget.request(MediaType.APPLICATION_JSON).get(AllPlanets.class);

            try {
                next = planets.getNext();
                localTarget = client.target(next);
            } catch (NullPointerException e) {
                next = null;
            }
            
            planetResult.addAll(planets.getResults());

        }

        return planetResult;
    }
    
}
