package com.xiao.tmall.util;

import org.springframework.web.multipart.MultipartFile;

public class UploadedImageFile {
    MultipartFile image;

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public MultipartFile getImage() {
        return image;
    }
}
