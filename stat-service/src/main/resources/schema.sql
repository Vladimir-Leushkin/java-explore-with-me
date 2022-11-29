DROP TABLE IF EXISTS HITS CASCADE;

CREATE TABLE IF NOT EXISTS HITS (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    ip VARCHAR(255) NOT NULL,
    created_on TIMESTAMP,
    CONSTRAINT pk_hits PRIMARY KEY (id)
    );