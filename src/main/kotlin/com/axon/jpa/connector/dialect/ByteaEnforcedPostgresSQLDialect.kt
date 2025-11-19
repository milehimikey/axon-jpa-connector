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

package com.axon.jpa.connector.dialect

import org.hibernate.boot.model.TypeContributions
import org.hibernate.dialect.DatabaseVersion
import org.hibernate.dialect.PostgreSQLDialect
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.SqlTypes
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType
import java.sql.Types

/**
 * Custom PostgreSQL dialect that enforces the use of BYTEA for BLOB columns.
 * This dialect ensures that ByteArray fields in JPA entities are properly mapped
 * to PostgreSQL's BYTEA type instead of the default OID type.
 * 
 * This is particularly important for Axon Framework entities that store
 * serialized event payloads and metadata as byte arrays.
 */
class ByteaEnforcedPostgresSQLDialect : PostgreSQLDialect(DatabaseVersion.make(9, 5)) {
    
    /**
     * Override column type mapping to use BYTEA for BLOB columns.
     */
    override fun columnType(sqlTypeCode: Int): String? {
        return if (sqlTypeCode == SqlTypes.BLOB) "bytea" else super.columnType(sqlTypeCode)
    }

    /**
     * Override cast type mapping to use BYTEA for BLOB columns.
     */
    override fun castType(sqlTypeCode: Int): String? {
        return if (sqlTypeCode == SqlTypes.BLOB) "bytea" else super.castType(sqlTypeCode)
    }

    /**
     * Contribute custom type mappings to ensure proper BLOB handling.
     */
    public override fun contributeTypes(
        typeContributions: TypeContributions,
        serviceRegistry: ServiceRegistry,
    ) {
        super.contributeTypes(typeContributions, serviceRegistry)
        val jdbcTypeRegistry = typeContributions.getTypeConfiguration()
            .jdbcTypeRegistry
        jdbcTypeRegistry.addDescriptor(Types.BLOB, BinaryJdbcType.INSTANCE)
    }
}
