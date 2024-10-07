interface SRTGenerator {

    fun createFanEnjoyerSubtitles(
        fan: String,
        enjoyer: String
    ): Pair<String, String>

    class Base : SRTGenerator {
        override fun createFanEnjoyerSubtitles(fan: String, enjoyer: String): Pair<String, String> {
            val subtitlesFan = "Average $fan fan"
            val subtitlesEnjoyer = "Average $enjoyer enjoyer"

            return Pair(
                "1\n00:00:00,000 --> 00:15:00,000\n$subtitlesFan",
                "1\n00:00:00,000 --> 00:15:00,000\n$subtitlesEnjoyer"
            )
        }
    }
}