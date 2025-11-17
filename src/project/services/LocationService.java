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


/**
 * Manages the reading and writing of monitored locations to a configuration file.
 * <p>
 * This service is responsible for persisting a list of directories to monitor
 * to a simple text file (one path per line) and exposing the current set of
 * monitored locations to callers. It performs basic validation to ensure only
 * directories are accepted.
 *
 * <p>Thread-safety: this implementation is not synchronized. Callers should
 * ensure calls are performed from a single thread or provide their own
 * synchronization if accessed concurrently.
 *
 * @author Andrei Maican
 * @version 1.0
 */
public class LocationService {
    
    /** Path to the configuration file that stores monitored locations. */
    private final Path configFile;

    /** Internal list of monitored directory paths. */
    private final List<Path> monitoredLocations;

    /**
     * Create a new LocationService that uses the given filename for storage.
     *
     * @param configFileName filename (relative or absolute) used to persist monitored locations
     */
    public LocationService(String configFileName) {
        this.configFile = Paths.get(configFileName);
        this.monitoredLocations = new ArrayList<>();
    }

    /**
     * Load monitored locations from the configured file into memory.
     * <p>
     * Each non-empty line in the file is interpreted as a path. If a path is
     * present but not a directory an {@link InvalidLocationException} is thrown.
     * If the file does not exist, this method leaves the internal list empty.
     *
     * @throws InvalidLocationException when a stored path is not a directory
     * @throws IOException if an I/O error occurs while reading the file
     */
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

    /**
     * Persist the currently configured monitored locations to the config file.
     *
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void saveLocations() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
            for(Path path : monitoredLocations){
                writer.write(path.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Add a new location to the list of monitored directories.
     *
     * @param path the directory to add
     * @throws InvalidLocationException if the provided path is not a directory
     */
    public void addLocation(Path path) throws InvalidLocationException {
        if(!Files.isDirectory(path)){
            throw new InvalidLocationException("Selected location is not a directory: " + path);
        }
        if(!monitoredLocations.contains(path)){
            monitoredLocations.add(path);
        }
    }

    /**
     * Remove a monitored location by its string representation. If the path
     * is not present nothing happens.
     *
     * @param pathString the string form of the path to remove
     */
    public void removeLocation(String pathString){
        Path path = Paths.get(pathString);
        monitoredLocations.remove(path);
    }

    /**
     * Return a copy of the currently monitored locations.
     *
     * @return a new list containing monitored directory paths
     */
    public List<Path> getMonitoredLocations() {
        return new ArrayList<>(monitoredLocations);
    }
    


}
