package com.kaloo.frontend

data class GuiaEjercicio(
    val nombre: String,
    val claves: List<String>,
    val musculo: String,
    val pasos: List<String>,
    val consejos: List<String>
)

internal fun sinAcentos(t: String): String =
    t.lowercase().map {
        when (it) {
            'á' -> 'a'; 'é' -> 'e'; 'í' -> 'i'; 'ó' -> 'o'; 'ú' -> 'u'; 'ü' -> 'u'; 'ñ' -> 'n'
            else -> it
        }
    }.joinToString("")

val CATALOGO_EJERCICIOS = listOf(
    GuiaEjercicio(
        "Sentadilla", listOf("sentadilla", "squat"), "Piernas y glúteos",
        listOf(
            "Párate con los pies al ancho de los hombros y las puntas ligeramente hacia afuera.",
            "Lleva la cadera hacia atrás y abajo, como si te sentaras en una silla.",
            "Baja hasta que los muslos queden casi paralelos al piso, con el pecho arriba.",
            "Empuja con los talones para subir y estira la cadera al final."
        ),
        listOf("Las rodillas siguen la dirección de los pies.", "Espalda recta, sin encorvarte.", "Si te duele la rodilla, baja menos.")
    ),
    GuiaEjercicio(
        "Flexiones", listOf("flexion", "lagartija", "push up", "push-up"), "Pecho, hombros y tríceps",
        listOf(
            "Apoya las manos un poco más abiertas que los hombros, con el cuerpo en línea recta.",
            "Baja el pecho hacia el piso doblando los codos.",
            "Empuja el piso hasta estirar los brazos."
        ),
        listOf("Aprieta abdomen y glúteos para no hundir la cadera.", "Principiante: apoya las rodillas.")
    ),
    GuiaEjercicio(
        "Press de banca", listOf("banca", "press plano", "press de pecho"), "Pecho, hombros y tríceps",
        listOf(
            "Acuéstate en el banco con los pies firmes en el piso.",
            "Agarra la barra un poco más abierta que los hombros.",
            "Baja la barra controlada hasta rozar el pecho.",
            "Empuja hacia arriba hasta estirar los brazos."
        ),
        listOf("Mantén los omóplatos juntos y apoyados.", "Pide a alguien que te cuide cuando uses mucho peso.")
    ),
    GuiaEjercicio(
        "Press militar", listOf("press militar", "press de hombro", "hombro", "press sobre la cabeza"), "Hombros",
        listOf(
            "De pie o sentado, sostén las pesas a la altura de los hombros.",
            "Empuja hacia arriba hasta estirar los brazos sin arquear la espalda.",
            "Baja controlado hasta los hombros."
        ),
        listOf("Abdomen firme para no arquear la zona lumbar.", "Usa un peso que puedas controlar.")
    ),
    GuiaEjercicio(
        "Peso muerto", listOf("peso muerto", "deadlift"), "Espalda baja, glúteos y piernas",
        listOf(
            "Pies al ancho de la cadera, con la barra sobre la mitad del pie.",
            "Dobla la cadera y las rodillas y agarra la barra con la espalda recta.",
            "Sube empujando el piso, con la barra pegada a las piernas.",
            "Termina de pie con la cadera estirada y baja con control."
        ),
        listOf("Nunca redondees la espalda.", "Empieza con poco peso para aprender la técnica.")
    ),
    GuiaEjercicio(
        "Dominadas", listOf("dominada", "pull up", "pull-up", "jalon"), "Espalda y bíceps",
        listOf(
            "Agarra la barra un poco más abierta que los hombros, con las palmas al frente.",
            "Jala el cuerpo hacia arriba hasta pasar la barbilla de la barra.",
            "Baja despacio hasta estirar los brazos."
        ),
        listOf("Evita balancearte.", "Si aún no puedes, usa una banda elástica o el jalón al pecho.")
    ),
    GuiaEjercicio(
        "Remo con mancuerna", listOf("remo"), "Espalda",
        listOf(
            "Apoya una mano y una rodilla en un banco, con la espalda recta.",
            "Con la otra mano, sostén la mancuerna con el brazo estirado.",
            "Jala el codo hacia la cadera, apretando la espalda.",
            "Baja controlado."
        ),
        listOf("No gires el tronco.", "Piensa en jalar con el codo, no con la mano.")
    ),
    GuiaEjercicio(
        "Curl de bíceps", listOf("curl de biceps", "curl biceps", "biceps", "curl"), "Bíceps",
        listOf(
            "De pie, sostén las mancuernas con los brazos pegados al cuerpo.",
            "Dobla los codos y sube las pesas hacia los hombros.",
            "Baja despacio hasta estirar los brazos."
        ),
        listOf("Los codos no se despegan del cuerpo.", "No balancees el torso para subir el peso.")
    ),
    GuiaEjercicio(
        "Extensión de tríceps", listOf("triceps", "fondos", "patada"), "Tríceps",
        listOf(
            "Sostén una mancuerna con las dos manos sobre la cabeza.",
            "Baja la pesa por detrás de la cabeza doblando los codos.",
            "Estira los brazos para subirla."
        ),
        listOf("Mantén los codos cerca de la cabeza.", "Abdomen firme para no arquear la espalda.")
    ),
    GuiaEjercicio(
        "Zancadas", listOf("zancada", "estocada", "desplante", "lunge"), "Piernas y glúteos",
        listOf(
            "De pie, da un paso largo hacia adelante.",
            "Baja doblando las dos rodillas hasta unos 90 grados.",
            "Empuja con el pie de adelante para volver al inicio y cambia de pierna."
        ),
        listOf("La rodilla de adelante no pasa mucho de la punta del pie.", "Tronco recto.")
    ),
    GuiaEjercicio(
        "Puente de glúteos", listOf("puente", "hip thrust", "gluteo"), "Glúteos",
        listOf(
            "Acuéstate boca arriba con las rodillas dobladas y los pies en el piso.",
            "Sube la cadera apretando los glúteos hasta formar una línea de hombros a rodillas.",
            "Baja despacio sin apoyar del todo."
        ),
        listOf("No arquees la espalda baja al subir.", "Pausa un segundo arriba.")
    ),
    GuiaEjercicio(
        "Plancha", listOf("plancha", "plank"), "Abdomen y core",
        listOf(
            "Apoya los antebrazos y las puntas de los pies en el piso.",
            "Mantén el cuerpo en línea recta, de la cabeza a los talones.",
            "Aguanta el tiempo indicado respirando normal."
        ),
        listOf("No dejes caer la cadera ni la subas demasiado.", "Es un ejercicio por tiempo, no por repeticiones.")
    ),
    GuiaEjercicio(
        "Abdominales", listOf("abdominal", "crunch", "encogimiento"), "Abdomen",
        listOf(
            "Acuéstate boca arriba con las rodillas dobladas.",
            "Pon las manos a los lados de la cabeza, sin jalar el cuello.",
            "Sube el pecho hacia las rodillas contrayendo el abdomen.",
            "Baja despacio."
        ),
        listOf("El movimiento sale del abdomen, no del cuello.", "Exhala al subir.")
    ),
    GuiaEjercicio(
        "Elevaciones laterales", listOf("elevacion lateral", "elevaciones laterales", "vuelo lateral"), "Hombros",
        listOf(
            "De pie, sostén una mancuerna en cada mano a los lados.",
            "Sube los brazos hacia los lados hasta la altura de los hombros.",
            "Baja controlado."
        ),
        listOf("Usa poco peso.", "Codos ligeramente doblados, sin encogerte de hombros.")
    ),
    GuiaEjercicio(
        "Prensa de piernas", listOf("prensa"), "Piernas",
        listOf(
            "Siéntate en la máquina con la espalda bien apoyada.",
            "Pon los pies al ancho de los hombros en la plataforma.",
            "Baja las rodillas hacia el pecho de forma controlada.",
            "Empuja sin bloquear las rodillas al estirar."
        ),
        listOf("No despegues la cadera del asiento.", "No bloquees las rodillas arriba.")
    ),
    GuiaEjercicio(
        "Cardio / saltar la cuerda", listOf("cuerda", "cardio", "burpee", "trote", "correr", "bicicleta", "mountain"), "Todo el cuerpo",
        listOf(
            "Calienta 3 a 5 minutos con ritmo suave.",
            "Sube la intensidad hasta que puedas hablar, pero con esfuerzo.",
            "Termina bajando el ritmo poco a poco."
        ),
        listOf("Aterriza suave, con las rodillas flexibles.", "Hidrátate y para si te mareas.")
    )
)

fun guiaDe(nombre: String): GuiaEjercicio? {
    val n = sinAcentos(nombre)
    return CATALOGO_EJERCICIOS.firstOrNull { g -> g.claves.any { n.contains(it) } }
}

fun urlVideo(nombre: String): String {
    val limpio = sinAcentos(nombre).filter { it.isLetterOrDigit() || it == ' ' }.trim()
    return "https://www.youtube.com/results?search_query=como+hacer+" + limpio.replace(' ', '+')
}