/*
 * MIT License
 *
 * Copyright (c) 2025 Axon JPA Connector
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.axon.jpa.connector

import com.axon.jpa.connector.entities.DomainEventEntry
import com.axon.jpa.connector.entities.TokenEntry
import com.axon.jpa.connector.repositories.DomainEventEntryRepository
import com.axon.jpa.connector.repositories.TokenEntryRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for Axon JPA Connector.
 * Tests the basic functionality of entities and repositories.
 */
@DataJpaTest
@TestPropertySource(properties = [
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver"
])
class AxonJpaConnectorTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var domainEventRepository: DomainEventEntryRepository

    @Autowired
    private lateinit var tokenRepository: TokenEntryRepository

    @Test
    fun `should save and retrieve domain event entry`() {
        // Given
        val domainEvent = DomainEventEntry(
            aggregateIdentifier = "test-aggregate-123",
            sequenceNumber = 1L,
            type = "TestEvent",
            eventIdentifier = "event-123",
            payload = "test payload".toByteArray(),
            payloadType = "java.lang.String",
            timeStamp = Instant.now().toString()
        )

        // When
        val savedEvent = domainEventRepository.save(domainEvent)
        entityManager.flush()

        // Then
        assertNotNull(savedEvent.globalIndex)
        assertEquals("test-aggregate-123", savedEvent.aggregateIdentifier)
        assertEquals(1L, savedEvent.sequenceNumber)
        assertEquals("TestEvent", savedEvent.type)
        assertEquals("event-123", savedEvent.eventIdentifier)
        assertEquals("java.lang.String", savedEvent.payloadType)
    }

    @Test
    fun `should save and retrieve token entry`() {
        // Given
        val tokenEntry = TokenEntry(
            processorName = "test-processor",
            segment = 0,
            token = "test token".toByteArray(),
            tokenType = "GlobalSequenceTrackingToken",
            timestamp = Instant.now().toString(),
            owner = "test-owner"
        )

        // When
        val savedToken = tokenRepository.save(tokenEntry)
        entityManager.flush()

        // Then
        assertEquals("test-processor", savedToken.processorName)
        assertEquals(0, savedToken.segment)
        assertEquals("GlobalSequenceTrackingToken", savedToken.tokenType)
        assertEquals("test-owner", savedToken.owner)
    }

    @Test
    fun `should find domain events by aggregate identifier`() {
        // Given
        val domainEvent1 = createTestDomainEvent("aggregate-1", 1L, "event-1")
        val domainEvent2 = createTestDomainEvent("aggregate-1", 2L, "event-2")
        val domainEvent3 = createTestDomainEvent("aggregate-2", 1L, "event-3")

        domainEventRepository.saveAll(listOf(domainEvent1, domainEvent2, domainEvent3))
        entityManager.flush()

        // When
        val events = domainEventRepository.findByAggregateIdentifier("aggregate-1", org.springframework.data.domain.Pageable.unpaged())

        // Then
        assertEquals(2, events.totalElements)
        assertEquals("aggregate-1", events.content[0].aggregateIdentifier)
        assertEquals("aggregate-1", events.content[1].aggregateIdentifier)
    }

    @Test
    fun `should count distinct aggregate identifiers`() {
        // Given
        val domainEvent1 = createTestDomainEvent("aggregate-1", 1L, "event-1")
        val domainEvent2 = createTestDomainEvent("aggregate-1", 2L, "event-2")
        val domainEvent3 = createTestDomainEvent("aggregate-2", 1L, "event-3")

        domainEventRepository.saveAll(listOf(domainEvent1, domainEvent2, domainEvent3))
        entityManager.flush()

        // When
        val count = domainEventRepository.countDistinctAggregateIdentifiers()

        // Then
        assertEquals(2L, count)
    }

    private fun createTestDomainEvent(aggregateId: String, sequenceNumber: Long, eventId: String): DomainEventEntry {
        return DomainEventEntry(
            aggregateIdentifier = aggregateId,
            sequenceNumber = sequenceNumber,
            type = "TestEvent",
            eventIdentifier = eventId,
            payload = "test payload".toByteArray(),
            payloadType = "java.lang.String",
            timeStamp = Instant.now().toString()
        )
    }
}
