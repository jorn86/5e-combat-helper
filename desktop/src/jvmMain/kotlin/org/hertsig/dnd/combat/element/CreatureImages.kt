package org.hertsig.dnd.combat.element

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hertsig.dnd.combat.dto.StatBlock
import org.hertsig.dnd.norr.norrFolder
import org.hertsig.logger.logger
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolute
import kotlin.io.path.inputStream
import kotlin.io.path.walk

private val log = logger {}

suspend fun StatBlock.image(): ImageBitmap? = imageFor(image ?: name) ?: imageFor(name)
suspend fun imageFor(creatureName: String): ImageBitmap? = withContext(Dispatchers.IO) {
    imageCache[creatureName].orElse(null)
}

private val imageCache = Caffeine.newBuilder()
    .expireAfterAccess(2, TimeUnit.HOURS)
    .removalListener<String, Optional<ImageBitmap>> { key, _, cause -> log.trace { "$key dropped from cache: $cause" } }
    .build(ImageLoader)

private object ImageLoader : CacheLoader<String, Optional<ImageBitmap>> {
    @OptIn(ExperimentalPathApi::class)
    override fun load(creatureName: String): Optional<ImageBitmap> {
        if (norrFolder == null) {
            log.debug("No image folder configured")
            return Optional.empty()
        }

        val realFolder = norrFolder.resolve("img").absolute().normalize()
        val imageFile = realFolder.walk()
            .firstOrNull { it.fileName.toString().equals("$creatureName.png", true) }
        return if (imageFile == null) {
            log.debug { "Image file for $creatureName not found in $realFolder" }
            Optional.empty()
        } else {
            log.debug { "Image file for $creatureName found: $imageFile" }
            Optional.of(imageFile.inputStream().use(::loadImageBitmap))
        }
    }
}
