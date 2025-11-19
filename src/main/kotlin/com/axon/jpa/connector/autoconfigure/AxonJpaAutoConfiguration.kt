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

package com.axon.jpa.connector.autoconfigure

import com.axon.jpa.connector.config.AxonJpaProperties
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
 * This configuration automatically:
 * - Enables entity scanning for Axon JPA entities
 * - Enables JPA repositories for Axon repositories
 * - Sets up configuration properties
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
@EntityScan(basePackages = ["com.axon.jpa.connector.entities"])
@EnableJpaRepositories(basePackages = ["com.axon.jpa.connector.repositories"])
class AxonJpaAutoConfiguration(
    private val properties: AxonJpaProperties
) {
    
    private val logger = LoggerFactory.getLogger(AxonJpaAutoConfiguration::class.java)
    
    init {
        logger.info("Axon JPA Connector auto-configuration enabled")
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
