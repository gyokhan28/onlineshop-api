package com.example.online_shop_api.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_types")
@Builder
public class JobType {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
}
