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

/**
 * Criteria for filtering events when loading from an event stream.
 * Provides flexible options for specifying which events to load.
 */
data class EventLoadingCriteria(
    /**
     * The starting sequence number (inclusive, defaults to 0)
     */
    val fromSequenceNumber: Long = 0,

    /**
     * The ending sequence number (inclusive, null means no upper limit)
     */
    val toSequenceNumber: Long? = null,

    /**
     * Filter by specific payload types (empty set means no filtering)
     */
    val payloadTypes: Set<String> = emptySet(),

    /**
     * Filter events after this timestamp (ISO format)
     */
    val fromTimestamp: String? = null,

    /**
     * Filter events before this timestamp (ISO format)
     */
    val toTimestamp: String? = null
)
