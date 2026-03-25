package ru.otus.annotation;

import ru.otus.model.AuditActionEnum;
import ru.otus.model.AuditEntityTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    AuditActionEnum action();

    AuditEntityTypeEnum entity();

    String idFieldName() default "";

}