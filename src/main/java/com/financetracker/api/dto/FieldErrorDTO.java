package com.financetracker.api.dto;


import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class FieldErrorDTO {

    private String field;
    private String message;


}
