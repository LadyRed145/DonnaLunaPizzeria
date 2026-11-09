package servicios

/** Lista miembros de clases del dominio usando reflection (sin dependencias externas). */
object ReflexionService {
    fun describirClase(tipo: Class<*>): String {
        val propiedades = tipo.declaredFields.filterNot { it.isSynthetic || it.name.startsWith("$") }
            .map { it.name }.sorted()
        val metodos = tipo.declaredMethods.filterNot { it.isSynthetic || '$' in it.name }
            .map { it.name }.distinct().sorted()
        return "Clase ${tipo.simpleName}\n  Propiedades: ${propiedades.joinToString()}\n  Métodos: ${metodos.joinToString()}"
    }
}
