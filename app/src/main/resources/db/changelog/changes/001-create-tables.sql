--liquibase formatted sql

--changeset lysmux:create-users
create table users
(
    user_id uuid primary key,
    name    varchar(120) not null
);
--rollback drop table users;

--changeset lysmux:create-orders
create table orders
(
    order_id        uuid primary key,
    user_id         uuid         not null references users (user_id),
    restaurant_name varchar(200) not null,
    total_amount    numeric      not null check (total_amount >= 0),
    status          varchar(16)  not null check (status in ('CREATED', 'COOKING', 'IN_DELIVERY', 'DELIVERED')),
    created_at      timestamptz  not null
);
create index orders_created_at_idx on orders (created_at desc);
--rollback drop table orders;

--changeset lysmux:create-notifications
create table notifications
(
    notification_id uuid primary key,
    user_id         uuid        not null references users (user_id),
    order_id        uuid        not null references orders (order_id),
    text            text        not null,
    created_at      timestamptz not null
);
create index notifications_created_at_idx on notifications (created_at desc);
create index notifications_user_id_created_at_idx on notifications (user_id, created_at desc);
--rollback drop table notifications;
