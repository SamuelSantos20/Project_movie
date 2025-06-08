package com.br.sb.project_movie.model;

public enum Genero {
    ACAO("Ação"),
    AVENTURA("Aventura"),
    COMEDIA("Comédia"),
    DRAMA("Drama"),
    FICCAO_CIENTIFICA("Ficção Científica"),
    ROMANCE("Romance"),
    TERROR("Terror"),
    SUSPENSE("Suspense"),
    ANIMACAO("Animação"),
    DOCUMENTARIO("Documentário"),
    FANTASIA("Fantasia"),
    MUSICAL("Musical"),
    POLICIAL("Policial"),
    GUERRA("Guerra"),
    WESTERN("Western");

    private final String descricao;

    Genero(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
