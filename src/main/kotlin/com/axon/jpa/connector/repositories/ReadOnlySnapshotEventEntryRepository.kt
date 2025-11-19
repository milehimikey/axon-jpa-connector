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

package com.axon.jpa.connector.repositories

import com.axon.jpa.connector.entities.SnapshotEventEntry
import com.axon.jpa.connector.entities.SnapshotEventEntryId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * Read-only repository for SnapshotEventEntry entities.
 * This interface provides only query methods for data analysis and troubleshooting.
 * No write operations are exposed to prevent accidental data modification.
 */
interface ReadOnlySnapshotEventEntryRepository : Repository<SnapshotEventEntry, SnapshotEventEntryId> {

    // Basic read operations
    fun findById(id: SnapshotEventEntryId): Optional<SnapshotEventEntry>
    fun findAll(): List<SnapshotEventEntry>
    fun findAll(sort: Sort): List<SnapshotEventEntry>
    fun findAll(pageable: Pageable): Page<SnapshotEventEntry>
    fun count(): Long
    fun existsById(id: SnapshotEventEntryId): Boolean

    // Specification-based queries
    fun findAll(spec: Specification<SnapshotEventEntry>): List<SnapshotEventEntry>
    fun findAll(spec: Specification<SnapshotEventEntry>, sort: Sort): List<SnapshotEventEntry>
    fun findAll(spec: Specification<SnapshotEventEntry>, pageable: Pageable): Page<SnapshotEventEntry>
    fun count(spec: Specification<SnapshotEventEntry>): Long

    // Custom query methods for snapshot analysis
    fun findByAggregateIdentifier(aggregateIdentifier: String): List<SnapshotEventEntry>
    fun findByEventIdentifier(eventIdentifier: String): Optional<SnapshotEventEntry>
    fun findByPayloadType(payloadType: String, pageable: Pageable): Page<SnapshotEventEntry>

    // Latest snapshot queries for EventStream
    @Query("""
        SELECT s FROM SnapshotEventEntry s 
        WHERE s.aggregateIdentifier = :aggregateId 
        AND s.sequenceNumber = (
            SELECT MAX(s2.sequenceNumber) FROM SnapshotEventEntry s2 
            WHERE s2.aggregateIdentifier = :aggregateId
        )
    """)
    fun findLatestSnapshotByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): SnapshotEventEntry?

    // Snapshot statistics
    @Query("""
        SELECT COUNT(DISTINCT s.aggregateIdentifier) FROM SnapshotEventEntry s
    """)
    fun countDistinctAggregateIdentifiers(): Long

    @Query("""
        SELECT MAX(s.sequenceNumber) FROM SnapshotEventEntry s 
        WHERE s.aggregateIdentifier = :aggregateId
    """)
    fun findMaxSequenceNumberByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): Long?

    @Query("""
        SELECT COUNT(s) FROM SnapshotEventEntry s 
        WHERE s.aggregateIdentifier = :aggregateId
    """)
    fun countByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): Long

    // Payload type analysis
    @Query("""
        SELECT DISTINCT s.payloadType FROM SnapshotEventEntry s 
        ORDER BY s.payloadType
    """)
    fun findDistinctPayloadTypes(): List<String>

    @Query("""
        SELECT s.payloadType, COUNT(s) FROM SnapshotEventEntry s 
        GROUP BY s.payloadType 
        ORDER BY COUNT(s) DESC
    """)
    fun countByPayloadType(): List<Array<Any>>

    // Time-based queries
    @Query("""
        SELECT s FROM SnapshotEventEntry s 
        WHERE s.timeStamp >= :fromTime AND s.timeStamp <= :toTime 
        ORDER BY s.timeStamp
    """)
    fun findByTimeStampBetween(
        @Param("fromTime") fromTime: String,
        @Param("toTime") toTime: String
    ): List<SnapshotEventEntry>
}
