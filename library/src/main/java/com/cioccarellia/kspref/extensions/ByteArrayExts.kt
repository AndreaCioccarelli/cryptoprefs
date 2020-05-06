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

package com.cioccarellia.kspref.extensions

import androidx.annotation.CheckResult
import com.cioccarellia.kspref.KsPrefs
import com.cioccarellia.kspref.defaults.Defaults
import com.cioccarellia.kspref.engine.SymmetricKey

@CheckResult
internal fun ByteArray.toSymmetricKey() = SymmetricKey(this)

@CheckResult
internal fun ByteArray.string() = this.toString(
    charset = try {
        KsPrefs.config.charset
    } catch (configNotInitialized: KotlinNullPointerException) {
        Defaults.CHARSET
    }
)

@CheckResult
internal fun ByteArray.bitCount() = size * 8

@PublishedApi
internal inline fun emptyByteArray(
    byteCount: Int = 0
) = ByteArray(byteCount)