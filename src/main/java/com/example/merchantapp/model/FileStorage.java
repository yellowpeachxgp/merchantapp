package com.example.merchantapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_storage")
public class FileStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;     // 原文件名
    private String filePath;     // 在服务器上的存储路径
    private String contentType;  // 文件类型（MIME）
    private Long fileSize;       // 文件大小（字节）

    private LocalDateTime uploadTime;  // 上传时间
}
