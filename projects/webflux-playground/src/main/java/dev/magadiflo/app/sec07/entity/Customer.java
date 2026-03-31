package dev.magadiflo.app.sec07.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "customers")
public class Customer {
    @Id
    private Long id;
    private String name;
    private String email;
}
