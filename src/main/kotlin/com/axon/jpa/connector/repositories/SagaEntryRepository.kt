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

import com.axon.jpa.connector.entities.SagaEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for SagaEntry entities.
 * Provides standard CRUD operations and custom query methods for sagas.
 */
@Repository
interface SagaEntryRepository : JpaRepository<SagaEntry, String> {
    
    /**
     * Find sagas by saga type with pagination.
     */
    fun findBySagaType(sagaType: String, pageable: Pageable): Page<SagaEntry>
    
    /**
     * Find sagas by saga type.
     */
    fun findBySagaType(sagaType: String): List<SagaEntry>
    
    /**
     * Find sagas by revision.
     */
    fun findByRevision(revision: String): List<SagaEntry>
    
    /**
     * Count sagas by saga type.
     */
    @Query("SELECT COUNT(s) FROM SagaEntry s WHERE s.sagaType = :sagaType")
    fun countBySagaType(@Param("sagaType") sagaType: String): Long
    
    /**
     * Count distinct saga types.
     */
    @Query("SELECT COUNT(DISTINCT s.sagaType) FROM SagaEntry s WHERE s.sagaType IS NOT NULL")
    fun countDistinctSagaTypes(): Long
    
    /**
     * Find all distinct saga types.
     */
    @Query("SELECT DISTINCT s.sagaType FROM SagaEntry s WHERE s.sagaType IS NOT NULL ORDER BY s.sagaType")
    fun findDistinctSagaTypes(): List<String>
    
    /**
     * Find all distinct revisions.
     */
    @Query("SELECT DISTINCT s.revision FROM SagaEntry s WHERE s.revision IS NOT NULL ORDER BY s.revision")
    fun findDistinctRevisions(): List<String>
    
    /**
     * Check if a saga exists by saga type.
     */
    fun existsBySagaType(sagaType: String): Boolean
    
    /**
     * Delete sagas by saga type.
     */
    fun deleteBySagaType(sagaType: String): Int
    
    /**
     * Find sagas by saga type and revision.
     */
    fun findBySagaTypeAndRevision(sagaType: String, revision: String): List<SagaEntry>
}
