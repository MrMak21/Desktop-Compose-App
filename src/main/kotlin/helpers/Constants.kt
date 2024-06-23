package helpers

object Constants {


//    val vehiclePlateNumbersRegex = Regex("^\\d{4}\$")
    val vehiclePlateNumbersRegex = Regex("^\\d{4}\\.0$")


    object Vehicle {
        const val LICENSE_FULL_REGEX = "[A-Za-z]{3}\\d{4}"
        const val GREEK_LICENSE_FULL_REGEX = "[\\u0370-\\u03FF\\u1F00-\\u1FFF]{3}\\d{4}"
        const val GREEK_LICENSE_FULL_REGEX_ALTERNATIVE = "[\\u0370-\\u03FF\\u1F00-\\u1FFF]{3} \\d{4}"
        const val LICENSE_LETTERS_REGEX = "[A-Za-z]{3}"
        const val GREEK_LICENSE_LETTERS_REGEX = "[\\u0370-\\u03FF\\u1F00-\\u1FFF]{3}"
    }
}