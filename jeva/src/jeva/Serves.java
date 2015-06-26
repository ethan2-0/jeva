package jeva;

import java.lang.annotation.*;

/**
 * Used to indicate that a method is the handler for a given path.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Serves {
	String path();
}
