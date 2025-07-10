package ar.edu.unlam.mobile.scaffolding.data.model

enum class SortCriterion {
    NONE, // Podría ser el estado por defecto o "Por Defecto" (orden del repositorio)
    NAME_ASC,
    NAME_DESC,
    RATING_ASC,
    RATING_DESC,
    DIFFICULTY_ASC, // Asumiendo que dificultad puede ser ordenada (ej: Fácil < Media < Difícil)
    DIFFICULTY_DESC,
}
