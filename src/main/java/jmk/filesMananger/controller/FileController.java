package jmk.filesMananger.controller;

import jmk.filesMananger.entity.FileInfo;
import jmk.filesMananger.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "file")
@AllArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam MultipartFile file){
        final FileInfo fileInfo = this.fileService.save(file);
        return ResponseEntity.status(HttpStatus.OK).body("The file is successfully saved as "+fileInfo.getName());

    }
    @GetMapping("/files")
    @ResponseStatus(value = HttpStatus.OK)
    public List<FileInfo> getAllFiles() {
        return fileService.getAllImages();
    }

    @GetMapping("/type-image/{filename:.+}")
    public ResponseEntity getImage(@PathVariable String filename){
        // Récupère le fichier demandé par son nom
        Resource file = this.fileService.loadImage(filename);

        // Renvoie l'en-tête Content-Disposition et le contenu du fichier
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/type-document/{filename:.+}")
    public ResponseEntity getDocument(@PathVariable String filename){
        // Récupère le fichier demandé par son nom
        Resource file = this.fileService.loadDocument(filename);
        // Renvoie l'en-tête Content-Disposition et le contenu du fichier
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }



}
