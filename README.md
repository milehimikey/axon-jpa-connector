# Axon JPA Connector

A Spring Boot library providing JPA entities and repositories for Axon Framework applications. This library offers ready-to-use JPA mappings for all Axon Framework storage needs, including domain events, snapshots, sagas, tokens, and dead letter queues.

## Features

- ✅ **Complete JPA Entities** - All Axon Framework storage entities included
- ✅ **Ready-to-use Repositories** - Spring Data JPA repositories with custom queries
- ✅ **Auto-Configuration** - Works out-of-the-box with Spring Boot
- ✅ **Multi-Database Support** - PostgreSQL, MySQL, H2 with optimized schemas
- ✅ **Custom Hibernate Dialects** - Optimized for each database type
- ✅ **Query Utilities** - Specifications and builders for complex queries
- ✅ **Production Ready** - Proper indexing and performance optimizations

## Quick Start

### 1. Add Dependency

```kotlin
dependencies {
    implementation("com.axon:axon-jpa-connector:1.0.0")
    
    // Add your database driver
    runtimeOnly("org.postgresql:postgresql") // or mysql, h2, etc.
}
```

### 2. Configure Database

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypass
  jpa:
    hibernate:
      ddl-auto: validate # or create-drop for development
```

### 3. Use Repositories

```kotlin
@Service
class EventService(
    private val domainEventRepository: DomainEventEntryRepository,
    private val tokenRepository: TokenEntryRepository
) {
    
    fun getEventsForAggregate(aggregateId: String): List<DomainEventEntry> {
        return domainEventRepository.findByAggregateIdentifier(aggregateId)
    }
    
    fun getProcessorTokens(processorName: String): List<TokenEntry> {
        return tokenRepository.findByProcessorName(processorName)
    }
}
```

## Entities

The library provides the following JPA entities:

| Entity | Description | Table Name |
|--------|-------------|------------|
| `DomainEventEntry` | Domain events with metadata and payload | `domain_event_entry` |
| `SnapshotEventEntry` | Aggregate snapshots | `snapshot_event_entry` |
| `TokenEntry` | Event processor tracking tokens | `token_entry` |
| `SagaEntry` | Saga instances | `saga_entry` |
| `AssociationValueEntry` | Saga associations | `association_value_entry` |
| `DeadLetterEntry` | Failed message entries | `dead_letter_entry` |

## Repositories

Each entity has a corresponding repository with custom query methods:

```kotlin
// Domain Events
interface DomainEventEntryRepository : JpaRepository<DomainEventEntry, Long> {
    fun findByAggregateIdentifier(aggregateIdentifier: String): List<DomainEventEntry>
    fun findByPayloadType(payloadType: String, pageable: Pageable): Page<DomainEventEntry>
    fun countDistinctAggregateIdentifiers(): Long
    // ... more methods
}

// Tokens
interface TokenEntryRepository : JpaRepository<TokenEntry, TokenEntryId> {
    fun findByProcessorName(processorName: String): List<TokenEntry>
    fun findByOwner(owner: String): List<TokenEntry>
    fun countDistinctProcessorNames(): Long
    // ... more methods
}
```

## Configuration

Customize the library behavior with configuration properties:

```yaml
axon:
  jpa:
    # Database schema (optional)
    schema: axon_framework
    
    # Table prefix (optional)
    table-prefix: "axon_"
    
    # Enable custom PostgreSQL dialect
    enable-custom-dialect: true
    
    # Entity configuration
    entities:
      domain-events: true
      snapshots: true
      tokens: true
      sagas: true
      dead-letters: true
      associations: true
    
    # Database settings
    database:
      type: postgresql # auto, postgresql, mysql, h2
      create-schema: false
      validate-schema: true
```

## Advanced Usage

### Query Builder

Use the fluent query builder for complex queries:

```kotlin
val specification = DomainEventEntryQueryBuilder.builder()
    .withAggregateIdentifier("order-123")
    .withPayloadType("OrderCreated")
    .withTimestampRange("2024-01-01T00:00:00Z", "2024-12-31T23:59:59Z")
    .build()

val events = domainEventRepository.findAll(specification)
```

### Custom Specifications

Create reusable query specifications:

```kotlin
val recentEvents = DomainEventEntrySpecification
    .hasTimestampAfter("2024-01-01T00:00:00Z")
    .and(DomainEventEntrySpecification.hasPayloadType("OrderCreated"))

val events = domainEventRepository.findAll(recentEvents)
```

## Database Support

### PostgreSQL (Recommended)
- Uses BYTEA for binary data
- Custom dialect for optimal performance
- Full-text search capabilities

### MySQL
- Uses LONGBLOB for binary data
- UTF8MB4 character set
- InnoDB storage engine

### H2 (Development/Testing)
- In-memory or file-based
- Perfect for testing
- Compatible with production schemas

## Migration

The library includes database migration scripts for all supported databases:

- `db/migration/postgresql/V1__Create_Axon_Tables.sql`
- `db/migration/mysql/V1__Create_Axon_Tables.sql`
- `db/migration/h2/V1__Create_Axon_Tables.sql`

Use with Flyway or Liquibase for automated schema management.

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please read our contributing guidelines and submit pull requests.

## Examples

### Basic Usage Example

```kotlin
@SpringBootApplication
class MyApplication

@RestController
class EventController(
    private val domainEventRepository: DomainEventEntryRepository
) {

    @GetMapping("/events/{aggregateId}")
    fun getEvents(@PathVariable aggregateId: String): List<DomainEventEntry> {
        return domainEventRepository.findByAggregateIdentifier(aggregateId)
    }

    @GetMapping("/events/stats")
    fun getStats(): Map<String, Any> {
        return mapOf(
            "totalEvents" to domainEventRepository.count(),
            "totalAggregates" to domainEventRepository.countDistinctAggregateIdentifiers(),
            "totalPayloadTypes" to domainEventRepository.countDistinctPayloadTypes()
        )
    }
}
```

### Testing Example

```kotlin
@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private lateinit var domainEventRepository: DomainEventEntryRepository

    @Test
    fun `should save and retrieve events`() {
        val event = DomainEventEntry(
            aggregateIdentifier = "test-123",
            sequenceNumber = 1L,
            eventIdentifier = "event-456",
            payload = "test".toByteArray(),
            payloadType = "TestEvent",
            timeStamp = Instant.now().toString()
        )

        val saved = domainEventRepository.save(event)
        val found = domainEventRepository.findByAggregateIdentifier("test-123")

        assertThat(found).hasSize(1)
        assertThat(found[0].eventIdentifier).isEqualTo("event-456")
    }
}
```

## Support

- 📖 [Documentation](https://github.com/milehimikey/axon-jpa-connector/wiki)
- 🐛 [Issue Tracker](https://github.com/milehimikey/axon-jpa-connector/issues)
- 💬 [Discussions](https://github.com/milehimikey/axon-jpa-connector/discussions)
