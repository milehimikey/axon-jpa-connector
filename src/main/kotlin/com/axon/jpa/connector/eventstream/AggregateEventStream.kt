/*
 * Copyright (c) 2024. MileHiMikey
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

package com.axon.jpa.connector.eventstream

import com.axon.jpa.connector.entities.DomainEventEntry
import com.axon.jpa.connector.entities.SnapshotEventEntry

/**
 * Represents a complete event stream for an aggregate, including both
 * the latest snapshot (if available) and subsequent events.
 * This provides an optimized view for aggregate reconstruction.
 */
data class AggregateEventStream(
    /**
     * The identifier of the aggregate
     */
    val aggregateIdentifier: String,

    /**
     * The events that occurred after the latest snapshot, ordered by sequence number
     */
    val events: List<DomainEventEntry>,

    /**
     * The latest snapshot for this aggregate, if available
     */
    val latestSnapshot: SnapshotEventEntry?,

    /**
     * The current (highest) sequence number for this aggregate
     */
    val currentSequenceNumber: Long
) {
    /**
     * Returns true if this aggregate has a snapshot available
     */
    fun hasSnapshot(): Boolean = latestSnapshot != null

    /**
     * Returns the total number of events since the snapshot (or from the beginning if no snapshot)
     */
    fun eventCount(): Int = events.size

    /**
     * Returns true if there are no events (and no snapshot)
     */
    fun isEmpty(): Boolean = events.isEmpty() && latestSnapshot == null
}
