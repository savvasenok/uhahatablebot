class TextMatcher {

    private val fanEnjoyerTextRegex = listOf(
        "^[\\w\\W]+( > )[\\w\\W]+\$".toRegex(),
        "^[\\w\\W]+( < )[\\w\\W]+\$".toRegex()
    )

    fun matchAtLeastOne(sentence: String, wordsToFind: List<String>): Boolean {
        val prepared = sentence.lowercase()

        for (word in wordsToFind) {
            if (word in prepared) {
                return true
            }
        }

        return false
    }

    fun matchFanEnjoyer(text: String): Boolean = fanEnjoyerTextRegex.any { it.matches(text) }
}