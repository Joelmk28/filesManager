package jmk.filesMananger.controller;

import jmk.filesMananger.entity.FileInfo;
import jmk.filesMananger.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "file")
@AllArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam MultipartFile file){
        this.fileStorageService.save(file);
        return ResponseEntity.status(HttpStatus.OK).body(file.getOriginalFilename()+" file is saved successfully");

    }
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getAllFiles() {
        // Récupère une liste de tous les chemins de fichiers stockés
        List<FileInfo> fileInfoList = this.fileStorageService.getAllImages().map(path -> {
            // Extrait le nom de fichier à partir du chemin
            String fileName = path.getFileName().toString();

            // Construit l'URL pour accéder au fichier
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FileController.class, "getFile", path.getFileName().toString())
                    .build().toString();

            // Crée un objet FileInfo avec le nom et l'URL du fichier
            return new FileInfo(fileName,url);
        }).collect(Collectors.toList());

        // Renvoie la liste des Urls et leurs nom
        return ResponseEntity.status(HttpStatus.OK).body(fileInfoList);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity getFile(@PathVariable String filename) {
        // Récupère le fichier demandé par son nom
        Resource file = this.fileStorageService.load(filename);

        // Renvoie l'en-tête Content-Disposition et le contenu du fichier
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


}
