package com.imt.adaptersinrest.clients.model.output;

import com.imt.adaptersinrest.common.model.output.AbstractOutput;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClientOutput extends AbstractOutput {
    private String id;
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private String adress;
}
