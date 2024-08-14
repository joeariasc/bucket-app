package co.wawand.server.bucketapp.user;

import co.wawand.server.bucketapp.filestorage.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserIdGenerationService userIdGenerationService;
    private final S3Service s3Service;

    public UserService(UserRepository userRepository, UserIdGenerationService userIdGenerationService, S3Service s3Service) {
        this.userRepository = userRepository;
        this.userIdGenerationService = userIdGenerationService;
        this.s3Service = s3Service;
    }

    public String generateUser(String name, MultipartFile file) {
        try {
            Long userId = userIdGenerationService.newUserId();
            String keyName = "user_" + userId;
            s3Service.uploadFile(keyName, file);

            User user1 = User.builder()
                    .id(userId)
                    .name(name)
                    .profilePicture(keyName)
                    .build();
            userRepository.save(user1);

            return keyName;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving user profile" + e.getMessage());
            return null;
        }
    }

    public Object getUserProfile(String keyName) {
        return s3Service.getFile(keyName);
    }
}
