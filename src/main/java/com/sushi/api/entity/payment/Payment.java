package com.sushi.api.entity.payment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.DatabaseTableNames;
import com.sushi.api.entity.order.paymentmethod.OrderPaymentMethod;
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
@SQLDelete(sql = "UPDATE " + DatabaseTableNames.PAYMENT + " SET deleted = true WHERE id = ?",
    check = ResultCheckStyle.NONE)
@Where(clause = "deleted = false")
@Table(name = DatabaseTableNames.PAYMENT,
    indexes = {@Index(columnList = "uuid"), @Index(columnList = "deleted")})
public class Payment implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private Long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private String uuid;


  @Column(name = "type")
  private String type;

  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "paid")
  private boolean paid;

  @Column(name = "description")
  private String description;

  @Column(name = "amount_paid")
  private Double amountPaid;

  @Column(name = "stripe_charge_id")
  private String stripeChargeId;

  @Embedded
  private OrderPaymentMethod paymentMethod;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;


  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  private void prePersist() {
    
    if (this.uuid == null || this.uuid.isEmpty()) {
      this.uuid = "payment-" + UUID.randomUUID().toString();
    }
  }


}
