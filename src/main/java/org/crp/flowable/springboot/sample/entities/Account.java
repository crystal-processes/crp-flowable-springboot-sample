package org.crp.flowable.springboot.sample.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Getter @Setter
@AllArgsConstructor
public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = -1524895032576145883L;

    private final String owner;
    private final String id;
}
