create table main.saved_locations
(
    id    BINARY(16)   not null
        constraint saved_locations_pk
            primary key,
    world varchar(255) not null,
    x     decimal      not null,
    y     decimal      not null,
    z     decimal      not null,
    yaw   float        not null,
    pitch float        not null
);

create table main.player_settings
(
    id            BINARY(16) not null
        constraint player_settings_pk
            primary key,
    home          binary(16) default null
        constraint player_settings_saved_locations_home_id_fk
            references main.saved_locations
            on delete set null,
    last_location binary(16)
        constraint player_settings_saved_locations_last_location_id_fk
            references main.saved_locations
);

create table main.sqlite_master
(
    type     TEXT,
    name     TEXT,
    tbl_name TEXT,
    rootpage INT,
    sql      TEXT
);

