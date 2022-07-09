package com.photory.config.resolver;

import com.photory.common.exception.model.UnAuthorizedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserEmailResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserEmail.class) && String.class.equals(parameter.getParameterType());
    }

    @NotNull
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object object = webRequest.getAttribute("user", 0);
        if (object == null) {
            throw new UnAuthorizedException("토큰이 없습니다.");
        }
        return object;
    }
}
