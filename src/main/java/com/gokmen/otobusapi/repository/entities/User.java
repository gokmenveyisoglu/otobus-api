package com.gokmen.otobusapi.repository.entities;

import com.gokmen.otobusapi.repository.record.User.ResponseUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    private String name;

    private String email;

    @Builder.Default
    private boolean active = true;

    public static ResponseUser toResponse(User user) {
        return new ResponseUser(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.isActive()
        );
    }
}
