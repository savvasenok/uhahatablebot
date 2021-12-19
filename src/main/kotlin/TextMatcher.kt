class TextMatcher {

    private val fanEnjoyerTextRegex = "^[A-zА-я0-9 ]+( > )[A-zА-я0-9 ]+\$".toRegex()

    fun matchAtLeastOne(sentence: String, wordsToFind: List<String>): Boolean {
        val prepared = sentence.lowercase()

        for (word in wordsToFind) {
            if (word in prepared) {
                return true
            }
        }

        return false
    }

    fun matchFanEnjoyer(text: String): Boolean = fanEnjoyerTextRegex.matches(text)
}