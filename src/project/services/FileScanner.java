package project.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import project.models.MediaFile;

/**
 * Utility that scans a list of filesystem locations and collects media files
 * matching a set of supported extensions.
 * <p>
 * The scanner walks directory trees rooted at the provided locations and
 * produces a list of {@link project.models.MediaFile} instances containing
 * basic metadata (name, path, size). Errors encountered while reading a
 * particular file or location are logged to {@code System.err} and do not
 * abort the entire scan.
 */
public class FileScanner {

    private final String[] supportedExtensions;
    
    /**
     * Create a new FileScanner.
     *
     * @param supportedExtensions array of lowercase file extensions to accept (e.g. ".mp3", ".jpg")
     */
    public FileScanner(String[] supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }

    /**
     * Scan the provided locations for files matching the configured
     * {@code supportedExtensions}.
     * <p>
     * The method will recursively walk each provided location. For each file
     * that matches the extension filters a {@link MediaFile} is created and
     * added to the returned list. I/O errors for individual files or
     * directories are printed to {@code System.err} but do not stop the scan.
     *
     * @param locations list of directory roots to scan
     * @return list of discovered media files (empty list if none found)
     */
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

    /**
     * Check whether the given path has one of the supported file extensions.
     *
     * @param path file path to check
     * @return {@code true} when the file's name ends with a supported extension
     */
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
