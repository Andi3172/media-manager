package project.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import project.exceptions.InvalidLocationException;

public class LocationService {
    
    private final Path configFile;
    private final List<Path> monitoredLocations;

    public LocationService(String configFileName) {
        this.configFile = Paths.get(configFileName);
        this.monitoredLocations = new ArrayList<>();
    }

    public void loadLocations() throws InvalidLocationException, IOException{
        monitoredLocations.clear();
        if(!Files.exists(configFile)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(configFile)) {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()){
                    continue;
                }
                Path path = Paths.get(line.trim());

                if(!Files.isDirectory(path)){
                    throw new InvalidLocationException("Location from file is not a valid directory: " + path);
                }
                monitoredLocations.add(path);
            }
        }
    }

    public void saveLocations() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
            for(Path path : monitoredLocations){
                writer.write(path.toString());
                writer.newLine();
            }
        }
    }

    public void addLocation(Path path) throws InvalidLocationException {
        if(!Files.isDirectory(path)){
            throw new InvalidLocationException("Selected location is not a directory: " + path);
        }
        if(!monitoredLocations.contains(path)){
            monitoredLocations.add(path);
        }
    }

    public void removeLocation(String pathString){
        Path path = Paths.get(pathString);
        monitoredLocations.remove(path);
    }

    public List<Path> getMonitoredLocations() {
        return new ArrayList<>(monitoredLocations);
    }
    


}
