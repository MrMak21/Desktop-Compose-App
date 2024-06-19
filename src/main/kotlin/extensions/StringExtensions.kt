package extensions

import helpers.Constants

val englishRegex = Regex("^[a-zA-Z]+$")
val greekRegex = Regex("^[α-ωΑ-Ω\\s]*\$")

val englishToGreek = mapOf(
    'a' to 'α', 'A' to 'Α',
    'b' to 'β', 'B' to 'Β',
    'e' to 'ε', 'E' to 'Ε',
    'h' to 'η', 'H' to 'Η',
    'i' to 'ι', 'I' to 'Ι',
    'k' to 'κ', 'K' to 'Κ',
    'm' to 'μ', 'M' to 'Μ',
    'n' to 'ν', 'N' to 'Ν',
    'o' to 'ο', 'O' to 'Ο',
    'p' to 'π', 'P' to 'Ρ',
    'r' to 'ρ', 'R' to 'Ρ',
    's' to 'σ', 'S' to 'Σ',
    't' to 'τ', 'T' to 'Τ',
    'u' to 'υ', 'U' to 'Υ',
    'x' to 'χ', 'X' to 'Χ',
    'y' to 'γ', 'Y' to 'Υ',
    'z' to 'ζ', 'Z' to 'Ζ'
)

val greekToEnglish = mapOf(
    'α' to 'a', 'Α' to 'A',
    'β' to 'b', 'Β' to 'B',
    'ε' to 'e', 'Ε' to 'E',
    'η' to 'h', 'Η' to 'H',
    'ι' to 'i', 'Ι' to 'I',
    'κ' to 'k', 'Κ' to 'K',
    'μ' to 'm', 'Μ' to 'M',
    'ν' to 'n', 'Ν' to 'N',
    'ο' to 'o', 'Ο' to 'O',
    'π' to 'p', 'Ρ' to 'P',
    'ρ' to 'r',
    'σ' to 's', 'Σ' to 'S',
    'τ' to 't', 'Τ' to 'T',
    'υ' to 'u', 'Υ' to 'U',
    'χ' to 'x', 'Χ' to 'X',
    'γ' to 'y', 'Υ' to 'Y',
    'ζ' to 'z', 'Ζ' to 'Z'
)

fun String.isEnglish(): Boolean = englishRegex.matches(this)
fun String.isGreek() = greekRegex.matches(this)

fun String.translateToGreek(): String {
    return this.map { char ->
        englishToGreek[char] ?: char
    }.joinToString("")
}

fun String.translateToEnglish(): String {
    return this.map { char ->
        greekToEnglish[char] ?: char
    }.joinToString("")
}