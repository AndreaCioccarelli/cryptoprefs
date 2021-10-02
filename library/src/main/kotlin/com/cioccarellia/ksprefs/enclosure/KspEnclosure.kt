/**
 * Designed and developed by Andrea Cioccarelli (@cioccarellia)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("NOTHING_TO_INLINE")

package com.cioccarellia.ksprefs.enclosure

import android.annotation.SuppressLint
import android.content.Context
import com.cioccarellia.ksprefs.KsPrefs
import com.cioccarellia.ksprefs.config.model.CommitStrategy
import com.cioccarellia.ksprefs.engines.EnginePicker
import com.cioccarellia.ksprefs.engines.Transmission
import com.cioccarellia.ksprefs.engines.base.Engine
import com.cioccarellia.ksprefs.extensions.*

@Suppress("MemberVisibilityCanBePrivate")
@SuppressLint("CommitPrefEdits")
@PublishedApi
internal class KspEnclosure(
    private val namespace: String,
    context: Context,
    internal var sharedReader: Reader = context.sharedprefs(namespace),
    internal val engine: Engine = EnginePicker.select(context)
) {
    private inline val defaultCommitStrategy
        get() = KsPrefs.config.commitStrategy

    private inline fun deriveValue(
        value: ByteArray
    ): ByteArray = engine.derive(
        Transmission(value)
    ).payload

    private inline fun integrateValue(
        value: ByteArray
    ): ByteArray = engine.integrate(
        Transmission(value)
    ).payload

    /**
     * We can safely assume that key is a string
     * */
    private inline fun deriveKey(
        value: String
    ): String = engine.derive(
        Transmission(value.bytes())
    ).payload.string()

    internal fun read(
        key: String,
        fallback: ByteArray
    ) = integrateValue(
        sharedReader.read(deriveKey(key), deriveValue(fallback))
    )

    internal fun readUnsafe(
        key: String
    ) = integrateValue(
        sharedReader.readOrThrow(deriveKey(key))
    )

    internal fun write(
        key: String,
        value: ByteArray,
        strategy: CommitStrategy
    ) = with(sharedReader.edit()) {
        write(deriveKey(key), deriveValue(value))
        finalize(strategy)
    }

    internal fun exists(
        key: String
    ) = sharedReader.exists(deriveKey(key))

    internal fun all(): Map<String, *> = sharedReader.all

    internal fun save(
        commitStrategy: CommitStrategy
    ) {
        with(sharedReader.edit()) {
            forceFinalization(commitStrategy = commitStrategy)
        }
    }

    internal fun remove(
        key: String
    ) = with(sharedReader.edit()) {
        delete(deriveKey(key))
        finalize(defaultCommitStrategy)
    }

    internal fun clear() = with(sharedReader.edit()) {
        clear()
        finalize(commitStrategy = CommitStrategy.APPLY)
    }
}