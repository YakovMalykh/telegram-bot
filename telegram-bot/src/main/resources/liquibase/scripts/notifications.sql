-- liquibase formatted sql

-- changeset ymalykh:1
CREATE TABLE notifications
(
    id                SERIAL PRIMARY KEY,
    chat_id           BIGINT    NOT NULL,
    notification_text TEXT      NOT NULL,
    date_time         TIMESTAMP NOT NULL,
    name              TEXT
);



