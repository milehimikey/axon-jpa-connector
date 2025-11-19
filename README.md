# Axon JPA Connector

A Spring Boot library for **read-only access** to existing Axon Framework event stores. Designed primarily for **data analysis, troubleshooting, and reporting** on production event stores without the risk of accidental data modification.

## 🔒 Read-Only by Design

This library is **read-only by default** to ensure safe access to production event stores:

- ✅ **No accidental writes** - Read-only repositories prevent data modification
- ✅ **No schema creation** - Safe to connect to existing production databases
- ✅ **Data analysis focus** - Rich querying and statistics for troubleshooting
- ✅ **Event store inspection** - Understand event patterns and aggregate behavior

> **Note**: Write operations can be enabled for testing environments by setting `axon.jpa.allow-writes=true`

## Features

- 🔒 **Read-Only by Default** - Safe access to production event stores
- 📊 **Event Analysis** - Rich querying and statistics for troubleshooting
- 🔍 **Aggregate Inspection** - Load events without deserialization
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

### 2. Configure Database (Read-Only)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/existing_event_store
    username: readonly_user  # Use read-only database user
    password: readonly_pass
  jpa:
    hibernate:
      ddl-auto: none  # NEVER modify existing schema
    show-sql: false

# Axon JPA Connector - Read-only configuration
axon:
  jpa:
    allow-writes: false  # Default: read-only mode
    schema: axon_events  # If events are in specific schema
