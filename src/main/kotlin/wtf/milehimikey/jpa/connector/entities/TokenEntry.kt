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
import java.io.Serializable

/**
 * JPA Entity representing a token entry in the Axon Framework.
 * This entity stores tracking tokens for event processors to track their progress.
 */
@Entity
@Table(name = "token_entry")
@IdClass(TokenEntryId::class)
data class TokenEntry(
    @Id
    @Column(name = "processor_name", nullable = false)
    val processorName: String,

    @Id
    @Column(name = "segment", nullable = false)
    val segment: Int,

    @Lob
    @Column(name = "token")
    val token: ByteArray? = null,

    @Column(name = "token_type")
    val tokenType: String? = null,

    @Column(name = "timestamp")
    val timestamp: String? = null,

    @Column(name = "owner")
    val owner: String? = null
) {
    // Override equals and hashCode because of ByteArray field
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TokenEntry

        if (processorName != other.processorName) return false
        if (segment != other.segment) return false
        if (token != null) {
            if (other.token == null) return false
            if (!token.contentEquals(other.token)) return false
        } else if (other.token != null) return false
        if (tokenType != other.tokenType) return false
        if (timestamp != other.timestamp) return false
        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = processorName.hashCode()
        result = 31 * result + segment
        result = 31 * result + (token?.contentHashCode() ?: 0)
        result = 31 * result + (tokenType?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)
        return result
    }
}

/**
 * Composite key class for TokenEntry
 */
data class TokenEntryId(
    val processorName: String = "",
    val segment: Int = 0
) : Serializable
