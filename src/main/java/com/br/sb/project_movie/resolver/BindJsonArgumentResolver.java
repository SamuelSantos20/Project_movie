package com.br.sb.project_movie.resolver;

import com.br.sb.project_movie.annotations.BindJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class BindJsonArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BindJson.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory)
            throws Exception {

        String paramName = parameter.getParameterName();
        String value = webRequest.getParameter(paramName);

        if (value == null) {
            throw new IllegalArgumentException("Campo '" + paramName + "' não encontrado na requisição multipart");
        }

        return objectMapper.readValue(value, parameter.getParameterType());
    }
}
