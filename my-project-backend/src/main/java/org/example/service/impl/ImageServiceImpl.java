package org.example.service.impl;

import org.example.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public String uploadAvatar(MultipartFile file, int id) {
        String imageName = UUID.randomUUID().toString().replace("-","");
        imageName = "/avatar/"+imageName;

    }
}
