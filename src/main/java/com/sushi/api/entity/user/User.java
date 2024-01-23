package com.sushi.api.entity.user;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.DatabaseTableNames;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.role.Role;
import com.sushi.api.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.USER + " SET deleted = 'T' WHERE id = ?", check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'F'")
@Table(name = DatabaseTableNames.USER, indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String            uuid;

    @Column(name = "first_name", nullable = true)
    private String            firstName;

    @Column(name = "last_name", nullable = true)
    private String            lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String            email;

    @Column(name = "password", nullable = false)
    private String            password;

    @Column(name = "phone_number", nullable = true)
    private String            phoneNumber;

    @Column(name = "dob", nullable = true)
    private LocalDate         dob;

    @JsonIgnoreProperties(value = {"users"})
    @JoinColumn(name = "account_id", nullable = false)
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Account           account;

    @Column(name = "deleted", nullable = false)
    private boolean           deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus        status;

    @JsonIgnoreProperties(value = {"user"})
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Address           address;

    @JsonIgnoreProperties(value = {"users"})
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = {@JoinColumn(name = "parent_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role>         roles;

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public String getRoleAsString() {
        if (this.roles == null) {
            return null;
        }
        return this.roles.stream().findFirst().get().getUserType().name();
    }

    public boolean isActive() {
        return Optional.ofNullable(this.status).orElse(UserStatus.NONE).equals(UserStatus.ACTIVE);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id).append(this.uuid).toHashCode();

        // return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        User other = (User) obj;
        return new EqualsBuilder().append(this.id, other.id).append(this.uuid, other.uuid).isEquals();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return ToStringBuilder.reflectionToString(this);
    }

    public String toJson() {
        return ObjectUtils.toJson(this);
    }

    @PrePersist
    private void preCreate() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            this.uuid = "user-" + UUID.randomUUID().toString();
        }

    }

    public String getFullName() {
        StringBuilder fullname = new StringBuilder();

        if (this.firstName != null && !this.firstName.isEmpty()) {
            fullname.append(this.firstName);
        }

        if (this.lastName != null && !this.lastName.isEmpty()) {
            if (fullname.toString().isEmpty()) {
                fullname.append(this.lastName);
            } else {
                fullname.append(" " + this.lastName);
            }
        }

        return fullname.toString();
    }

}
