package utils

object Reflexion {
    fun listarMiembros(clase: Class<*>) {
        println("Clase: ${clase.simpleName}")

        val metodos: MutableList<String> = mutableListOf()
        val campos: MutableList<String> = mutableListOf()
        var actual: Class<*>? = clase

        while (actual != null && actual != Any::class.java) {
            actual.declaredMethods.forEach { metodos.add(it.name) }
            actual.declaredFields.forEach { campos.add(it.name) }
            actual = actual.superclass
        }

        println("Métodos:")
        metodos.distinct().sorted().forEach { println("  - $it") }

        println("Propiedades/campos:")
        campos.distinct().sorted().forEach { println("  - $it") }

        println("Anotaciones:")
        val anotaciones: Array<Annotation> = clase.declaredAnnotations
        if (anotaciones.isEmpty()) {
            println("  - Sin anotaciones")
        } else {
            anotaciones.forEach { println("  - $it") }
        }
    }
}
