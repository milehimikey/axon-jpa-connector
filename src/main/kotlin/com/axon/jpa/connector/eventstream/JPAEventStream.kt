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

package com.axon.jpa.connector.eventstream

import com.axon.jpa.connector.entities.DomainEventEntry
import com.axon.jpa.connector.entities.SnapshotEventEntry
import com.axon.jpa.connector.repositories.DomainEventEntryRepository
import com.axon.jpa.connector.repositories.SnapshotEventEntryRepository
import com.axon.jpa.connector.specifications.DomainEventEntrySpecification
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

/**
 * JPA-based implementation of EventStream that loads events from PostgreSQL
 * without deserializing payloads. Provides efficient access to raw event data.
 */
@Service
class JPAEventStream(
    private val domainEventRepository: DomainEventEntryRepository,
    private val snapshotEventRepository: SnapshotEventEntryRepository
) : EventStream {

    override fun load(
        aggregateId: String,
        fromSequenceNumber: Long,
        toSequenceNumber: Long?
    ): List<DomainEventEntry> {
        return if (toSequenceNumber != null) {
            domainEventRepository.findByAggregateIdentifierAndSequenceNumberBetween(
                aggregateId, fromSequenceNumber, toSequenceNumber, Sort.by("sequenceNumber")
            )
        } else {
            domainEventRepository.findByAggregateIdentifierAndSequenceNumberGreaterThanEqual(
                aggregateId, fromSequenceNumber, Sort.by("sequenceNumber")
            )
        }
    }

    override fun load(
        aggregateId: String, 
        criteria: EventLoadingCriteria
    ): List<DomainEventEntry> {
        val specifications = mutableListOf<Specification<DomainEventEntry>>().apply {
            add(DomainEventEntrySpecification.hasAggregateIdentifier(aggregateId))
            add(DomainEventEntrySpecification.hasSequenceNumberGreaterThanOrEqual(criteria.fromSequenceNumber))
            
            criteria.toSequenceNumber?.let { 
                add(DomainEventEntrySpecification.hasSequenceNumberLessThanOrEqual(it))
            }
            
            if (criteria.payloadTypes.isNotEmpty()) {
                add(DomainEventEntrySpecification.hasPayloadTypeIn(criteria.payloadTypes))
            }
            
            criteria.fromTimestamp?.let {
                add(DomainEventEntrySpecification.hasTimestampAfter(it))
            }
            
            criteria.toTimestamp?.let {
                add(DomainEventEntrySpecification.hasTimestampBefore(it))
            }
        }
        
        val combinedSpec = DomainEventEntrySpecification.and(specifications)
        return domainEventRepository.findAll(combinedSpec, Sort.by("sequenceNumber"))
    }

    override fun loadLatestSnapshot(aggregateId: String): SnapshotEventEntry? {
        return snapshotEventRepository.findLatestSnapshotByAggregateIdentifier(aggregateId)
    }

    override fun loadEventsSinceSnapshot(aggregateId: String): AggregateEventStream {
        val latestSnapshot = loadLatestSnapshot(aggregateId)
        val fromSequence = latestSnapshot?.sequenceNumber?.plus(1) ?: 0
        
        val events = load(aggregateId, fromSequence)
        val currentSequence = events.maxOfOrNull { it.sequenceNumber } 
            ?: latestSnapshot?.sequenceNumber 
            ?: 0
        
        return AggregateEventStream(
            aggregateIdentifier = aggregateId,
            events = events,
            latestSnapshot = latestSnapshot,
            currentSequenceNumber = currentSequence
        )
    }
}
