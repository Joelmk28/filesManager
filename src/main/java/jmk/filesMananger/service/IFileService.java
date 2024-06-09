package jmk.filesMananger.service;

import jmk.filesMananger.entity.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface IFileService {

    FileInfo save(MultipartFile file);
    Resource loadImage(String filename);
    Resource loadDocument(String filename);
   // Stream<Path> getAllImages();
    List<FileInfo> getAllImages();


}
