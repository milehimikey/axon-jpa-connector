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
import java.time.LocalDateTime

/**
 * JPA Entity representing a dead letter entry in the Axon Framework.
 * This entity stores events that could not be processed successfully.
 */
@Entity
@Table(name = "dead_letter_entry")
data class DeadLetterEntry(
    @Id
    @Column(name = "dead_letter_id", nullable = false)
    val deadLetterId: String,

    @Column(name = "sequence_identifier", nullable = false)
    val sequenceIdentifier: String,

    @Column(name = "sequence_index", nullable = false)
    val sequenceIndex: Long,

    @Column(name = "message_type", nullable = false)
    val messageType: String,

    @Column(name = "event_identifier", nullable = false)
    val messageId: String,

    @Column(name = "cause_message")
    val causeMessage: String? = null,

    @Column(name = "cause_type")
    val causeType: String? = null,

    @Lob
    @Column(name = "diagnostics")
    val diagnostics: ByteArray? = null,

    @Column(name = "enqueued_at", nullable = false)
    val enqueuedAt: LocalDateTime,

    @Column(name = "last_touched", nullable = false)
    val lastTouched: LocalDateTime,

    @Column(name = "processing_started")
    val processingStarted: LocalDateTime? = null,

    @Column(name = "processing_group", nullable = false)
    val processingGroup: String
) {
    // Override equals and hashCode because of ByteArray field
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeadLetterEntry

        if (deadLetterId != other.deadLetterId) return false
        if (sequenceIdentifier != other.sequenceIdentifier) return false
        if (sequenceIndex != other.sequenceIndex) return false
        if (messageType != other.messageType) return false
        if (messageId != other.messageId) return false
        if (causeMessage != other.causeMessage) return false
        if (causeType != other.causeType) return false
        if (diagnostics != null) {
            if (other.diagnostics == null) return false
            if (!diagnostics.contentEquals(other.diagnostics)) return false
        } else if (other.diagnostics != null) return false
        if (enqueuedAt != other.enqueuedAt) return false
        if (lastTouched != other.lastTouched) return false
        if (processingStarted != other.processingStarted) return false
        if (processingGroup != other.processingGroup) return false

        return true
    }

    override fun hashCode(): Int {
        var result = deadLetterId.hashCode()
        result = 31 * result + sequenceIdentifier.hashCode()
        result = 31 * result + sequenceIndex.hashCode()
        result = 31 * result + messageType.hashCode()
        result = 31 * result + messageId.hashCode()
        result = 31 * result + (causeMessage?.hashCode() ?: 0)
        result = 31 * result + (causeType?.hashCode() ?: 0)
        result = 31 * result + (diagnostics?.contentHashCode() ?: 0)
        result = 31 * result + enqueuedAt.hashCode()
        result = 31 * result + lastTouched.hashCode()
        result = 31 * result + (processingStarted?.hashCode() ?: 0)
        result = 31 * result + processingGroup.hashCode()
        return result
    }
}
