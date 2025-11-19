-- Axon Framework H2 Schema
-- Version 1.0.0 - Initial schema creation

-- Domain Event Entry table - stores the actual events
CREATE TABLE IF NOT EXISTS domain_event_entry (
    global_index         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    aggregate_identifier VARCHAR(255) NOT NULL,
    sequence_number      BIGINT       NOT NULL,
    type                 VARCHAR(255),
    event_identifier     VARCHAR(255) NOT NULL,
    meta_data            BLOB,
    payload              BLOB         NOT NULL,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255) NOT NULL,
    time_stamp           VARCHAR(255) NOT NULL
);

-- Create unique constraints
ALTER TABLE domain_event_entry ADD CONSTRAINT uk_domain_event_aggregate_seq UNIQUE (aggregate_identifier, sequence_number);
ALTER TABLE domain_event_entry ADD CONSTRAINT uk_domain_event_identifier UNIQUE (event_identifier);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_domain_event_aggregate ON domain_event_entry (aggregate_identifier);
CREATE INDEX IF NOT EXISTS idx_domain_event_timestamp ON domain_event_entry (time_stamp);
CREATE INDEX IF NOT EXISTS idx_domain_event_payload_type ON domain_event_entry (payload_type);

-- Snapshot Event Entry table - stores aggregate snapshots
CREATE TABLE IF NOT EXISTS snapshot_event_entry (
    aggregate_identifier VARCHAR(255) NOT NULL,
    sequence_number      BIGINT       NOT NULL,
    type                 VARCHAR(255) NOT NULL,
    event_identifier     VARCHAR(255) NOT NULL,
    meta_data            BLOB,
    payload              BLOB         NOT NULL,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255) NOT NULL,
    time_stamp           VARCHAR(255) NOT NULL,
    PRIMARY KEY (aggregate_identifier, sequence_number)
);

-- Create unique constraint for snapshot events
ALTER TABLE snapshot_event_entry ADD CONSTRAINT uk_snapshot_event_identifier UNIQUE (event_identifier);

-- Create indexes for snapshot events
CREATE INDEX IF NOT EXISTS idx_snapshot_event_aggregate ON snapshot_event_entry (aggregate_identifier);
CREATE INDEX IF NOT EXISTS idx_snapshot_event_timestamp ON snapshot_event_entry (time_stamp);

-- Saga Entry table - stores saga instances
CREATE TABLE IF NOT EXISTS saga_entry (
    saga_id         VARCHAR(255) NOT NULL,
    revision        VARCHAR(255),
    saga_type       VARCHAR(255),
    serialized_saga BLOB,
    PRIMARY KEY (saga_id)
);

-- Create indexes for saga entries
CREATE INDEX IF NOT EXISTS idx_saga_entry_type ON saga_entry (saga_type);

-- Association Value Entry table - stores associations between sagas and domain objects
CREATE TABLE IF NOT EXISTS association_value_entry (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    association_key   VARCHAR(255) NOT NULL,
    association_value VARCHAR(255),
    saga_id           VARCHAR(255) NOT NULL,
    saga_type         VARCHAR(255)
);

-- Create unique constraint for associations
ALTER TABLE association_value_entry ADD CONSTRAINT uk_association_saga_key_value UNIQUE (saga_id, association_key, association_value);

-- Create indexes for association value entries
CREATE INDEX IF NOT EXISTS idx_association_saga_id ON association_value_entry (saga_id);
CREATE INDEX IF NOT EXISTS idx_association_key_value ON association_value_entry (association_key, association_value);

-- Token Entry table - stores tracking tokens for event processors
CREATE TABLE IF NOT EXISTS token_entry (
    processor_name VARCHAR(255) NOT NULL,
    segment        INTEGER      NOT NULL,
    token          BLOB,
    token_type     VARCHAR(255),
    timestamp      VARCHAR(255),
    owner          VARCHAR(255),
    PRIMARY KEY (processor_name, segment)
);

-- Create indexes for token entries
CREATE INDEX IF NOT EXISTS idx_token_entry_processor ON token_entry (processor_name);
CREATE INDEX IF NOT EXISTS idx_token_entry_owner ON token_entry (owner);

-- Dead Letter Entry table - stores events that could not be processed
CREATE TABLE IF NOT EXISTS dead_letter_entry (
    dead_letter_id      VARCHAR(255) NOT NULL PRIMARY KEY,
    sequence_identifier VARCHAR(255) NOT NULL,
    sequence_index      BIGINT       NOT NULL,
    message_type        VARCHAR(255) NOT NULL,
    event_identifier    VARCHAR(255) NOT NULL,
    cause_message       VARCHAR(255),
    cause_type          VARCHAR(255),
    diagnostics         BLOB,
    enqueued_at         TIMESTAMP    NOT NULL,
    last_touched        TIMESTAMP    NOT NULL,
    processing_started  TIMESTAMP,
    processing_group    VARCHAR(255) NOT NULL
);

-- Create unique constraint for dead letter entries
ALTER TABLE dead_letter_entry ADD CONSTRAINT uk_dead_letter_sequence UNIQUE (sequence_identifier, sequence_index);

-- Create indexes for dead letter entries
CREATE INDEX IF NOT EXISTS idx_dead_letter_processing_group ON dead_letter_entry (processing_group);
CREATE INDEX IF NOT EXISTS idx_dead_letter_message_type ON dead_letter_entry (message_type);
CREATE INDEX IF NOT EXISTS idx_dead_letter_enqueued_at ON dead_letter_entry (enqueued_at);
CREATE INDEX IF NOT EXISTS idx_dead_letter_last_touched ON dead_letter_entry (last_touched);
