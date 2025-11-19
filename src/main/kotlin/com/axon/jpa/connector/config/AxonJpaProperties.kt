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

package com.axon.jpa.connector.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for Axon JPA Connector.
 * These properties allow customization of the library behavior.
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
