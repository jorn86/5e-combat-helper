package org.hertsig.dnd.combat.element

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.google.common.base.Optional
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hertsig.core.debug
import org.hertsig.core.logger
import org.hertsig.core.trace
import org.hertsig.dnd.combat.dto.StatBlock
import java.util.concurrent.TimeUnit
import kotlin.io.path.*

private val log = logger {}

suspend fun StatBlock.image() = imageFor(name)
suspend fun imageFor(creatureName: String) = withContext(Dispatchers.IO) {
    imageCache.get(creatureName).orNull()
}

private val imageCache = CacheBuilder.newBuilder()
    .expireAfterAccess(2, TimeUnit.HOURS)
    .removalListener<String, Optional<ImageBitmap>> { log.trace { "${it.key} dropped from cache" } }
    .build(ImageLoader)

private object ImageLoader : CacheLoader<String, Optional<ImageBitmap>>() {
    @OptIn(ExperimentalPathApi::class)
    override fun load(creatureName: String): Optional<ImageBitmap> {
        val folder = System.getProperty("imageFolder")
        if (folder == null) {
            log.debug("No image folder configured")
            return Optional.absent()
        }

        val realFolder = Path(folder).absolute()
        val imageFile = realFolder.walk().firstOrNull { it.fileName.toString().equals("$creatureName.png", true) }
        return if (imageFile == null) {
            log.debug { "Image file for $creatureName not found in $folder" }
            Optional.absent()
        } else {
            log.debug { "Image file for $creatureName found: $imageFile" }
            Optional.of(imageFile.inputStream().use(::loadImageBitmap))
        }
    }
}
