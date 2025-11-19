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

package wtf.milehimikey.jpa.connector.specifications

import wtf.milehimikey.jpa.connector.entities.DomainEventEntry
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
     * Filter by payload types in the given set.
     */
    fun hasPayloadTypeIn(payloadTypes: Set<String>): Specification<DomainEventEntry> {
        return Specification { root, _, criteriaBuilder ->
            root.get<String>("payloadType").`in`(payloadTypes)
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
