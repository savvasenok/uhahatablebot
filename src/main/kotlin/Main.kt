import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.get.getFileAdditionalInfo
import dev.inmo.tgbotapi.extensions.api.send.media.sendVideo
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.media.PhotoContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.filenameFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(PreviewFeature::class)
suspend fun main(args: Array<String>) {

    val textMatcher = TextMatcher()
    val sRTGenerator = SRTGenerator.Base()
    val fileManager = FileManager.Base(sRTGenerator, File(System.getProperty("user.dir")))
    val memeCreator = MemeCreator.Base(fileManager)
    val bot = telegramBot(args.first())

    val scope = CoroutineScope(Dispatchers.Default)

    bot.buildBehaviourWithLongPolling(scope) {
        println(getMe())

        onText({ textMatcher.matchAtLeastOne(it.content.text, WordTriggers.cringe) }) {
            sendVideo(it.chat, fileManager.getCringeVideo().asMultipartFile())
        }

        onText(
            {
                textMatcher.matchAtLeastOne(it.content.text, WordTriggers.sniff) &&
                        it.replyTo != null
            }
        ) {
            try {
                val photoMessage = it.replyTo as CommonMessage<PhotoContent>
                val pathedFile = bot.getFileAdditionalInfo(photoMessage.content)

                fileManager.savePhoto(pathedFile.filePath.filenameFromUrl, bot.downloadFile(pathedFile))
                val video = memeCreator.createSniffMeme(pathedFile.filePath.filenameFromUrl, it.messageId.toString())
                sendVideo(it.chat, video.asMultipartFile())

                fileManager.deleteSaved(pathedFile.filePath.filenameFromUrl)
                fileManager.deleteOutput(it.messageId.toString())
            } catch (e: Exception) {
                // pass
            }
        }

        onText(
            { textMatcher.matchFanEnjoyer(it.content.text) }
        ) {
            val splitted = it.content.text.split(" > ")
            if (splitted.size == 2) {
                val fan = splitted[1]
                val enjoyer = splitted[0]

                val (fanSubs, enjoyerSubs) = fileManager.createFanEnjoyerSubs(fan, enjoyer, it.messageId.toString())
                val video = memeCreator.createFanEnjoyerMeme(fanSubs, enjoyerSubs, it.messageId.toString())
                //                val video = memeCreator.createFanEnjoyerMeme(fan, enjoyer, it.messageId.toString())
                sendVideo(it.chat, video.asMultipartFile())
            }
        }
    }.join()
}