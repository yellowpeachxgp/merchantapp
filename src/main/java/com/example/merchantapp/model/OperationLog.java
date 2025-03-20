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
@Table(name = "operation_logs")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 操作的用户，多对一
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String action;    // 操作名称或方法

    @Column(length = 500)
    private String details;   // 操作详情或结果

    private LocalDateTime timestamp;  // 操作时间
}
