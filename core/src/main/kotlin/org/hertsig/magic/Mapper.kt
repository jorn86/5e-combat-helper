package org.hertsig.magic

typealias Mapper = (value: Any?) -> Any?

object DefaultMapper: Mapper {
    override fun invoke(value: Any?) = value
}
