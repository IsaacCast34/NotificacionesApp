package com.example.notificacionesapp.data.categories

object CategoryManager {
    private val defaultCategories = listOf(
        "General",
        "Trabajo",
        "Personal",
        "Estudio",
        "Compras",
        "Salud",
        "Finanzas",
        "Proyectos"
    )

    private val userCategories = mutableSetOf<String>()

    fun getCategories(): List<String> {
        return (defaultCategories + userCategories).sorted()
    }

    fun addCategory(category: String) {
        if (category.isNotBlank()) {
            userCategories.add(category)
        }
    }

    fun parseTags(tagsString: String): List<String> {
        return tagsString.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun formatTags(tags: List<String>): String {
        return tags.joinToString(", ")
    }
}