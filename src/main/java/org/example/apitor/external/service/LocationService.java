package org.example.apitor.external.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocationService {
    private final ResourceLoader resourceLoader;
    private DatabaseReader dbReader;

    public LocationService(ResourceLoader resourceLoader){
        this.resourceLoader=resourceLoader;
    }

    @PostConstruct
    public void init(){
        try{
            Resource resource = resourceLoader.getResource("classpath:GeioLite2-City.mmdb");
            try(InputStream dbStream = resource.getInputStream()){
                this.dbReader = new DatabaseReader.Builder(dbStream).build();
            }
        }catch(Exception ex){
            System.err.println("MaxMind DB could not be downloaded");
        }
    }

    public Map<String, String> resolveLocationFromIp(String ipAddress) {
        Map<String, String> location =  new HashMap<>();
        location.put("country", "Unknown");
        location.put("city", "Unknown");
        location.put("latitude", "400.0");
        location.put("longitude", "400.0");
        location.put("pincode", "999999");

        try{
            InetAddress ip = InetAddress.getByName(ipAddress);
            if(ip.equals(InetAddress.getLoopbackAddress())){
                location.put("country", "Localhost");
                location.put("city", "Development Environment");
                return location;
            }
            CityResponse response= dbReader.city(ip);
            if(response.getCountry()!=null){
                location.put("country", response.getCountry().getName());
            }
            if(response.getCity()!=null){
                location.put("city", response.getCity().getName());
            }
            if(response.getLocation()!=null){
                location.put("latitude", String.valueOf(response.getLocation().getLatitude()));
                location.put("longitude", String.valueOf(response.getLocation().getLongitude()));
            }
            if(response.getPostal()!=null){
                location.put("pincode", response.getPostal().getCode());
            }

        }catch (Exception ex){
            System.err.println("Invalid IP or unable to find, falling to default");
        }
        return location;

    }
}
