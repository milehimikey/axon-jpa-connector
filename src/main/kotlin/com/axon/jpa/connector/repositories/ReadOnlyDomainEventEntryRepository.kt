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

import com.axon.jpa.connector.entities.DomainEventEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * Read-only repository for DomainEventEntry entities.
 * This interface provides only query methods for data analysis and troubleshooting.
 * No write operations are exposed to prevent accidental data modification.
 */
interface ReadOnlyDomainEventEntryRepository : Repository<DomainEventEntry, Long> {

    // Basic read operations
    fun findById(id: Long): Optional<DomainEventEntry>
    fun findAll(): List<DomainEventEntry>
    fun findAll(sort: Sort): List<DomainEventEntry>
    fun findAll(pageable: Pageable): Page<DomainEventEntry>
    fun count(): Long
    fun existsById(id: Long): Boolean

    // Specification-based queries
    fun findAll(spec: Specification<DomainEventEntry>): List<DomainEventEntry>
    fun findAll(spec: Specification<DomainEventEntry>, sort: Sort): List<DomainEventEntry>
    fun findAll(spec: Specification<DomainEventEntry>, pageable: Pageable): Page<DomainEventEntry>
    fun count(spec: Specification<DomainEventEntry>): Long

    // Custom query methods for event analysis
    fun findByAggregateIdentifier(aggregateIdentifier: String): List<DomainEventEntry>
    fun findByAggregateIdentifierOrderBySequenceNumber(aggregateIdentifier: String): List<DomainEventEntry>
    fun findByPayloadType(payloadType: String, pageable: Pageable): Page<DomainEventEntry>
    fun findByEventIdentifier(eventIdentifier: String): Optional<DomainEventEntry>

    // Sequence number range queries for EventStream
    fun findByAggregateIdentifierAndSequenceNumberBetween(
        aggregateIdentifier: String,
        fromSequence: Long,
        toSequence: Long,
        sort: Sort
    ): List<DomainEventEntry>

    fun findByAggregateIdentifierAndSequenceNumberGreaterThanEqual(
        aggregateIdentifier: String,
        fromSequence: Long,
        sort: Sort
    ): List<DomainEventEntry>

    // Aggregate statistics
    @Query("""
        SELECT COUNT(DISTINCT d.aggregateIdentifier) FROM DomainEventEntry d
    """)
    fun countDistinctAggregateIdentifiers(): Long

    @Query("""
        SELECT MAX(d.sequenceNumber) FROM DomainEventEntry d 
        WHERE d.aggregateIdentifier = :aggregateId
    """)
    fun findMaxSequenceNumberByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): Long?

    @Query("""
        SELECT COUNT(d) FROM DomainEventEntry d 
        WHERE d.aggregateIdentifier = :aggregateId
    """)
    fun countByAggregateIdentifier(@Param("aggregateId") aggregateIdentifier: String): Long

    // Payload type analysis
    @Query("""
        SELECT DISTINCT d.payloadType FROM DomainEventEntry d 
        ORDER BY d.payloadType
    """)
    fun findDistinctPayloadTypes(): List<String>

    @Query("""
        SELECT d.payloadType, COUNT(d) FROM DomainEventEntry d 
        GROUP BY d.payloadType 
        ORDER BY COUNT(d) DESC
    """)
    fun countByPayloadType(): List<Array<Any>>

    // Time-based queries
    @Query("""
        SELECT d FROM DomainEventEntry d 
        WHERE d.timeStamp >= :fromTime AND d.timeStamp <= :toTime 
        ORDER BY d.timeStamp
    """)
    fun findByTimeStampBetween(
        @Param("fromTime") fromTime: String,
        @Param("toTime") toTime: String
    ): List<DomainEventEntry>
}
