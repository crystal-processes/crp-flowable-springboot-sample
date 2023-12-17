package org.crp.flowable.springboot.sample.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Getter @Setter @AllArgsConstructor
public class Contract implements Serializable {
    @Serial
    private static final long serialVersionUID = -2948855108722785389L;

    private final String id;
    private final String account;

    private int maxAmount;
}
