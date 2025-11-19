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

package wtf.milehimikey.jpa.connector.repositories

import wtf.milehimikey.jpa.connector.entities.DomainEventEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for DomainEventEntry entities.
 * Provides standard CRUD operations and custom query methods for domain events.
 */
@Repository
interface DomainEventEntryRepository : JpaRepository<DomainEventEntry, Long>, JpaSpecificationExecutor<DomainEventEntry> {

    /**
     * Find domain events by aggregate identifier with pagination.
     */
    fun findByAggregateIdentifier(aggregateIdentifier: String, pageable: Pageable): Page<DomainEventEntry>

    /**
     * Find domain events by type with pagination.
     */
    fun findByType(type: String, pageable: Pageable): Page<DomainEventEntry>

    /**
     * Find domain events by payload type with pagination.
     */
    fun findByPayloadType(payloadType: String, pageable: Pageable): Page<DomainEventEntry>

    /**
     * Find domain events with timestamp greater than or equal to the specified value.
     */
    fun findByTimeStampGreaterThanEqual(timestamp: String, pageable: Pageable): Page<DomainEventEntry>

    /**
     * Find domain events with timestamp less than or equal to the specified value.
     */
    fun findByTimeStampLessThanEqual(timestamp: String, pageable: Pageable): Page<DomainEventEntry>

    /**
     * Find domain events by aggregate identifier and type.
     */
    fun findByAggregateIdentifierAndType(
        aggregateIdentifier: String,
        type: String,
        pageable: Pageable
    ): Page<DomainEventEntry>

    /**
     * Find domain events by aggregate identifier and sequence number range.
     */
    @Query("""
        SELECT d FROM DomainEventEntry d 
        WHERE d.aggregateIdentifier = :aggregateId 
        AND d.sequenceNumber BETWEEN :fromSeq AND :toSeq 
        ORDER BY d.sequenceNumber
    """)
    fun findByAggregateIdentifierAndSequenceNumberBetween(
        @Param("aggregateId") aggregateIdentifier: String,
        @Param("fromSeq") fromSequenceNumber: Long,
        @Param("toSeq") toSequenceNumber: Long,
        pageable: Pageable
    ): Page<DomainEventEntry>

    /**
     * Count distinct aggregate identifiers.
     */
    @Query("SELECT COUNT(DISTINCT d.aggregateIdentifier) FROM DomainEventEntry d")
    fun countDistinctAggregateIdentifiers(): Long

    /**
     * Count distinct payload types.
     */
    @Query("SELECT COUNT(DISTINCT d.payloadType) FROM DomainEventEntry d")
    fun countDistinctPayloadTypes(): Long

    /**
     * Find the latest sequence number for an aggregate.
     */
    @Query("""
        SELECT MAX(d.sequenceNumber) FROM DomainEventEntry d
        WHERE d.aggregateIdentifier = :aggregateId
    """)
    fun findMaxSequenceNumberByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): Long?

    /**
     * Find events by sequence number range.
     */
    fun findByAggregateIdentifierAndSequenceNumberBetween(
        aggregateIdentifier: String,
        fromSequence: Long,
        toSequence: Long,
        sort: Sort
    ): List<DomainEventEntry>

    /**
     * Find events from a specific sequence number.
     */
    fun findByAggregateIdentifierAndSequenceNumberGreaterThanEqual(
        aggregateIdentifier: String,
        fromSequence: Long,
        sort: Sort
    ): List<DomainEventEntry>
}
