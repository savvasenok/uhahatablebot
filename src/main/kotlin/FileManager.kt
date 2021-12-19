import content.CringeVideo
import content.EnjoyerVideo
import content.FanVideo
import content.SniffVideo
import java.io.File

interface FileManager {

    fun savePhoto(filename: String, bytes: ByteArray)
    fun getCringeVideo(): File

    fun getSniffVideoPath(): String
    fun getFanVideoPath(): String
    fun getEnjoyerVideoPath(): String

    fun getSavedContent(filename: String): File?
    fun getOutputPath(filename: String): File
    fun deleteSaved(filename: String)
    fun deleteOutput(filename: String)
    fun createFanEnjoyerSubs(fan: String, enjoyer: String, filename: String): Pair<File, File>
    fun getFanEnjoyerMusicPath(): File

    class Base(
        private val srtGenerator: SRTGenerator,
        main: File
    ) : FileManager {
        private val contentFolder = File(main, "content")
        private val savedContentFolder = File(main, "saved")
        private val outputContentFolder = File(main, "output")

        init {
            if (!contentFolder.exists()) contentFolder.mkdir()
            if (!savedContentFolder.exists()) savedContentFolder.mkdir()
            if (!outputContentFolder.exists()) outputContentFolder.mkdir()
        }

        override fun savePhoto(filename: String, bytes: ByteArray) {
            File(savedContentFolder, filename).apply {
                createNewFile()
                writeBytes(bytes)
            }
        }

        override fun getCringeVideo(): File = CringeVideo(contentFolder).file

        override fun getSniffVideoPath(): String = SniffVideo(contentFolder).file.path

        override fun getFanVideoPath(): String = FanVideo(contentFolder).file.path
        override fun getEnjoyerVideoPath(): String = EnjoyerVideo(contentFolder).file.path

        override fun getSavedContent(filename: String): File? {
            val file = File(savedContentFolder, filename)

            if (file.exists()) {
                return file
            }

            return null
        }

        override fun getOutputPath(filename: String): File {
            return File(outputContentFolder, filename)
        }

        override fun deleteSaved(filename: String) {
            File(savedContentFolder, filename).delete()
        }

        override fun deleteOutput(filename: String) {
            File(outputContentFolder, "$filename.mp4").delete()
        }

        override fun createFanEnjoyerSubs(fan: String, enjoyer: String, filename: String): Pair<File, File> {
            val (fanSubs, enjoyerSubs) = srtGenerator.createFanEnjoyerSubtitles(fan, enjoyer)

            return Pair(
                File(savedContentFolder, "${filename}fan.srt").apply {
                    writeText(fanSubs)
                },
                File(savedContentFolder, "${filename}enjoyer.srt").apply {
                    writeText(enjoyerSubs)
                })
        }

        override fun getFanEnjoyerMusicPath() = File(contentFolder, "fan-enjoyer.mp3")
    }
}