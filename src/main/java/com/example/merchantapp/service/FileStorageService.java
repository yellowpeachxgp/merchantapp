package com.example.merchantapp.service;

import com.example.merchantapp.model.FileStorage;
import com.example.merchantapp.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Autowired
    private FileStorageRepository fileStorageRepository;

    // 保存文件到本地并记录数据库
    public FileStorage storeFile(MultipartFile file) throws IOException {
        // 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成唯一文件名，避免冲突
        String originalFilename = file.getOriginalFilename();
        String fileExt = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + fileExt;
        // 保存文件到磁盘
        File dest = new File(dir, newFilename);
        file.transferTo(dest);
        // 保存文件信息到数据库
        FileStorage fileRecord = new FileStorage();
        fileRecord.setFileName(originalFilename);
        fileRecord.setFilePath(dest.getAbsolutePath());
        fileRecord.setContentType(file.getContentType());
        fileRecord.setFileSize(file.getSize());
        fileRecord.setUploadTime(LocalDateTime.now());
        return fileStorageRepository.save(fileRecord);
    }

    // 根据ID获取文件记录
    public FileStorage getFile(Long id) {
        return fileStorageRepository.findById(id).orElse(null);
    }

    // 将文件作为资源加载（用于文件下载）
    public Resource loadFileAsResource(Long id) throws MalformedURLException {
        FileStorage fileInfo = fileStorageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
        return new UrlResource(Paths.get(fileInfo.getFilePath()).toUri());
    }
}
