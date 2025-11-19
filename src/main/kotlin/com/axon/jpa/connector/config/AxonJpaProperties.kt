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

package com.axon.jpa.connector.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for Axon JPA Connector.
 * This library is designed for read-only access to existing Axon event stores
 * for troubleshooting and data analysis purposes.
 */
@ConfigurationProperties(prefix = "axon.jpa")
data class AxonJpaProperties(
    /**
     * Database schema name for Axon tables.
     * Default: null (uses default schema)
     */
    val schema: String? = null,
    
    /**
     * Table name prefix for all Axon tables.
     * Default: empty string (no prefix)
     */
    val tablePrefix: String = "",
    
    /**
     * Whether to enable custom PostgreSQL dialect for BYTEA handling.
     * Default: true
     */
    val enableCustomDialect: Boolean = true,

    /**
     * Whether to allow write operations to the database.
     * Default: false (read-only mode for data analysis and troubleshooting)
     * Set to true only for testing or specific use cases where writes are needed.
     */
    val allowWrites: Boolean = false,
    
    /**
     * Entity configuration settings.
     */
    val entities: EntityConfig = EntityConfig(),
    
    /**
     * Database-specific settings.
     */
    val database: DatabaseConfig = DatabaseConfig()
) {
    
    /**
     * Configuration for individual entity types.
     */
    data class EntityConfig(
        /**
         * Whether to enable domain event entities.
         * Default: true
         */
        val domainEvents: Boolean = true,
        
        /**
         * Whether to enable snapshot event entities.
         * Default: true
         */
        val snapshots: Boolean = true,
        
        /**
         * Whether to enable token entry entities.
         * Default: true
         */
        val tokens: Boolean = true,
        
        /**
         * Whether to enable saga entry entities.
         * Default: true
         */
        val sagas: Boolean = true,
        
        /**
         * Whether to enable dead letter entry entities.
         * Default: true
         */
        val deadLetters: Boolean = true,
        
        /**
         * Whether to enable association value entry entities.
         * Default: true
         */
        val associations: Boolean = true
    )
    
    /**
     * Database-specific configuration.
     */
    data class DatabaseConfig(
        /**
         * Database type for dialect selection.
         * Supported values: postgresql, mysql, h2, auto
         * Default: auto (auto-detect based on driver)
         */
        val type: String = "auto",
        
        /**
         * Whether to automatically create database schema.
         * Default: false
         */
        val createSchema: Boolean = false,
        
        /**
         * Whether to validate database schema on startup.
         * Default: true
         */
        val validateSchema: Boolean = true
    )
}
