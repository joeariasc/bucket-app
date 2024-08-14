package co.wawand.server.bucketapp.user.web;

import co.wawand.server.bucketapp.filestorage.S3Service;
import co.wawand.server.bucketapp.user.UserService;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String health() {
        return "UP";
    }

    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name
    ) {
        if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE)) {
            return "Only PNG files are allowed";
        }

        String keyName = userService.generateUser(name, file);
        if (keyName != null) {
            return "User profile id: " + keyName;
        } else return "Failed to upload file";
    }

    @GetMapping("/view/{keyName}")
    public ResponseEntity<?> viewFile(@PathVariable String keyName) {
        var userProfile = (S3Object) userService.getUserProfile(keyName);

        if (userProfile == null || userProfile.getObjectContent() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        var content = userProfile.getObjectContent();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + keyName + "\"")
                .body(new InputStreamResource(content));
    }

    @GetMapping("/download/{keyName}")
    public ResponseEntity<?> downloadFile(@PathVariable String keyName) {
        var userProfile = (S3Object) userService.getUserProfile(keyName);

        if (userProfile == null || userProfile.getObjectContent() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        }

        var content = userProfile.getObjectContent();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(content));
    }
}
