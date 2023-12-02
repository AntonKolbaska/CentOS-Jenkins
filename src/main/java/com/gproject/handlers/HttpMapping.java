package com.gproject.handlers;

import com.gproject.annotations.HttpMethod;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class HttpMapping {
    //store URL
    private final String path;
    //store HTTP-method
    private final HttpMethod method;


}
