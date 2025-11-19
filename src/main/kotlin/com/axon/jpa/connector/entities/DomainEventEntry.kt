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
 * JPA Entity representing a domain event entry in the Axon Framework event store.
 * This entity stores the actual events with their metadata and payload.
 */
@Entity
@Table(name = "domain_event_entry")
data class DomainEventEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "global_index")
    val globalIndex: Long = 0,

    @Column(name = "aggregate_identifier", nullable = false)
    val aggregateIdentifier: String,

    @Column(name = "sequence_number", nullable = false)
    val sequenceNumber: Long,

    @Column(name = "type")
    val type: String? = null,

    @Column(name = "event_identifier", nullable = false)
    val eventIdentifier: String,

    @Lob
    @Column(name = "meta_data")
    val metaData: ByteArray? = null,

    @Lob
    @Column(name = "payload", nullable = false)
    val payload: ByteArray,

    @Column(name = "payload_revision")
    val payloadRevision: String? = null,

    @Column(name = "payload_type", nullable = false)
    val payloadType: String,

    @Column(name = "time_stamp", nullable = false)
    val timeStamp: String
) {
    // Override equals and hashCode because of ByteArray fields
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainEventEntry

        if (globalIndex != other.globalIndex) return false
        if (aggregateIdentifier != other.aggregateIdentifier) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (type != other.type) return false
        if (eventIdentifier != other.eventIdentifier) return false
        if (metaData != null) {
            if (other.metaData == null) return false
            if (!metaData.contentEquals(other.metaData)) return false
        } else if (other.metaData != null) return false
        if (!payload.contentEquals(other.payload)) return false
        if (payloadRevision != other.payloadRevision) return false
        if (payloadType != other.payloadType) return false
        if (timeStamp != other.timeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = globalIndex.hashCode()
        result = 31 * result + aggregateIdentifier.hashCode()
        result = 31 * result + sequenceNumber.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + eventIdentifier.hashCode()
        result = 31 * result + (metaData?.contentHashCode() ?: 0)
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + (payloadRevision?.hashCode() ?: 0)
        result = 31 * result + payloadType.hashCode()
        result = 31 * result + timeStamp.hashCode()
        return result
    }
}
