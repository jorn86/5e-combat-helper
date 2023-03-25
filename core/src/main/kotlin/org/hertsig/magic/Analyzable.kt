package org.hertsig.magic

/**
 * Can be added to any Magic interface to automatically generate the interface code
 */
interface Analyzable {
    fun analyze(className: String)
}
