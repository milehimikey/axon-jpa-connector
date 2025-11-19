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

import wtf.milehimikey.jpa.connector.entities.TokenEntry
import wtf.milehimikey.jpa.connector.entities.TokenEntryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for TokenEntry entities.
 * Provides standard CRUD operations and custom query methods for tracking tokens.
 */
@Repository
interface TokenEntryRepository : JpaRepository<TokenEntry, TokenEntryId>, JpaSpecificationExecutor<TokenEntry> {

    /**
     * Find all token entries for a specific processor.
     */
    fun findByProcessorName(processorName: String): List<TokenEntry>

    /**
     * Find all token entries owned by a specific owner.
     */
    fun findByOwner(owner: String): List<TokenEntry>

    /**
     * Find a specific token entry by processor name and segment.
     */
    fun findByProcessorNameAndSegment(processorName: String, segment: Int): TokenEntry?

    /**
     * Find all token entries for a processor ordered by segment.
     */
    @Query("""
        SELECT t FROM TokenEntry t 
        WHERE t.processorName = :processorName 
        ORDER BY t.segment
    """)
    fun findByProcessorNameOrderBySegment(@Param("processorName") processorName: String): List<TokenEntry>

    /**
     * Count the number of distinct processor names.
     */
    @Query("SELECT COUNT(DISTINCT t.processorName) FROM TokenEntry t")
    fun countDistinctProcessorNames(): Long

    /**
     * Count the number of segments for a specific processor.
     */
    @Query("SELECT COUNT(t) FROM TokenEntry t WHERE t.processorName = :processorName")
    fun countSegmentsByProcessorName(@Param("processorName") processorName: String): Long

    /**
     * Find all distinct processor names.
     */
    @Query("SELECT DISTINCT t.processorName FROM TokenEntry t ORDER BY t.processorName")
    fun findDistinctProcessorNames(): List<String>

    /**
     * Find all distinct owners.
     */
    @Query("SELECT DISTINCT t.owner FROM TokenEntry t WHERE t.owner IS NOT NULL ORDER BY t.owner")
    fun findDistinctOwners(): List<String>

    /**
     * Find token entries by processor name and owner.
     */
    fun findByProcessorNameAndOwner(processorName: String, owner: String?): List<TokenEntry>

    /**
     * Delete all token entries for a specific processor.
     */
    fun deleteByProcessorName(processorName: String): Int

    /**
     * Check if a processor has any token entries.
     */
    fun existsByProcessorName(processorName: String): Boolean
}
