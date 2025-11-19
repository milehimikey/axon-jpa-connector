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
import wtf.milehimikey.jpa.connector.repositories.ReadOnlyDomainEventEntryRepository
import wtf.milehimikey.jpa.connector.repositories.ReadOnlySnapshotEventEntryRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.domain.Sort
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ReadOnlyEventStreamTest {

    @Mock
    private lateinit var domainEventRepository: ReadOnlyDomainEventEntryRepository

    @Mock
    private lateinit var snapshotEventRepository: ReadOnlySnapshotEventEntryRepository

    @InjectMocks
    private lateinit var readOnlyEventStream: ReadOnlyEventStream

    @Test
    fun `should load events from sequence number`() {
        // Given
        val aggregateId = "test-aggregate"
        val fromSequence = 5L
        val expectedEvents = listOf(
            createTestEvent(aggregateId, 5L, "TestEvent"),
            createTestEvent(aggregateId, 6L, "TestEvent")
        )

        `when`(
            domainEventRepository.findByAggregateIdentifierAndSequenceNumberGreaterThanEqual(
                aggregateId, fromSequence, Sort.by("sequenceNumber")
            )
        ).thenReturn(expectedEvents)

        // When
        val result = readOnlyEventStream.load(aggregateId, fromSequence)

        // Then
        assert(result == expectedEvents)
        verify(domainEventRepository).findByAggregateIdentifierAndSequenceNumberGreaterThanEqual(
            aggregateId, fromSequence, Sort.by("sequenceNumber")
        )
    }

    @Test
    fun `should load events in sequence range`() {
        // Given
        val aggregateId = "test-aggregate"
        val fromSequence = 5L
        val toSequence = 10L
        val expectedEvents = listOf(
            createTestEvent(aggregateId, 5L, "TestEvent"),
            createTestEvent(aggregateId, 6L, "TestEvent")
        )

        `when`(
            domainEventRepository.findByAggregateIdentifierAndSequenceNumberBetween(
                aggregateId, fromSequence, toSequence, Sort.by("sequenceNumber")
            )
        ).thenReturn(expectedEvents)

        // When
        val result = readOnlyEventStream.load(aggregateId, fromSequence, toSequence)

        // Then
        assert(result == expectedEvents)
        verify(domainEventRepository).findByAggregateIdentifierAndSequenceNumberBetween(
            aggregateId, fromSequence, toSequence, Sort.by("sequenceNumber")
        )
    }

    @Test
    fun `should load latest snapshot`() {
        // Given
        val aggregateId = "test-aggregate"
        val expectedSnapshot = createSnapshotEvent(aggregateId, 10L)

        `when`(snapshotEventRepository.findLatestSnapshotByAggregateIdentifier(aggregateId))
            .thenReturn(expectedSnapshot)

        // When
        val result = readOnlyEventStream.loadLatestSnapshot(aggregateId)

        // Then
        assert(result == expectedSnapshot)
        verify(snapshotEventRepository).findLatestSnapshotByAggregateIdentifier(aggregateId)
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

    private fun createSnapshotEvent(aggregateId: String, sequenceNumber: Long): SnapshotEventEntry {
        return SnapshotEventEntry(
            aggregateIdentifier = aggregateId,
            sequenceNumber = sequenceNumber,
            type = "SnapshotEvent",
            eventIdentifier = "snapshot-$aggregateId",
            payload = "snapshot payload".toByteArray(),
            payloadType = "SnapshotEvent",
            timeStamp = Instant.now().toString()
        )
    }
}
