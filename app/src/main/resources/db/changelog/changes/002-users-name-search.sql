--liquibase formatted sql

--changeset lysmux:users-name-search-index
create index users_name_search_idx on users using gin (to_tsvector('simple', name));
--rollback drop index users_name_search_idx;
