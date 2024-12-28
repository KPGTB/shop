package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.serivce.iface.IS3Service;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired private IS3Service s3Service;
    @Autowired private Properties properties;

    @PutMapping("/uploadImage")
    public JsonResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String[] nameElements = file.getOriginalFilename().split("\\.");
        String ext = nameElements[nameElements.length-1];
        String newName = "upload_" + System.currentTimeMillis() + "." + ext;

        try {
            s3Service.uploadFile(file.getBytes(), newName, this.properties.getS3PublicBucket());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }
        return new JsonResponse<>(HttpStatus.OK, "Uploaded", this.properties.getS3PublicUrl() + "/" + newName);
    }

    @PutMapping("/uploadFile")
    public JsonResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String[] nameElements = file.getOriginalFilename().split("\\.");
        String ext = nameElements[nameElements.length-1];
        String newName = "upload_" + System.currentTimeMillis() + "." + ext;

        try {
            s3Service.uploadFile(file.getBytes(), newName, this.properties.getS3PrivateBucket());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }
        return new JsonResponse<>(HttpStatus.OK, "Uploaded", newName);
    }
}
