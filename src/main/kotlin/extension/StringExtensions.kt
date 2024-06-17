package extension

val englishRegex = Regex("^[a-zA-Z]+$")

fun String.isEnglish(): Boolean {
    return englishRegex.matches(this)
}