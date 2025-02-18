package com.montreal.oauth.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20)
    @Column(name = "username")
    private String username;

    @Size(max = 50)
    @Column(name = "email")
    private String email;

    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @Size(max = 120)
    @Column(name = "fullname")
    private String fullName;

    @Size(max = 120)
    @Column(name = "cpf")
    private String cpf;

    @Size(max = 120)
    @Column(name = "phone")
    private String phone;

    @Size(max = 250)
    @Column(name = "company_id")
    private String companyId;

    @Size(max = 1000)
    @Column(name = "link")
    private String link;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "token_temporary")
    private String tokenTemporary;

    @Column(name = "token_expired_at")
    private LocalDateTime tokenExpiredAt;

    private boolean isReset;
    private Timestamp resetAt;
    private boolean isEnabled;
    private boolean isCreatedByAdmin;
    private boolean isPasswordChangedByUser;

    public boolean isCreatedByAdmin() {
        return this.isCreatedByAdmin;
    }

    public void setCreatedByAdmin(boolean isCreatedByAdmin) {
        this.isCreatedByAdmin = isCreatedByAdmin;
    }

    public boolean isPasswordChangedByUser() {
        return this.isPasswordChangedByUser;
    }

    public void setPasswordChangedByUser(boolean isPasswordChangedByUser) {
        this.isPasswordChangedByUser = isPasswordChangedByUser;
    }

}
