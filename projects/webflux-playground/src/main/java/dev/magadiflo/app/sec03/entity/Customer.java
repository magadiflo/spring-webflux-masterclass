package dev.magadiflo.app.sec03.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/*
No tenemos @Entity en R2DBC.
@Table y @Column no son realmente necesarios aquí, pero si queremos personalizar un poco sí podríamos agregarlos.
 */
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
