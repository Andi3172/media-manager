package project.models;
import java.nio.file.Path;

public class MediaFile {
    private String name;
    private Path path;
    private long sizeInBytes;

    public MediaFile(String name, Path path, long sizeInBytes) {
        this.name = name;
        this.path = path;
        this.sizeInBytes = sizeInBytes;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "name='" + name + '\'' +
                ", path=" + path +
                '}';
    }

    
    
}
