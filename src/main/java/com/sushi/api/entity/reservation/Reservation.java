package com.sushi.api.entity.reservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
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
import com.sushi.api.entity.order.OrderStatus;
import com.sushi.api.entity.role.Role;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserStatus;
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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.RESERVATION + " SET deleted = true WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = false")
@Table(name = DatabaseTableNames.RESERVATION,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Reservation implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

  @Column(name = "number_of_people", nullable = false)
  private Integer numberOfPeople;

  @Column(name = "reserved_at", nullable = true)
  private LocalDateTime reservedAt;

  @Column(name = "checked_in_time", nullable = true)
  private LocalDateTime checkedInTime;

  @Column(name = "checked_out_time", nullable = true)
  private LocalDateTime checkedOutTime;

  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ReservationStatus status;

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id).append(this.uuid).toHashCode();
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
    Reservation other = (Reservation) obj;
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
      this.uuid = "reservation-" + UUID.randomUUID().toString();
    }

    if (this.status == null) {
      this.status = ReservationStatus.RESERVED;
    }

  }
}
