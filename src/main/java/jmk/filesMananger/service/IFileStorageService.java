package jmk.filesMananger.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IFileStorageService {

    void save(MultipartFile file);
    Resource load(String filename);
    Stream<Path> getAllImages();


}
