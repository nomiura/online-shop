package domain.resolver;

import domain.annotation.CurrentAccount;
import domain.entity.Account;
import domain.exception.AccessDeniedException;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


// HandlerMethodArgumentResolver - это интерфейс Spring, который позволяет перехватывать
// и обрабатывать параметры методов контроллеров до того, как будет вызван сам метод.
//
//1. Кастомная обработка параметров - вы можете сами решить, откуда брать значение для параметра
//2. Упрощение кода контроллеров - вместо того чтобы каждый раз писать SecurityContextHolder.getContext()
// .getAuthentication() в контроллере, вы делаете это один раз в резолвере
//3. Централизованная логика - вся логика получения текущего пользователя находится в одном месте
@Component
public class CurrentAccountArgumentResolver implements HandlerMethodArgumentResolver {

//    Метод-предикат. Spring вызывает его для каждого параметра каждого метода в каждом контроллере.
//    Должен вернуть true, если этот резолвер может обработать данный параметр.
//    Проверяет, есть ли на параметре аннотация @CurrentAccount.
//    Если аннотация есть → true, если нет → false.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentAccount.class) != null
                && parameter.getParameterType().equals(Account.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, //метаданные о параметры(можно получить тип аннотации итд)
                                  @Nullable ModelAndViewContainer mavContainer, //контейнер для ModelAndView
                                  // (редко используется в REST)
                                  NativeWebRequest webRequest, //доступ к http-запросу(headers, parameters итд)
                                  @Nullable WebDataBinderFactory binderFactory) //фабрика для привязки данных
            throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //получ текущ аутетив из спринг секьюрити
                //SecurityContextHolder хранит информацию о текущем пользователе в ThreadLocal (привязано к текущему потоку)


        if (authentication == null || !(authentication.getPrincipal() instanceof Account)) {
            throw new AccessDeniedException("User is not authenticated");
            // или throw new UnauthorizedException("Authentication required");
        }
        return authentication.getPrincipal();
    }
}
//    1. Приходит HTTP-запрос
//   ↓
//           2. DispatcherServlet получает запрос
//   ↓
//           3. Находит соответствующий метод контроллера
//   ↓
//           4. Для каждого параметра метода вызывает supportsParameter() у всех резолверов
//   ↓
//           5. Для первого резолвера, вернувшего true, вызывает resolveArgument()
//   ↓
//           6. Передает полученное значение в метод контроллера
//   ↓
//           7. Выполняется метод контроллера