```

### 3. Analyze Event Data

```kotlin
@Service
class EventAnalysisService(
    private val eventStream: EventStream  // ReadOnlyEventStream by default
) {

    fun analyzeAggregate(aggregateId: String): AggregateAnalysis {
        // Load all events for analysis
        val events = eventStream.load(aggregateId, 0)
        val snapshot = eventStream.loadLatestSnapshot(aggregateId)

        return AggregateAnalysis(
            eventCount = events.size,
            latestSequence = events.maxOfOrNull { it.sequenceNumber } ?: 0,
            hasSnapshot = snapshot != null,
            eventTypes = events.map { it.payloadType }.distinct()
        )
    }

    fun troubleshootEventFlow(aggregateId: String, fromSequence: Long): List<DomainEventEntry> {
        return eventStream.load(aggregateId, fromSequence)
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

## Read-Only Mode

### Default Behavior

By default, the library operates in **read-only mode** to ensure safe access to production event stores:

```yaml
axon:
  jpa:
    allow-writes: false  # Default: read-only mode
```

### Read-Only Repositories

When in read-only mode, the library provides specialized repositories that expose only query methods:

- `ReadOnlyDomainEventEntryRepository` - Query-only access to events
- `ReadOnlySnapshotEventEntryRepository` - Query-only access to snapshots
- No save, delete, or modification methods available

### Enabling Writes (Testing Only)

For testing environments, you can enable write operations:

```yaml
# application-test.yml
axon:
  jpa:
    allow-writes: true
    database:
      create-schema: true  # Only for tests
```

### Safety Features

- **Schema Protection**: No automatic table creation in production
- **Write Prevention**: Read-only repositories prevent accidental modifications
- **Clear Logging**: Startup logs indicate read-only vs write mode
- **Production Focus**: Designed for connecting to existing event stores

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

## Event Stream - Loading Aggregates

The library provides an `EventStream` interface for loading events without deserializing payloads, similar to Axon Framework's aggregate loading capabilities:

```kotlin
@Service
class MyService(private val eventStream: EventStream) {

    // Load all events for an aggregate
    fun loadAggregateHistory(aggregateId: String): List<DomainEventEntry> {
        return eventStream.load(aggregateId)
    }

    // Load events from a specific sequence number
    fun loadRecentEvents(aggregateId: String, fromSequence: Long): List<DomainEventEntry> {
        return eventStream.load(aggregateId, fromSequence)
    }

    // Load events in a range
    fun loadEventRange(aggregateId: String, from: Long, to: Long): List<DomainEventEntry> {
        return eventStream.load(aggregateId, from, to)
    }

    // Load with filtering criteria
    fun loadSpecificEventTypes(aggregateId: String, eventTypes: Set<String>): List<DomainEventEntry> {
        val criteria = EventLoadingCriteria(payloadTypes = eventTypes)
        return eventStream.load(aggregateId, criteria)
    }

    // Load optimized view with snapshot + subsequent events
    fun loadCurrentState(aggregateId: String): AggregateEventStream {
        return eventStream.loadEventsSinceSnapshot(aggregateId)
    }
}
```

### Working with Raw Event Data

The library provides extension functions for working with event payloads without deserialization:

```kotlin
val events = eventStream.load("user-123")

events.forEach { event ->
    println("Event: ${event.payloadType}")

    // Handle JSON payloads
    if (event.isJsonPayload()) {
        println("JSON Payload: ${event.payloadAsString()}")
    } else {
        println("Binary payload of ${event.payload.size} bytes")
    }

    // Access metadata if present
    if (event.hasMetaData()) {
        println("Metadata: ${event.metaDataAsString()}")
    }
}
```

## Configuration

### Production Configuration (Read-Only)

For connecting to existing production event stores:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/production_event_store
    username: readonly_user
    password: readonly_password
  jpa:
    hibernate:
      ddl-auto: none  # NEVER modify production schema
    show-sql: false

axon:
  jpa:
    # Read-only mode (default)
    allow-writes: false

    # Database schema (if events are in specific schema)
    schema: axon_events

    # Table prefix (if your tables have prefixes)
    table-prefix: ""

    # Enable custom PostgreSQL dialect for BYTEA handling
    enable-custom-dialect: true

    # Enable only entities you need for analysis
    entities:
      domain-events: true   # Essential for event analysis
      snapshots: true       # Essential for aggregate analysis
      tokens: false         # Disable if not needed
      sagas: false          # Disable if not needed
      dead-letters: false   # Disable if not needed
      associations: false   # Disable if not needed

    # Database settings
    database:
      type: postgresql      # auto, postgresql, mysql, h2
      create-schema: false  # NEVER create schema in production
      validate-schema: true # Validate expected tables exist
```

### Testing Configuration (Writes Enabled)

For test environments where you need to create test data:

```yaml
# application-test.yml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop

axon:
  jpa:
    allow-writes: true      # Enable writes for testing
    database:
      create-schema: true   # Allow schema creation in tests
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

### Event Store Analysis Tool

```kotlin
@SpringBootApplication
class EventAnalysisApplication

@RestController
class EventAnalysisController(
    private val eventStream: EventStream,
    private val domainEventRepository: ReadOnlyDomainEventEntryRepository
) {

    @GetMapping("/analysis/{aggregateId}")
    fun analyzeAggregate(@PathVariable aggregateId: String): AggregateAnalysis {
        val events = eventStream.load(aggregateId, 0)
        val snapshot = eventStream.loadLatestSnapshot(aggregateId)

        return AggregateAnalysis(
            aggregateId = aggregateId,
            eventCount = events.size,
            latestSequence = events.maxOfOrNull { it.sequenceNumber } ?: 0,
            hasSnapshot = snapshot != null,
            eventTypes = events.map { it.payloadType }.distinct(),
            firstEventTime = events.minOfOrNull { it.timeStamp },
            lastEventTime = events.maxOfOrNull { it.timeStamp }
        )
    }

    @GetMapping("/stats")
    fun getEventStoreStats(): EventStoreStats {
        return EventStoreStats(
            totalEvents = domainEventRepository.count(),
            totalAggregates = domainEventRepository.countDistinctAggregateIdentifiers(),
            payloadTypes = domainEventRepository.findDistinctPayloadTypes(),
            payloadTypeDistribution = domainEventRepository.countByPayloadType()
        )
    }

    @GetMapping("/troubleshoot/{aggregateId}")
    fun troubleshootAggregate(
        @PathVariable aggregateId: String,
        @RequestParam(defaultValue = "0") fromSequence: Long
    ): List<EventSummary> {
        return eventStream.load(aggregateId, fromSequence).map { event ->
            EventSummary(
                sequenceNumber = event.sequenceNumber,
                eventType = event.payloadType,
                timestamp = event.timeStamp,
                hasMetadata = event.hasMetaData(),
                payloadSize = event.payload.size
            )
        }
    }
}
```

### Testing Examples

#### Read-Only Analysis Test

```kotlin
@DataJpaTest
@TestPropertySource(properties = ["axon.jpa.allow-writes=false"])
class ReadOnlyEventAnalysisTest {

    @Autowired
    private lateinit var eventStream: EventStream  // Will be ReadOnlyEventStream

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `should analyze existing events`() {
        // Assume events already exist in test database
        val events = eventStream.load("existing-aggregate-123", 0)

        assertThat(events).isNotEmpty()
        assertThat(events.first().payloadType).isEqualTo("SomeEvent")
    }
}
```

#### Write-Enabled Test (For Testing Library Features)

```kotlin
@DataJpaTest
@TestPropertySource(properties = [
    "axon.jpa.allow-writes=true",
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
class EventStreamTest {

    @Autowired
    private lateinit var eventStream: EventStream  // Will be JPAEventStream

    @Autowired
    private lateinit var domainEventRepository: DomainEventEntryRepository

    @Test
    fun `should save and load events`() {
        val event = DomainEventEntry(
            aggregateIdentifier = "test-123",
            sequenceNumber = 1L,
            type = "TestEvent",
            eventIdentifier = "event-456",
            payload = "test".toByteArray(),
            payloadType = "TestEvent",
            timeStamp = Instant.now().toString()
        )

        domainEventRepository.save(event)
        val loaded = eventStream.load("test-123", 0)

        assertThat(loaded).hasSize(1)
        assertThat(loaded[0].eventIdentifier).isEqualTo("event-456")
    }
}
```

## Support

- 📖 [Documentation](https://github.com/milehimikey/axon-jpa-connector/wiki)
- 🐛 [Issue Tracker](https://github.com/milehimikey/axon-jpa-connector/issues)
- 💬 [Discussions](https://github.com/milehimikey/axon-jpa-connector/discussions)
