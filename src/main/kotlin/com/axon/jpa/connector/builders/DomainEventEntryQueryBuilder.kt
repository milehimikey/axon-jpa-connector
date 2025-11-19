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
