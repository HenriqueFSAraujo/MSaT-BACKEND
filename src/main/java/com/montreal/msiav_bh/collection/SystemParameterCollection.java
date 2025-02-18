package com.montreal.msiav_bh.collection;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "system_parameter")
public class SystemParameterCollection {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment")
    private Long id;

    private String system;
    private String parameter;
    private String value;

}
