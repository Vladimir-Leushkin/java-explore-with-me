DROP TABLE IF EXISTS EVENT_COMPILATIONS CASCADE;
DROP TABLE IF EXISTS COMPILATIONS CASCADE;
DROP TABLE IF EXISTS REQUESTS CASCADE;
DROP TABLE IF EXISTS EVENTS CASCADE;
DROP TABLE IF EXISTS CATEGORIES CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS COMMENTS CASCADE;

CREATE TABLE IF NOT EXISTS USERS (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id),
    CONSTRAINT UQ_USER_MAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS CATEGORIES (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT PK_CATEGORIES PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORIES_NAME UNIQUE (name)
    );

CREATE TABLE IF NOT EXISTS EVENTS (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation  VARCHAR(2000) NOT NULL,
    category_id INTEGER NOT NULL REFERENCES CATEGORIES (id) ON DELETE CASCADE,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator BIGINT NOT NULL REFERENCES USERS (id) ON DELETE CASCADE,
    location_lat REAL,
    location_lon REAL,
    paid    BOOLEAN,
    participant_limit INTEGER,
    published_on TIMESTAMP,
    request_moderation BOOLEAN,
    state VARCHAR(120) NOT NULL,
    title VARCHAR(120) NOT NULL,
    CONSTRAINT PK_EVENTS PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS REQUESTS (
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_on TIMESTAMP NOT NULL,
    event_id    BIGINT NOT NULL REFERENCES EVENTS (id) ON DELETE CASCADE,
    requester_id    BIGINT NOT NULL REFERENCES USERS (id) ON DELETE CASCADE,
    status    varchar(25) NOT NULL,
    CONSTRAINT PQ_REQUEST PRIMARY KEY (id),
    CONSTRAINT FK_REQUEST_BY_EVENTS FOREIGN KEY (event_id) REFERENCES EVENTS (id),
    CONSTRAINT FK_REQUEST_BY_USERS FOREIGN KEY (requester_id) REFERENCES USERS (id),
    CONSTRAINT UQ_PARTICIPANT_PER_EVENT UNIQUE (requester_id, event_id)
    );

CREATE TABLE IF NOT EXISTS COMPILATIONS (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(120) NOT NULL,
    pinned BOOLEAN NOT NULL,
    CONSTRAINT PK_COMPILATIONS PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS EVENT_COMPILATIONS (
    compilation_id BIGINT REFERENCES COMPILATIONS (id) ON DELETE CASCADE,
    event_id BIGINT REFERENCES EVENTS (id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
    );

CREATE TABLE IF NOT EXISTS COMMENTS (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(5000) NOT NULL,
    event_id BIGINT NOT NULL REFERENCES EVENTS (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES USERS (id) ON DELETE CASCADE,
    created_date TIMESTAMP NOT NULL,
    edit_date TIMESTAMP,
    CONSTRAINT PK_COMMENT PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_BY_EVENTS FOREIGN KEY (event_id) REFERENCES EVENTS (id),
    CONSTRAINT FK_COMMENT_BY_USERS FOREIGN KEY (user_id) REFERENCES USERS (id)
    );
