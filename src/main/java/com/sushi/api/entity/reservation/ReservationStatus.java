package com.sushi.api.entity.reservation;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ReservationStatus {

  WAITLIST, RESERVED, CHECKED_IN, CHECKED_OUT, NO_SHOW;
}
