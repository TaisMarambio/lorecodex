package com.lorecodex.backend.model;

public enum ChallengeDifficulty {
    SUPER_FACIL(1), FACIL(2), NORMAL(3), DIFICIL(4), SUPER_DIFICIL(5), EXTREMO(6);

    private final int level;

    ChallengeDifficulty(int level) { this.level = level; }

    public static ChallengeDifficulty fromLevel(int level) {
        for (ChallengeDifficulty d : values()) {
            if (d.level == level) return d;
        }
        throw new IllegalArgumentException("Nivel de dificultad inválido: " + level);
    }
}