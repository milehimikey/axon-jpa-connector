/*
 * Copyright (c) 2024. MileHiMikey
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

package wtf.milehimikey.jpa.connector.eventstream

import wtf.milehimikey.jpa.connector.entities.DomainEventEntry
import wtf.milehimikey.jpa.connector.entities.SnapshotEventEntry
import wtf.milehimikey.jpa.connector.repositories.DomainEventEntryRepository
import wtf.milehimikey.jpa.connector.repositories.SnapshotEventEntryRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
@Import(JPAEventStream::class)
@TestPropertySource(properties = [
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "axon.jpa.allow-writes=true"
])
@ContextConfiguration(classes = [wtf.milehimikey.jpa.connector.TestConfiguration::class])
class JPAEventStreamTest {

    @Autowired
    private lateinit var eventStream: EventStream

    @Autowired
    private lateinit var domainEventRepository: DomainEventEntryRepository

    @Autowired
    private lateinit var snapshotEventRepository: SnapshotEventEntryRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `should load all events for aggregate`() {
        // Given
        val aggregateId = "test-aggregate-123"
        val events = createTestEvents(aggregateId, 3)
        events.forEach { domainEventRepository.save(it) }
        entityManager.flush()

        // When
        val loadedEvents = eventStream.load(aggregateId)

        // Then
        assertEquals(3, loadedEvents.size)
        assertEquals(0L, loadedEvents[0].sequenceNumber)
        assertEquals(1L, loadedEvents[1].sequenceNumber)
        assertEquals(2L, loadedEvents[2].sequenceNumber)
    }

    @Test
    fun `should load events from specific sequence number`() {
        // Given
        val aggregateId = "test-aggregate-456"
        val events = createTestEvents(aggregateId, 5)
        events.forEach { domainEventRepository.save(it) }
        entityManager.flush()

        // When
        val loadedEvents = eventStream.load(aggregateId, fromSequenceNumber = 2L)

        // Then
        assertEquals(3, loadedEvents.size)
        assertEquals(2L, loadedEvents[0].sequenceNumber)
        assertEquals(3L, loadedEvents[1].sequenceNumber)
        assertEquals(4L, loadedEvents[2].sequenceNumber)
    }

    @Test
    fun `should load events in sequence range`() {
        // Given
        val aggregateId = "test-aggregate-789"
        val events = createTestEvents(aggregateId, 5)
        events.forEach { domainEventRepository.save(it) }
        entityManager.flush()

        // When
        val loadedEvents = eventStream.load(aggregateId, fromSequenceNumber = 1L, toSequenceNumber = 3L)

        // Then
        assertEquals(3, loadedEvents.size)
        assertEquals(1L, loadedEvents[0].sequenceNumber)
        assertEquals(2L, loadedEvents[1].sequenceNumber)
        assertEquals(3L, loadedEvents[2].sequenceNumber)
    }

    @Test
    fun `should load events with criteria filtering`() {
        // Given
        val aggregateId = "test-aggregate-criteria"
        val events = listOf(
            createTestEvent(aggregateId, 0, "EventTypeA"),
            createTestEvent(aggregateId, 1, "EventTypeB"),
            createTestEvent(aggregateId, 2, "EventTypeA"),
            createTestEvent(aggregateId, 3, "EventTypeC")
        )
        events.forEach { domainEventRepository.save(it) }
        entityManager.flush()

        // When
        val criteria = EventLoadingCriteria(
            fromSequenceNumber = 0L,
            toSequenceNumber = 3L,
            payloadTypes = setOf("EventTypeA")
        )
        val loadedEvents = eventStream.load(aggregateId, criteria)

        // Then
        assertEquals(2, loadedEvents.size)
        assertEquals("EventTypeA", loadedEvents[0].payloadType)
        assertEquals("EventTypeA", loadedEvents[1].payloadType)
    }

    @Test
    fun `should load latest snapshot`() {
        // Given
        val aggregateId = "test-aggregate-snapshot"
        val snapshot = SnapshotEventEntry(
            aggregateIdentifier = aggregateId,
            sequenceNumber = 10L,
            type = "SnapshotEvent",
            eventIdentifier = "snapshot-$aggregateId",
            payload = "snapshot payload".toByteArray(),
            payloadType = "SnapshotEvent",
            timeStamp = Instant.now().toString()
        )
        snapshotEventRepository.save(snapshot)
        entityManager.flush()

        // When
        val loadedSnapshot = eventStream.loadLatestSnapshot(aggregateId)

        // Then
        assertNotNull(loadedSnapshot)
        assertEquals(aggregateId, loadedSnapshot.aggregateIdentifier)
        assertEquals(10L, loadedSnapshot.sequenceNumber)
    }

    @Test
    fun `should return null when no snapshot exists`() {
        // When
        val loadedSnapshot = eventStream.loadLatestSnapshot("non-existent-aggregate")

        // Then
        assertNull(loadedSnapshot)
    }

    @Test
    fun `should load events since snapshot`() {
        // Given
        val aggregateId = "test-aggregate-since-snapshot"

        // Create snapshot
        val snapshot = SnapshotEventEntry(
            aggregateIdentifier = aggregateId,
            sequenceNumber = 5L,
            type = "SnapshotEvent",
            eventIdentifier = "snapshot-$aggregateId",
            payload = "snapshot payload".toByteArray(),
            payloadType = "SnapshotEvent",
            timeStamp = Instant.now().toString()
        )
        snapshotEventRepository.save(snapshot)

        // Create events after snapshot
        val events = (6..8).map { i ->
            createTestEvent(aggregateId, i.toLong(), "TestEvent")
        }
        events.forEach { domainEventRepository.save(it) }
        entityManager.flush()

        // When
        val eventStream = eventStream.loadEventsSinceSnapshot(aggregateId)

        // Then
        assertEquals(aggregateId, eventStream.aggregateIdentifier)
        assertEquals(3, eventStream.events.size)
        assertEquals(6L, eventStream.events[0].sequenceNumber)
        assertEquals(8L, eventStream.currentSequenceNumber)
        assertTrue(eventStream.hasSnapshot())
        assertNotNull(eventStream.latestSnapshot)
    }

    private fun createTestEvents(aggregateId: String, count: Int): List<DomainEventEntry> {
        return (0 until count).map { i ->
            createTestEvent(aggregateId, i.toLong(), "TestEvent")
        }
    }

    private fun createTestEvent(aggregateId: String, sequenceNumber: Long, payloadType: String): DomainEventEntry {
        return DomainEventEntry(
            aggregateIdentifier = aggregateId,
            sequenceNumber = sequenceNumber,
            type = payloadType,
            eventIdentifier = "event-$aggregateId-$sequenceNumber",
            payload = "test payload $sequenceNumber".toByteArray(),
            payloadType = payloadType,
            timeStamp = Instant.now().toString()
        )
    }
}
