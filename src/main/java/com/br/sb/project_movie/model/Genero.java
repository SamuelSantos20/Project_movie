package com.br.sb.project_movie.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genero {
    ACAO("Ação", "Action"),
    AVENTURA("Aventura", "Adventure"),
    COMEDIA("Comédia", "Comedy"),
    DRAMA("Drama", "Drama"),
    FICCAO_CIENTIFICA("Ficção Científica", "Science Fiction"),
    ROMANCE("Romance", "Romance"),
    TERROR("Terror", "Horror"),
    SUSPENSE("Suspense", "Thriller"),
    ANIMACAO("Animação", "Animation"),
    DOCUMENTARIO("Documentário", "Documentary"),
    FANTASIA("Fantasia", "Fantasy"),
    MUSICAL("Musical", "Musical"),
    POLICIAL("Policial", "Crime"),
    GUERRA("Guerra", "War"),
    WESTERN("Faroeste", "Western"),
    BIOGRAFIA("Biografia", "Biography"),
    HISTORIA("Histórico", "History"),
    FAMILIA("Família", "Family"),
    MISTERIO("Mistério", "Mystery"),
    CRIME("Crime", "Crime"),
    REALITY_SHOW("Reality Show", "Reality"),
    FICCAO("Ficção", "Fiction"),
    EPOPEIA("Epopéia", "Epic"),
    SUPER_HEROI("Super-herói", "Superhero");

    private final String descricao;
    private final String englishName;

    Genero(String descricao, String englishName) {
        this.descricao = descricao;
        this.englishName = englishName;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static Genero fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (Genero genero : values()) {
            if (genero.descricao.equalsIgnoreCase(value) || genero.englishName.equalsIgnoreCase(value)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + value);
    }
}
