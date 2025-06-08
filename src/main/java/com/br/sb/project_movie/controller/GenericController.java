package com.br.sb.project_movie.controller;

import org.springframework.http.HttpHeaders;

import java.net.URI;

public interface GenericController {
    default HttpHeaders gerarHaderLoccation(String resourceUri) {
        URI uri = URI.create(resourceUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return headers;
    }
}
