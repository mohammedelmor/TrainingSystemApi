package org.elmor.trainingsystemapi.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetails {
    private String message;
    private String detailedMessage;
}
