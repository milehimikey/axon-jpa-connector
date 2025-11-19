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

package wtf.milehimikey.jpa.connector.autoconfigure

import wtf.milehimikey.jpa.connector.config.AxonJpaProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import jakarta.persistence.EntityManager

/**
 * Auto-configuration for Axon JPA Connector.
 *
 * This library is designed for read-only access to existing Axon event stores
 * for troubleshooting and data analysis purposes.
 *
 * This configuration automatically:
 * - Enables entity scanning for Axon JPA entities
 * - Enables JPA repositories for Axon repositories (read-only by default)
 * - Sets up configuration properties
 * - Configures read-only mode by default to prevent accidental data modification
 */
@AutoConfiguration
@ConditionalOnClass(EntityManager::class)
@ConditionalOnProperty(
    prefix = "axon.jpa",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(AxonJpaProperties::class)
@EntityScan(basePackages = ["wtf.milehimikey.jpa.connector.entities"])
@EnableJpaRepositories(basePackages = ["wtf.milehimikey.jpa.connector.repositories"])
class AxonJpaAutoConfiguration(
    private val properties: AxonJpaProperties
) {

    private val logger = LoggerFactory.getLogger(AxonJpaAutoConfiguration::class.java)

    init {
        logger.info("Axon JPA Connector auto-configuration enabled")
        logger.info("Read-only mode: {} (allowWrites={})", !properties.allowWrites, properties.allowWrites)
        if (!properties.allowWrites) {
            logger.info("Library configured for read-only access to existing event stores")
        } else {
            logger.warn("Write operations are enabled - use with caution!")
        }
        logger.debug("Axon JPA properties: {}", properties)

        // Log enabled entities
        val enabledEntities = mutableListOf<String>()
        with(properties.entities) {
            if (domainEvents) enabledEntities.add("DomainEvents")
            if (snapshots) enabledEntities.add("Snapshots")
            if (tokens) enabledEntities.add("Tokens")
            if (sagas) enabledEntities.add("Sagas")
            if (deadLetters) enabledEntities.add("DeadLetters")
            if (associations) enabledEntities.add("Associations")
        }

        if (enabledEntities.isNotEmpty()) {
            logger.info("Enabled Axon JPA entities: {}", enabledEntities.joinToString(", "))
        } else {
            logger.warn("No Axon JPA entities are enabled")
        }

        // Log database configuration
        logger.debug("Database type: {}", properties.database.type)
        logger.debug("Custom dialect enabled: {}", properties.enableCustomDialect)

        if (!properties.schema.isNullOrBlank()) {
            logger.debug("Using schema: {}", properties.schema)
        }

        if (properties.tablePrefix.isNotBlank()) {
            logger.debug("Using table prefix: {}", properties.tablePrefix)
        }
    }
}
