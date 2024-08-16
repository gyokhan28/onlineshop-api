package com.example.online_shop_api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_types")
public class JobType {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
}
