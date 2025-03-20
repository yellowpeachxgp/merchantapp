package com.example.merchantapp.controller;

import com.example.merchantapp.model.FileStorage;
import com.example.merchantapp.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<FileStorage> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileStorage saved = fileStorageService.storeFile(file);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
        FileStorage fileInfo = fileStorageService.getFile(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = fileStorageService.loadFileAsResource(id);
        // 设置响应头，Content-Type 和 Content-Disposition（附件下载形式）
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(
                        fileInfo.getContentType() != null ? fileInfo.getContentType() : "application/octet-stream"))
                .body(resource);
    }
}
