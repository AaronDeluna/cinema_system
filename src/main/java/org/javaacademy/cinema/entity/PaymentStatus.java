package org.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PAID(true),
    UNPAID(false);

    private final boolean status;

    public static PaymentStatus fromBoolean(boolean status) {
        for (PaymentStatus paymentStatus : values()) {
            if (paymentStatus.status == status) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}

