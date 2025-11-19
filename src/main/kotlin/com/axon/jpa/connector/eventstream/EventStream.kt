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
 * Interface for loading events from an event stream without deserializing payloads.
 * Provides access to raw event data stored in the underlying persistence layer.
 */
interface EventStream {

    /**
     * Load events for an aggregate without deserializing payloads.
     *
     * @param aggregateId The identifier of the aggregate
     * @param fromSequenceNumber The starting sequence number (inclusive, defaults to 0)
     * @param toSequenceNumber The ending sequence number (inclusive, null means no upper limit)
     * @return List of domain events ordered by sequence number
     */
    fun load(
        aggregateId: String,
        fromSequenceNumber: Long = 0,
        toSequenceNumber: Long? = null
    ): List<DomainEventEntry>

    /**
     * Load events for an aggregate with optional filtering criteria.
     *
     * @param aggregateId The identifier of the aggregate
     * @param criteria The filtering criteria to apply
     * @return List of domain events matching the criteria, ordered by sequence number
     */
    fun load(
        aggregateId: String,
        criteria: EventLoadingCriteria
    ): List<DomainEventEntry>

    /**
     * Load the latest snapshot for an aggregate if available.
     *
     * @param aggregateId The identifier of the aggregate
     * @return The latest snapshot event entry, or null if no snapshot exists
     */
    fun loadLatestSnapshot(aggregateId: String): SnapshotEventEntry?

    /**
     * Load events since the last snapshot for optimal aggregate reconstruction.
     * This method combines snapshot loading with subsequent events for performance.
     *
     * @param aggregateId The identifier of the aggregate
     * @return An aggregate event stream containing the snapshot and subsequent events
     */
    fun loadEventsSinceSnapshot(aggregateId: String): AggregateEventStream
}
