package domain.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) //мета-аннотация (аннотация для аннотаций), указывает, где можно применять
//нашу аннотацию
@Retention(RetentionPolicy.RUNTIME) //указывает, до какого этапа "доживет" эта аннотация
// эьа аннотация будет сохранения в байт-код, и доступна во время выполнения программы (через рефлексию)
@AuthenticationPrincipal //аннотация из спринг секьюрити, "в этот параметр метода нужно будет подставить
//объект Principal (текущ пользователь) из контекста безопасности
public @interface CurrentAccount {
    boolean required() default true;
//    Этот атрибут дублирует функциональность @AuthenticationPrincipal(required = ...).
//    Если required = true (по умолчанию), а пользователь не авторизован — Spring выбросит исключение.
//    Если required = false, и пользователя нет — в параметр придет null (и вы сами обработаете этот случай).


}
