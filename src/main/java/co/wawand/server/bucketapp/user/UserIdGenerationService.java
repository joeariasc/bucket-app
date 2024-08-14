package co.wawand.server.bucketapp.user;

import org.springframework.stereotype.Service;

@Service
public class UserIdGenerationService {
    public Long newUserId() {
        return System.nanoTime();
    }
}
