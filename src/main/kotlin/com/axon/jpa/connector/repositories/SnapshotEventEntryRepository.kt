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

import com.axon.jpa.connector.entities.SnapshotEventEntry
import com.axon.jpa.connector.entities.SnapshotEventEntryId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for SnapshotEventEntry entities.
 * Provides standard CRUD operations and custom query methods for snapshot events.
 */
@Repository
interface SnapshotEventEntryRepository : JpaRepository<SnapshotEventEntry, SnapshotEventEntryId> {
    
    /**
     * Find snapshot events by aggregate identifier with pagination.
     */
    fun findByAggregateIdentifier(aggregateIdentifier: String, pageable: Pageable): Page<SnapshotEventEntry>
    
    /**
     * Find snapshot events by type with pagination.
     */
    fun findByType(type: String, pageable: Pageable): Page<SnapshotEventEntry>
    
    /**
     * Find snapshot events by payload type with pagination.
     */
    fun findByPayloadType(payloadType: String, pageable: Pageable): Page<SnapshotEventEntry>
    
    /**
     * Find snapshot events by aggregate identifier and type.
     */
    fun findByAggregateIdentifierAndType(
        aggregateIdentifier: String, 
        type: String, 
        pageable: Pageable
    ): Page<SnapshotEventEntry>
    
    /**
     * Find the latest snapshot for an aggregate.
     */
    @Query("""
        SELECT s FROM SnapshotEventEntry s 
        WHERE s.aggregateIdentifier = :aggregateId 
        ORDER BY s.sequenceNumber DESC
    """)
    fun findLatestSnapshotByAggregateIdentifier(
        @Param("aggregateId") aggregateIdentifier: String,
        pageable: Pageable
    ): Page<SnapshotEventEntry>
    
    /**
     * Find snapshot with the highest sequence number for an aggregate.
     */
    @Query("""
        SELECT s FROM SnapshotEventEntry s 
        WHERE s.aggregateIdentifier = :aggregateId 
        AND s.sequenceNumber = (
            SELECT MAX(s2.sequenceNumber) FROM SnapshotEventEntry s2 
            WHERE s2.aggregateIdentifier = :aggregateId
        )
    """)
    fun findLatestSnapshotByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): SnapshotEventEntry?
    
    /**
     * Count distinct aggregate identifiers.
     */
    @Query("SELECT COUNT(DISTINCT s.aggregateIdentifier) FROM SnapshotEventEntry s")
    fun countDistinctAggregateIdentifiers(): Long
    
    /**
     * Count distinct payload types.
     */
    @Query("SELECT COUNT(DISTINCT s.payloadType) FROM SnapshotEventEntry s")
    fun countDistinctPayloadTypes(): Long
    
    /**
     * Delete snapshots older than a specific sequence number for an aggregate.
     */
    @Query("""
        DELETE FROM SnapshotEventEntry s 
        WHERE s.aggregateIdentifier = :aggregateId 
        AND s.sequenceNumber < :sequenceNumber
    """)
    fun deleteOldSnapshotsByAggregateIdentifierAndSequenceNumber(
        @Param("aggregateId") aggregateIdentifier: String,
        @Param("sequenceNumber") sequenceNumber: Long
    ): Int
}
