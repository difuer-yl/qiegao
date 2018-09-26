package club.qiegaoshijie.qiegao.command.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cmd
{
    int minArgs();

    String value();

    boolean onlyConsole() default false;

    boolean onlyPlayer() default false;

    String permission() default "qiegao.default" ;

    boolean status() default true;
}

