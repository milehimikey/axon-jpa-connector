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

package com.axon.jpa.connector.entities

import jakarta.persistence.*

/**
 * JPA Entity representing a saga entry in the Axon Framework.
 * This entity stores saga instances and their state.
 */
@Entity
@Table(name = "saga_entry")
data class SagaEntry(
    @Id
    @Column(name = "saga_id", nullable = false)
    val sagaId: String,

    @Column(name = "revision")
    val revision: String? = null,

    @Column(name = "saga_type")
    val sagaType: String? = null,

    @Lob
    @Column(name = "serialized_saga")
    val serializedSaga: ByteArray? = null
) {
    // Override equals and hashCode because of ByteArray field
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SagaEntry

        if (sagaId != other.sagaId) return false
        if (revision != other.revision) return false
        if (sagaType != other.sagaType) return false
        if (serializedSaga != null) {
            if (other.serializedSaga == null) return false
            if (!serializedSaga.contentEquals(other.serializedSaga)) return false
        } else if (other.serializedSaga != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sagaId.hashCode()
        result = 31 * result + (revision?.hashCode() ?: 0)
        result = 31 * result + (sagaType?.hashCode() ?: 0)
        result = 31 * result + (serializedSaga?.contentHashCode() ?: 0)
        return result
    }
}
