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

package com.axon.jpa.connector.entities

import jakarta.persistence.*

/**
 * JPA Entity representing an association value entry in the Axon Framework.
 * This entity stores associations between sagas and domain objects.
 */
@Entity
@Table(
    name = "association_value_entry",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["saga_id", "association_key", "association_value"])
    ]
)
data class AssociationValueEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "association_key", nullable = false)
    val associationKey: String,

    @Column(name = "association_value")
    val associationValue: String? = null,

    @Column(name = "saga_id", nullable = false)
    val sagaId: String,

    @Column(name = "saga_type")
    val sagaType: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssociationValueEntry

        if (id != other.id) return false
        if (associationKey != other.associationKey) return false
        if (associationValue != other.associationValue) return false
        if (sagaId != other.sagaId) return false
        if (sagaType != other.sagaType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + associationKey.hashCode()
        result = 31 * result + (associationValue?.hashCode() ?: 0)
        result = 31 * result + sagaId.hashCode()
        result = 31 * result + (sagaType?.hashCode() ?: 0)
        return result
    }
}
