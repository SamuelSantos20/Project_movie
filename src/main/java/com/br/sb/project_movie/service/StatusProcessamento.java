package com.br.sb.project_movie.service;

public enum StatusProcessamento {
    EM_PROCESSAMENTO("Em Processamento"),
    CONCLUIDO("Concluído"),
    ERRO("Erro");

    private final String descricao;

    StatusProcessamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}