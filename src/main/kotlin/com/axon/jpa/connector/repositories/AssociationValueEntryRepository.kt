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

import com.axon.jpa.connector.entities.AssociationValueEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for AssociationValueEntry entities.
 * Provides standard CRUD operations and custom query methods for saga associations.
 */
@Repository
interface AssociationValueEntryRepository : JpaRepository<AssociationValueEntry, Long> {
    
    /**
     * Find association value entries by saga ID.
     */
    fun findBySagaId(sagaId: String): List<AssociationValueEntry>
    
    /**
     * Find association value entries by saga type.
     */
    fun findBySagaType(sagaType: String): List<AssociationValueEntry>
    
    /**
     * Find association value entries by association key.
     */
    fun findByAssociationKey(associationKey: String): List<AssociationValueEntry>
    
    /**
     * Find association value entries by association key and value.
     */
    fun findByAssociationKeyAndAssociationValue(associationKey: String, associationValue: String): List<AssociationValueEntry>
    
    /**
     * Find association value entries by saga ID and association key.
     */
    fun findBySagaIdAndAssociationKey(sagaId: String, associationKey: String): List<AssociationValueEntry>
    
    /**
     * Find association value entries by saga type and association key.
     */
    fun findBySagaTypeAndAssociationKey(sagaType: String, associationKey: String): List<AssociationValueEntry>
    
    /**
     * Find saga IDs by association key and value.
     */
    @Query("SELECT DISTINCT a.sagaId FROM AssociationValueEntry a WHERE a.associationKey = :key AND a.associationValue = :value")
    fun findSagaIdsByAssociationKeyAndValue(@Param("key") associationKey: String, @Param("value") associationValue: String): List<String>
    
    /**
     * Find saga IDs by association key.
     */
    @Query("SELECT DISTINCT a.sagaId FROM AssociationValueEntry a WHERE a.associationKey = :key")
    fun findSagaIdsByAssociationKey(@Param("key") associationKey: String): List<String>
    
    /**
     * Count associations by saga ID.
     */
    @Query("SELECT COUNT(a) FROM AssociationValueEntry a WHERE a.sagaId = :sagaId")
    fun countBySagaId(@Param("sagaId") sagaId: String): Long
    
    /**
     * Count associations by saga type.
     */
    @Query("SELECT COUNT(a) FROM AssociationValueEntry a WHERE a.sagaType = :sagaType")
    fun countBySagaType(@Param("sagaType") sagaType: String): Long
    
    /**
     * Find all distinct association keys.
     */
    @Query("SELECT DISTINCT a.associationKey FROM AssociationValueEntry a ORDER BY a.associationKey")
    fun findDistinctAssociationKeys(): List<String>
    
    /**
     * Find all distinct saga types.
     */
    @Query("SELECT DISTINCT a.sagaType FROM AssociationValueEntry a WHERE a.sagaType IS NOT NULL ORDER BY a.sagaType")
    fun findDistinctSagaTypes(): List<String>
    
    /**
     * Delete associations by saga ID.
     */
    fun deleteBySagaId(sagaId: String): Int
    
    /**
     * Delete associations by saga ID and association key.
     */
    fun deleteBySagaIdAndAssociationKey(sagaId: String, associationKey: String): Int
    
    /**
     * Check if an association exists.
     */
    fun existsBySagaIdAndAssociationKeyAndAssociationValue(sagaId: String, associationKey: String, associationValue: String?): Boolean
}
