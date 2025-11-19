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
