/*
 * Copyright (c) 2024. Axon JPA Connector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axon.jpa.connector.eventstream

import com.axon.jpa.connector.entities.DomainEventEntry
import com.axon.jpa.connector.entities.SnapshotEventEntry

/**
 * Extension functions for working with event payloads without deserialization.
 */

/**
 * Convert the payload byte array to a UTF-8 string.
 * Useful for JSON or text-based payloads.
 */
fun DomainEventEntry.payloadAsString(): String = String(payload, Charsets.UTF_8)

/**
 * Check if the payload appears to be JSON format.
 * This is a simple heuristic based on the first character.
 */
fun DomainEventEntry.isJsonPayload(): Boolean = payloadAsString().trim().let { 
    it.startsWith("{") || it.startsWith("[") 
}

/**
 * Convert the metadata byte array to a UTF-8 string, if metadata exists.
 * Returns null if no metadata is present.
 */
fun DomainEventEntry.metaDataAsString(): String? {
    val meta = metaData
    return meta?.let { String(it, Charsets.UTF_8) }
}

/**
 * Check if this event has metadata.
 */
fun DomainEventEntry.hasMetaData(): Boolean {
    val meta = metaData
    return meta != null && meta.isNotEmpty()
}

/**
 * Convert the payload byte array to a UTF-8 string.
 * Useful for JSON or text-based payloads.
 */
fun SnapshotEventEntry.payloadAsString(): String = String(payload, Charsets.UTF_8)

/**
 * Check if the payload appears to be JSON format.
 * This is a simple heuristic based on the first character.
 */
fun SnapshotEventEntry.isJsonPayload(): Boolean = payloadAsString().trim().let { 
    it.startsWith("{") || it.startsWith("[") 
}

/**
 * Convert the metadata byte array to a UTF-8 string, if metadata exists.
 * Returns null if no metadata is present.
 */
fun SnapshotEventEntry.metaDataAsString(): String? {
    val meta = metaData
    return meta?.let { String(it, Charsets.UTF_8) }
}

/**
 * Check if this snapshot has metadata.
 */
fun SnapshotEventEntry.hasMetaData(): Boolean {
    val meta = metaData
    return meta != null && meta.isNotEmpty()
}
