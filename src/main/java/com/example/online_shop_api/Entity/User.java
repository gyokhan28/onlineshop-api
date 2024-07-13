package com.example.online_shop_api.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(NON_DEFAULT) // reduce the size of the Json output for null/0 values.
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(min = 3, max = 40)
    private String firstName;
    @NotNull
    @Size(min = 3, max = 40)
    private String lastName;
    @NotNull
    @Size(min = 3, max = 30)
    private String username;
    @NotNull
    @Size(min = 10, max = 30)
    private String email;
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Date when employee was created/hired

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private String phoneNumber;

    private boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orderList;

}
