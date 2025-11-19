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
