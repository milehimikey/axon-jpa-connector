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

package wtf.milehimikey.jpa.connector.entities

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
