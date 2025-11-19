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

import com.axon.jpa.connector.entities.DeadLetterEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Repository interface for DeadLetterEntry entities.
 * Provides standard CRUD operations and custom query methods for dead letter entries.
 */
@Repository
interface DeadLetterEntryRepository : JpaRepository<DeadLetterEntry, String>, JpaSpecificationExecutor<DeadLetterEntry> {
    
    /**
     * Find dead letter entries by processing group.
     */
    fun findByProcessingGroup(processingGroup: String): List<DeadLetterEntry>
    
    /**
     * Find dead letter entries by processing group with pagination.
     */
    fun findByProcessingGroup(processingGroup: String, pageable: Pageable): Page<DeadLetterEntry>
    
    /**
     * Find dead letter entries by message type.
     */
    fun findByMessageType(messageType: String): List<DeadLetterEntry>
    
    /**
     * Find dead letter entries by message type with pagination.
     */
    fun findByMessageType(messageType: String, pageable: Pageable): Page<DeadLetterEntry>
    
    /**
     * Find dead letter entries by sequence identifier.
     */
    fun findBySequenceIdentifier(sequenceIdentifier: String): List<DeadLetterEntry>
    
    /**
     * Find dead letter entries by processing group and message type.
     */
    fun findByProcessingGroupAndMessageType(processingGroup: String, messageType: String): List<DeadLetterEntry>
    
    /**
     * Count distinct processing groups.
     */
    @Query("SELECT COUNT(DISTINCT d.processingGroup) FROM DeadLetterEntry d")
    fun countDistinctProcessingGroups(): Long
    
    /**
     * Count distinct sequence identifiers (queue IDs).
     */
    @Query("SELECT COUNT(DISTINCT d.sequenceIdentifier) FROM DeadLetterEntry d")
    fun countDistinctQueueIds(): Long
    
    /**
     * Count distinct message types.
     */
    @Query("SELECT COUNT(DISTINCT d.messageType) FROM DeadLetterEntry d")
    fun countDistinctMessageTypes(): Long
    
    /**
     * Find all distinct processing groups.
     */
    @Query("SELECT DISTINCT d.processingGroup FROM DeadLetterEntry d ORDER BY d.processingGroup")
    fun findDistinctProcessingGroups(): List<String>
    
    /**
     * Find all distinct message types.
     */
    @Query("SELECT DISTINCT d.messageType FROM DeadLetterEntry d ORDER BY d.messageType")
    fun findDistinctMessageTypes(): List<String>
    
    /**
     * Find dead letter entries enqueued after a specific date.
     */
    fun findByEnqueuedAtAfter(enqueuedAt: LocalDateTime): List<DeadLetterEntry>
    
    /**
     * Find dead letter entries last touched before a specific date.
     */
    fun findByLastTouchedBefore(lastTouched: LocalDateTime): List<DeadLetterEntry>
    
    /**
     * Count dead letter entries by processing group.
     */
    @Query("SELECT COUNT(d) FROM DeadLetterEntry d WHERE d.processingGroup = :processingGroup")
    fun countByProcessingGroup(@Param("processingGroup") processingGroup: String): Long
    
    /**
     * Delete old dead letter entries before a specific date.
     */
    @Query("DELETE FROM DeadLetterEntry d WHERE d.lastTouched < :cutoffDate")
    fun deleteByLastTouchedBefore(@Param("cutoffDate") cutoffDate: LocalDateTime): Int
}
