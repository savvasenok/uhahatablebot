import java.io.File

interface MemeCreator {

    fun createSniffMeme(overlayPhotoFilename: String, outputName: String): File
    fun createFanEnjoyerMeme(fan: File, enjoyer: File, outputName: String): File

    class Base(
        private val fileManager: FileManager
    ) : MemeCreator {

        override fun createSniffMeme(overlayPhotoFilename: String, outputName: String): File {
            val sniffCommand =
                "ffmpeg -i ${fileManager.getSniffVideoPath()} -i ${fileManager.getSavedContent(overlayPhotoFilename)} -b:v 1M -filter_complex [1:v]scale=640:395[ovrl],[0:v][ovrl]overlay=(0):(0) ${
                    fileManager.getOutputPath(
                        "$outputName.mp4"
                    ).path
                }"
            sniffCommand.runCommand()
            return fileManager.getOutputPath("$outputName.mp4")
        }

        override fun createFanEnjoyerMeme(fan: File, enjoyer: File, outputName: String): File {
            val fanCommand =
                "ffmpeg -y -i ${fileManager.getFanVideoPath()} -vf subtitles=${fan.path}:force_style='Fontsize=14,Alignment=6' ${
                    fileManager.getOutputPath("${outputName}fan.mp4").path
                }"
            val enjoyerCommand =
                "ffmpeg -y -i ${fileManager.getEnjoyerVideoPath()} -vf subtitles=${enjoyer.path}:force_style='Fontsize=14,Alignment=6' ${
                    fileManager.getOutputPath("${outputName}enjoyer.mp4").path
                }"
            val connectVideoAndAddAudioCommand =
                "ffmpeg -i ${fileManager.getOutputPath("${outputName}fan.mp4").path} -i ${fileManager.getOutputPath("${outputName}enjoyer.mp4").path} -i ${fileManager.getFanEnjoyerMusicPath().path} -filter_complex hstack=inputs=2 ${
                    fileManager.getOutputPath("${outputName}.mp4").path
                }"

            fanCommand.runCommand()
            enjoyerCommand.runCommand()
            connectVideoAndAddAudioCommand.runCommand()

            fileManager.deleteOutput("${outputName}fan")
            fileManager.deleteOutput("${outputName}enjoyer")
            fileManager.deleteSaved(fan.name)
            fileManager.deleteSaved(enjoyer.name)

            return fileManager.getOutputPath("$outputName.mp4")
        }
    }
}

fun String.runCommand() = ProcessBuilder(
    "\\s".toRegex()
        .split(this)
)
    .redirectError(File("/home/savvasenok/Desktop/test.txt"))
    .start()
    .waitFor()