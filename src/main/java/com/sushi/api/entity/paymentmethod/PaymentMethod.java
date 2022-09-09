package com.sushi.api.entity.paymentmethod;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.InvalidMappingException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.DatabaseTableNames;
import com.sushi.api.entity.order.paymentmethod.OrderPaymentMethod;
import com.sushi.api.entity.payment.Payment;
import com.sushi.api.entity.user.User;
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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.PAYMEN_METHOD + " SET deleted = 'true' WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = 'false'")
@Table(name = DatabaseTableNames.PAYMEN_METHOD,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class PaymentMethod implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;


  @Column(name = "type", updatable = false, nullable = false)
  private String type;

  @Column(name = "name")
  private String name;

  @Column(name = "last4")
  private String last4;

  @Column(name = "position")
  private Integer position;

  @Column(name = "brand")
  private String brand;

  @Column(name = "source_token")
  private String sourceToken;

  @Column(name = "payment_gateway_id")
  private String paymentGatewayId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;


  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.id).append(this.uuid).append(this.user)
        .toHashCode();
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
    PaymentMethod other = (PaymentMethod) obj;
    return new EqualsBuilder().append(this.id, other.id).append(this.uuid, other.uuid)
        .append(this.user, other.user).isEquals();
  }

  @PrePersist
  @PreUpdate
  private void preCreateUpdate() {
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "payment-method-" + UUID.randomUUID().toString();
    }
  }

}
