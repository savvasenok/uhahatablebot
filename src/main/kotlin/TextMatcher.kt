class TextMatcher {

    private val fanEnjoyerTextRegex = listOf(
        "^[\\w\\W]+( > )[\\w\\W]+\$".toRegex(),
        "^[\\w\\W]+( < )[\\w\\W]+\$".toRegex()
    )

    private val based = listOf("база", "базе", "базу", "базы", "базой", "base", "based")
    private val women = listOf("женщина", "женщины", "woman", "women", "women moment")

    fun matchAtLeastOne(sentence: String, wordsToFind: List<String>): Boolean =
        wordsToFind.any { it in sentence.lowercase() }

    fun matchFanEnjoyer(text: String): Boolean = fanEnjoyerTextRegex.any { it.matches(text) }

    fun matchBase(text: String): Boolean = based.any { it in text.lowercase() }
    fun matchWoman(text: String) = women.any { it == text.lowercase() }
}