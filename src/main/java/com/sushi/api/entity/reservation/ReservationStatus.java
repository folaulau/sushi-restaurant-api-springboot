package com.sushi.api.entity.reservation;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ReservationStatus {

  WAITLIST, RESERVED, CHECK_IN, CHECK_OUT, NO_SHOW;
}
