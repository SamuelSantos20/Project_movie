package com.br.sb.project_movie.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindJson {
    String value() default ""; // Default empty to use parameter name if not specified
}
