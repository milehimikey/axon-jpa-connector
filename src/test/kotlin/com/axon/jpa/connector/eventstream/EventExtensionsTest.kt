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
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EventExtensionsTest {

    @Test
    fun `should convert payload to string`() {
        // Given
        val event = createDomainEvent(payload = "test payload".toByteArray())

        // When
        val payloadString = event.payloadAsString()

        // Then
        assertEquals("test payload", payloadString)
    }

    @Test
    fun `should detect JSON payload for object`() {
        // Given
        val jsonPayload = """{"name": "test", "value": 123}"""
        val event = createDomainEvent(payload = jsonPayload.toByteArray())

        // When & Then
        assertTrue(event.isJsonPayload())
    }

    @Test
    fun `should detect JSON payload for array`() {
        // Given
        val jsonPayload = """[{"name": "test"}, {"name": "test2"}]"""
        val event = createDomainEvent(payload = jsonPayload.toByteArray())

        // When & Then
        assertTrue(event.isJsonPayload())
    }

    @Test
    fun `should not detect JSON payload for plain text`() {
        // Given
        val textPayload = "plain text payload"
        val event = createDomainEvent(payload = textPayload.toByteArray())

        // When & Then
        assertFalse(event.isJsonPayload())
    }

    @Test
    fun `should convert metadata to string when present`() {
        // Given
        val metadata = "test metadata".toByteArray()
        val event = createDomainEvent(metaData = metadata)

        // When
        val metadataString = event.metaDataAsString()

        // Then
        assertEquals("test metadata", metadataString)
        assertTrue(event.hasMetaData())
    }

    @Test
    fun `should return null for metadata when not present`() {
        // Given
        val event = createDomainEvent(metaData = null)

        // When
        val metadataString = event.metaDataAsString()

        // Then
        assertNull(metadataString)
        assertFalse(event.hasMetaData())
    }

    @Test
    fun `should work with snapshot events`() {
        // Given
        val jsonPayload = """{"snapshot": true}"""
        val snapshot = createSnapshotEvent(payload = jsonPayload.toByteArray())

        // When & Then
        assertEquals(jsonPayload, snapshot.payloadAsString())
        assertTrue(snapshot.isJsonPayload())
    }

    private fun createDomainEvent(
        payload: ByteArray = "test".toByteArray(),
        metaData: ByteArray? = null
    ): DomainEventEntry {
        return DomainEventEntry(
            aggregateIdentifier = "test-aggregate",
            sequenceNumber = 1L,
            type = "TestEvent",
            eventIdentifier = "test-event",
            payload = payload,
            payloadType = "TestEvent",
            timeStamp = Instant.now().toString(),
            metaData = metaData
        )
    }

    private fun createSnapshotEvent(
        payload: ByteArray = "test".toByteArray(),
        metaData: ByteArray? = null
    ): SnapshotEventEntry {
        return SnapshotEventEntry(
            aggregateIdentifier = "test-aggregate",
            sequenceNumber = 1L,
            type = "SnapshotEvent",
            eventIdentifier = "test-snapshot",
            payload = payload,
            payloadType = "SnapshotEvent",
            timeStamp = Instant.now().toString(),
            metaData = metaData
        )
    }
}
