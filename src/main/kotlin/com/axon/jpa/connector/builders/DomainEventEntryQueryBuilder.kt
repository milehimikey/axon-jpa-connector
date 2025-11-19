/*
 * Copyright (c) 2025 MileHiMikey
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

package com.axon.jpa.connector.builders

import com.axon.jpa.connector.entities.DomainEventEntry
import com.axon.jpa.connector.specifications.DomainEventEntrySpecification
import org.springframework.data.jpa.domain.Specification

/**
 * Fluent query builder for DomainEventEntry specifications.
 * Provides a convenient way to build complex queries using method chaining.
 */
class DomainEventEntryQueryBuilder {
    private val specifications = mutableListOf<Specification<DomainEventEntry>>()

    /**
     * Add a filter by aggregate identifier.
     */
    fun withAggregateIdentifier(aggregateIdentifier: String?): DomainEventEntryQueryBuilder {
        if (!aggregateIdentifier.isNullOrBlank()) {
            specifications.add(DomainEventEntrySpecification.hasAggregateIdentifier(aggregateIdentifier))
        }
        return this
    }

    /**
     * Add a filter by event type.
     */
    fun withType(type: String?): DomainEventEntryQueryBuilder {
        if (!type.isNullOrBlank()) {
            specifications.add(DomainEventEntrySpecification.hasType(type))
        }
        return this
    }

    /**
     * Add a filter by payload type.
     */
    fun withPayloadType(payloadType: String?): DomainEventEntryQueryBuilder {
        if (!payloadType.isNullOrBlank()) {
            specifications.add(DomainEventEntrySpecification.hasPayloadType(payloadType))
        }
        return this
    }

    /**
     * Add a filter by timestamp range.
     */
    fun withTimestampRange(fromTimestamp: String?, toTimestamp: String?): DomainEventEntryQueryBuilder {
        if (!fromTimestamp.isNullOrBlank()) {
            specifications.add(DomainEventEntrySpecification.hasTimestampAfter(fromTimestamp))
        }
        if (!toTimestamp.isNullOrBlank()) {
            specifications.add(DomainEventEntrySpecification.hasTimestampBefore(toTimestamp))
        }
        return this
    }

    /**
     * Add a filter by sequence number range.
     */
    fun withSequenceNumberRange(fromSequenceNumber: Long?, toSequenceNumber: Long?): DomainEventEntryQueryBuilder {
        if (fromSequenceNumber != null) {
            specifications.add(DomainEventEntrySpecification.hasSequenceNumberGreaterThanOrEqual(fromSequenceNumber))
        }
        if (toSequenceNumber != null) {
            specifications.add(DomainEventEntrySpecification.hasSequenceNumberLessThanOrEqual(toSequenceNumber))
        }
        return this
    }

    /**
     * Add a filter by global index range.
     */
    fun withGlobalIndexRange(fromGlobalIndex: Long?, toGlobalIndex: Long?): DomainEventEntryQueryBuilder {
        if (fromGlobalIndex != null) {
            specifications.add(DomainEventEntrySpecification.hasGlobalIndexGreaterThanOrEqual(fromGlobalIndex))
        }
        if (toGlobalIndex != null) {
            specifications.add(DomainEventEntrySpecification.hasGlobalIndexLessThanOrEqual(toGlobalIndex))
        }
        return this
    }

    /**
     * Add a filter by event identifier.
     */
    fun withEventIdentifier(eventIdentifier: String?): DomainEventEntryQueryBuilder {
        if (!eventIdentifier.isNullOrBlank()) {
            specifications.add(DomainEventEntrySpecification.hasEventIdentifier(eventIdentifier))
        }
        return this
    }

    /**
     * Build the final specification by combining all added specifications with AND.
     */
    fun build(): Specification<DomainEventEntry> {
        return DomainEventEntrySpecification.and(specifications)
    }

    /**
     * Build the final specification by combining all added specifications with OR.
     */
    fun buildOr(): Specification<DomainEventEntry> {
        return DomainEventEntrySpecification.or(specifications)
    }

    companion object {
        /**
         * Create a new query builder.
         */
        fun builder(): DomainEventEntryQueryBuilder {
            return DomainEventEntryQueryBuilder()
        }
    }
}
