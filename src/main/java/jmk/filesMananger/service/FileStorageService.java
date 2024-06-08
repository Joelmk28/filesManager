package jmk.filesMananger.service;

import jmk.filesMananger.controller.FileController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
@Service
public class FileStorageService implements IFileStorageService {

    //initialisation du repertoire ( existant ou pas )
    private final Path root =Paths.get("uploads");
    private final Path imagePath = Paths.get("uploads/images");
    private final Path documentPath = Paths.get("uploads/documents");
    private final Path zipPath = Paths.get("uploads/zip");


    public void init() {
        try{
            //si le repertoire n'existe pas on en cree
            Files.createDirectories(root);
            Files.createDirectories(imagePath);
            Files.createDirectories(documentPath);
            Files.createDirectories(zipPath);
        }
        catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void save(MultipartFile file) {

        this.init();
       try {
           if(file.isEmpty()){
               throw new RuntimeException("Please insert the file");
           }
           //lecture des contenus du fichier (on octes)
           final InputStream inputStream = file.getInputStream();
           //recupation de l'extension
           final String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
           System.out.println("*****************************\n " +
                   "Type: "+file.getContentType()+"\n"+
                   "Resource : "+file.getResource()+"\n"+
                   "Name: "+file.getName()+"\n"+
                   "Original Name: "+file.getOriginalFilename()+"\n"+
                   "Taille: "+file.getSize()+"\n"+
                   "Extension: "+ extension +"\n"+
                   "**************************************");

           //determination de les extenstions
           List<String>imageExtensions = Arrays.asList("png","jpg","jpeg");
           List<String>documentExtensions = Arrays.asList("pdf","msword","doc","zip");


           String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
           String randomId = UUID.randomUUID().toString().substring(0, 8);


           String uniqueFileName = timestamp + "-" + randomId + "."+extension;
           //definition chemin du fichier complet où le fichier téléchargé sera enregistré
          if(imageExtensions.stream().anyMatch(s -> s.equalsIgnoreCase(extension))){
               final Path imagePath = this.imagePath.resolve(uniqueFileName);
               Files.copy(inputStream,imagePath);
              String url = MvcUriComponentsBuilder
                      .fromMethodName(FileController.class, "getFile",imagePath .getFileName().toString())
                      .build().toString();
               return;
           }

          if(documentExtensions.stream().anyMatch(s -> s.equalsIgnoreCase(extension))){
              final Path documentPath = this.documentPath.resolve(uniqueFileName);
              Files.copy(inputStream,documentPath);
              return;
          }

           final Path path = this.root.resolve(uniqueFileName);
           //enregistrement du fichier
           Files.copy(inputStream, path);


       }
       catch (IOException e){
           throw new RuntimeException(e.getMessage());
       }

    }


    @Override
    public Resource load(String filename) {
        try{
            //definition du chemin
            Path imageFile = this.imagePath.resolve(filename);
            //covertion du chemin en url
            Resource resource = new UrlResource(imageFile.toUri());
            //vérifie si le fichier existe et s'il est lisible
            if(resource.exists()||resource.isReadable()){
                return resource;
            }
            else{
                throw new RuntimeException("Vous ne pouvez pas lire ce fichier");
            }
        }
        catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }

    }


    @Override
    public Stream<Path> getAllImages(){
        try {
            return //parcourir le repertoire que l'on defini avec une profondeur d'1 niveau
                    Files.walk(this.imagePath,1)
                            //on verifie si le chemin trouvé correspond à celui defini et on l'exclu
                    .filter(path -> !path.equals(this.imagePath))
                            //recuperation du chemin relative du fichier du repertoire
                    .map(this.imagePath::relativize);
        } catch (IOException e) {
          throw new RuntimeException(e.getMessage());
        }

    }
}
