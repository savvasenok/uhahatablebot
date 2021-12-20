import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.get.getFileAdditionalInfo
import dev.inmo.tgbotapi.extensions.api.send.media.sendVideo
import dev.inmo.tgbotapi.extensions.api.send.sendActionRecordVideo
import dev.inmo.tgbotapi.extensions.api.send.sendActionUploadDocument
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.media.PhotoContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.filenameFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            launch {
                sendActionUploadDocument(it.chat)
                sendVideo(it.chat, fileManager.getCringeVideo().asMultipartFile())
            }
        }

        onText(
            {
                textMatcher.matchAtLeastOne(it.content.text, WordTriggers.sniff) &&
                        it.replyTo != null
            }
        ) {
            launch {
                sendActionRecordVideo(it.chat)
                val photoMessage = it.replyTo as CommonMessage<PhotoContent>
                val pathedFile = bot.getFileAdditionalInfo(photoMessage.content)
                fileManager.savePhoto(pathedFile.filePath.filenameFromUrl, bot.downloadFile(pathedFile))
                val video: File
                withContext(Dispatchers.Default) {
                    video = memeCreator.createSniffMeme(pathedFile.filePath.filenameFromUrl, it.messageId.toString())
                }

                sendActionUploadDocument(it.chat)
                withContext(Dispatchers.IO) {
                    sendVideo(it.chat, video.asMultipartFile(), replyToMessageId = it.messageId)
                    fileManager.deleteSaved(pathedFile.filePath.filenameFromUrl)
                    fileManager.deleteOutput(it.messageId.toString())
                }
            }
        }

        onText(
            { textMatcher.matchFanEnjoyer(it.content.text) }
        ) {
            val bigger = (">" in it.content.text)
            val splitted = if (bigger) it.content.text.split(" > ") else it.content.text.split(" < ")
            if (splitted.size == 2) {

                launch {
                    sendActionRecordVideo(it.chat)
                    val fan = if (bigger) splitted[1] else splitted[0]
                    val enjoyer = if (bigger) splitted[0] else splitted[1]

                    val (fanSubs, enjoyerSubs) = fileManager.createFanEnjoyerSubs(fan, enjoyer, it.messageId.toString())

                    val video: File
                    withContext(Dispatchers.Default) {
                        video = memeCreator.createFanEnjoyerMeme(fanSubs, enjoyerSubs, it.messageId.toString())
                    }

                    sendActionUploadDocument(it.chat)
                    withContext(Dispatchers.IO) {
                        sendVideo(it.chat, video.asMultipartFile(), replyToMessageId = it.messageId)
                        fileManager.deleteOutput(it.messageId.toString())
                    }
                }
            }
        }

        onText({ textMatcher.matchBase(it.content.text) }) {
            launch(Dispatchers.IO) {
                val videoToSend = listOf(fileManager.getBasedVideo(), fileManager.getItsBaseVideo()).random()
                sendVideo(it.chat, videoToSend.asMultipartFile(), replyToMessageId = it.messageId)
            }
        }
    }.join()
}