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
                    fileManager.getOutputPath("$outputName.mp4").path
                }"
            sniffCommand.runCommand(fileManager.logFile)
            return fileManager.getOutputPath("$outputName.mp4")
        }

        override fun createFanEnjoyerMeme(fan: File, enjoyer: File, outputName: String): File {
            val output = fileManager.getOutputPath("${outputName}.mp4").path
            val fanVid = fileManager.getFanVideoPath()
            val fanSubs = fan.path
            val enjVid = fileManager.getEnjoyerVideoPath()
            val enjSubs = enjoyer.path
            val music = fileManager.getFanEnjoyerMusicPath().path
            val command =
                """ffmpeg -y -i $fanVid -i $enjVid -i $music -filter_complex [0:v]subtitles=$fanSubs:force_style='Fontsize=12,Alignment=6'[fan];[1:v]subtitles=$enjSubs:force_style='Fontsize=12,Alignment=6'[enjoyer];[fan][enjoyer]hstack=inputs=2[video];[video]drawtext=text='@uhahatablebot':fontcolor=white@0.7:x=w-text_w-12:y=h-text_h-12 $output"""

            println(command)

            command.runCommand(fileManager.logFile)

            return fileManager.getOutputPath("$outputName.mp4")
        }
    }
}

fun String.runCommand(logFile: File) = ProcessBuilder("\\s".toRegex().split(this))
    .redirectError(logFile)
    .start()
    .waitFor()