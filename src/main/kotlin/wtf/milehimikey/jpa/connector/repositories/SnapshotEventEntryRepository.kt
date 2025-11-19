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

import wtf.milehimikey.jpa.connector.entities.SnapshotEventEntry
import wtf.milehimikey.jpa.connector.entities.SnapshotEventEntryId
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
