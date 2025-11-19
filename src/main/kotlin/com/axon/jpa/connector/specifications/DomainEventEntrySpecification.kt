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

package com.axon.jpa.connector.specifications

import com.axon.jpa.connector.entities.DomainEventEntry
import org.springframework.data.jpa.domain.Specification

/**
 * JPA Specifications for DomainEventEntry queries.
 * Provides reusable query criteria for complex domain event filtering.
 */
object DomainEventEntrySpecification {

    /**
     * Filter by aggregate identifier.
     */
    fun hasAggregateIdentifier(aggregateIdentifier: String): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<String>("aggregateIdentifier"), aggregateIdentifier)
        }
    }

    /**
     * Filter by event type.
     */
    fun hasType(type: String): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<String>("type"), type)
        }
    }

    /**
     * Filter by payload type.
     */
    fun hasPayloadType(payloadType: String): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<String>("payloadType"), payloadType)
        }
    }

    /**
     * Filter by timestamp after the given value.
     */
    fun hasTimestampAfter(timestamp: String): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(root.get("timeStamp"), timestamp)
        }
    }

    /**
     * Filter by timestamp before the given value.
     */
    fun hasTimestampBefore(timestamp: String): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.lessThanOrEqualTo(root.get("timeStamp"), timestamp)
        }
    }

    /**
     * Filter by sequence number greater than or equal to the given value.
     */
    fun hasSequenceNumberGreaterThanOrEqual(sequenceNumber: Long): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(root.get("sequenceNumber"), sequenceNumber)
        }
    }

    /**
     * Filter by sequence number less than or equal to the given value.
     */
    fun hasSequenceNumberLessThanOrEqual(sequenceNumber: Long): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.lessThanOrEqualTo(root.get("sequenceNumber"), sequenceNumber)
        }
    }

    /**
     * Filter by global index greater than or equal to the given value.
     */
    fun hasGlobalIndexGreaterThanOrEqual(globalIndex: Long): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(root.get("globalIndex"), globalIndex)
        }
    }

    /**
     * Filter by global index less than or equal to the given value.
     */
    fun hasGlobalIndexLessThanOrEqual(globalIndex: Long): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.lessThanOrEqualTo(root.get("globalIndex"), globalIndex)
        }
    }

    /**
     * Filter by event identifier.
     */
    fun hasEventIdentifier(eventIdentifier: String): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<String>("eventIdentifier"), eventIdentifier)
        }
    }

    /**
     * Combine multiple specifications with AND.
     */
    fun and(specifications: List<Specification<DomainEventEntry>>): Specification<DomainEventEntry> {
        if (specifications.isEmpty()) {
            return Specification.where(null)
        }

        var result = specifications[0]
        for (i in 1 until specifications.size) {
            result = result.and(specifications[i])
        }

        return result
    }

    /**
     * Combine multiple specifications with OR.
     */
    fun or(specifications: List<Specification<DomainEventEntry>>): Specification<DomainEventEntry> {
        if (specifications.isEmpty()) {
            return Specification.where(null)
        }

        var result = specifications[0]
        for (i in 1 until specifications.size) {
            result = result.or(specifications[i])
        }

        return result
    }
}
