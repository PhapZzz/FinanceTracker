package com.financetracker.api.entity;

import com.financetracker.api.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;


    // mối quan hệ 1 chiều nên không khai báo one to many
    // khi nào mà muốn biết 1 role có bao nhiêu user thì làm chiều còn lại

//    @OneToMany(mappedBy = "role")
//    private List<User> users;

}

