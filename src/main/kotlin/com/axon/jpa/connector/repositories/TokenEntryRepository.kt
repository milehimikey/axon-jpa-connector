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

import com.axon.jpa.connector.entities.TokenEntry
import com.axon.jpa.connector.entities.TokenEntryId
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
