package project.services;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import project.models.MediaFile;

public class FileScanner {

    private final String[] supportedExtensions;
    
    public FileScanner(String[] supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }

    public List<MediaFile> scanLocations(List<Path> locations){
        List<MediaFile> discoveredFiles = new ArrayList<>();

        for(Path location: locations){
            try(Stream<Path> stream = Files.walk(location)){
                stream
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedFile)
                    .forEach(path -> {
                        try{
                            MediaFile mf = new MediaFile(
                                path.getFileName().toString(),
                                path,
                                Files.size(path)
                            );
                            discoveredFiles.add(mf);
                        } catch (IOException e) {
                            System.err.println("Could not get size for file: " + path + " due to " + e.getMessage());
                        }
                    });

            } catch(IOException e){
                System.err.println("Error scanning location: " + location + " due to " + e.getMessage());
            }
        }
        return discoveredFiles;
    }

    private boolean isSupportedFile(Path path){
        String fileName = path.getFileName().toString().toLowerCase();
        for(String ext : supportedExtensions){
            if(fileName.endsWith(ext)){
                return true;
            }
        }
        return false;
    }
}
