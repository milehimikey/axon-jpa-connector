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

package wtf.milehimikey.jpa.connector.config

import wtf.milehimikey.jpa.connector.eventstream.EventStream
import wtf.milehimikey.jpa.connector.eventstream.JPAEventStream
import wtf.milehimikey.jpa.connector.eventstream.ReadOnlyEventStream
import wtf.milehimikey.jpa.connector.repositories.DomainEventEntryRepository
import wtf.milehimikey.jpa.connector.repositories.ReadOnlyDomainEventEntryRepository
import wtf.milehimikey.jpa.connector.repositories.ReadOnlySnapshotEventEntryRepository
import wtf.milehimikey.jpa.connector.repositories.SnapshotEventEntryRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Auto-configuration for EventStream functionality.
 * Provides read-only implementation by default for data analysis and troubleshooting.
 * Full read-write implementation is available when explicitly enabled.
 */
@Configuration
class EventStreamConfiguration {

    /**
     * Provides a read-only EventStream implementation for data analysis and troubleshooting.
     * This is the default implementation to prevent accidental data modification.
     */
    @Bean
    @ConditionalOnMissingBean(EventStream::class)
    @ConditionalOnProperty(name = ["axon.jpa.allow-writes"], havingValue = "false", matchIfMissing = true)
    fun readOnlyEventStream(
        domainEventRepository: ReadOnlyDomainEventEntryRepository,
        snapshotEventRepository: ReadOnlySnapshotEventEntryRepository
    ): EventStream {
        return ReadOnlyEventStream(domainEventRepository, snapshotEventRepository)
    }

    /**
     * Provides a full read-write EventStream implementation.
     * Only available when writes are explicitly enabled.
     */
    @Bean
    @ConditionalOnMissingBean(EventStream::class)
    @ConditionalOnProperty(name = ["axon.jpa.allow-writes"], havingValue = "true")
    fun fullEventStream(
        domainEventRepository: DomainEventEntryRepository,
        snapshotEventRepository: SnapshotEventEntryRepository
    ): EventStream {
        return JPAEventStream(domainEventRepository, snapshotEventRepository)
    }
}
