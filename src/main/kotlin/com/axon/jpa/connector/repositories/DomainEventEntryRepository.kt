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

package com.axon.jpa.connector.repositories

import com.axon.jpa.connector.entities.DomainEventEntry
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
