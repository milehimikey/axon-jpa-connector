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

package wtf.milehimikey.jpa.connector.dialect

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
