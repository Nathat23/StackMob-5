package uk.antiperson.stackmob.entity.traits;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TraitMetadata {
    String path();
}
