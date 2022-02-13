create table users
(
    user_status varchar                                         not null,
    bot_state   varchar                                         not null,
    language    varchar                                         not null,
    chat_id     integer                                         not null,
    id          integer default nextval('global_seq'::regclass) not null,
    constraint users_pkey
        primary key (id),
    constraint users_chat_id_key
        unique (chat_id)
);

alter table users
    owner to qwsoytblvgzaat;

create table categories
(
    id_parent integer,
    name      varchar                                         not null,
    id        integer default nextval('global_seq'::regclass) not null,
    constraint categories_pkey
        primary key (id),
    constraint categories_categories__fk
        foreign key (id_parent) references categories
            on update restrict on delete restrict
);

alter table categories
    owner to qwsoytblvgzaat;

create table posts
(
    id_category integer                                         not null,
    file        text,
    descr       text                                            not null,
    id          integer default nextval('global_seq'::regclass) not null,
    constraint posts_pkey
        primary key (id),
    constraint posts_categories_id_fk
        foreign key (id_category) references categories
            on update restrict on delete restrict
);

alter table posts
    owner to qwsoytblvgzaat;

