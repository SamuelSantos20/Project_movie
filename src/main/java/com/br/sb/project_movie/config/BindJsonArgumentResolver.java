package com.br.sb.project_movie.config;

import com.br.sb.project_movie.annotations.BindJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class BindJsonArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(BindJsonArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BindJson.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) throws Exception {

        StandardMultipartHttpServletRequest multipartRequest = webRequest.getNativeRequest(StandardMultipartHttpServletRequest.class);
        if (multipartRequest == null) {
            throw new IllegalArgumentException("Esta requisição não é um multipart request");
        }

        BindJson bindJson = parameter.getParameterAnnotation(BindJson.class);
        String partName = (bindJson != null && !bindJson.value().isEmpty()) ? bindJson.value() : parameter.getParameterName();
        logger.debug("Attempting to resolve part: {}", partName);

        List<Part> parts = multipartRequest.getParts().stream()
                .filter(part -> part.getName().equals(partName))
                .collect(Collectors.toList());

        if (parts.isEmpty()) {
            logger.error("No part found for name: {}", partName);
            throw new IllegalArgumentException("Parte '" + partName + "' não encontrada no multipart");
        }

        Part jsonPart = parts.get(0);
        logger.debug("Found part: {}, Content-Type: {}", partName, jsonPart.getContentType());

        String jsonContent;
        try {
            jsonContent = new String(jsonPart.getInputStream().readAllBytes());
            logger.debug("Raw JSON content: {}", jsonContent);
        } catch (IOException e) {
            logger.error("Failed to read part content: {}", e.getMessage());
            throw new IllegalArgumentException("Falha ao ler o conteúdo da parte '" + partName + "'");
        }

        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            logger.error("JSON content is empty for part: {}", partName);
            throw new IllegalArgumentException("Parte JSON '" + partName + "' está vazia");
        }

        return objectMapper.readValue(jsonContent, parameter.getParameterType());
    }
}