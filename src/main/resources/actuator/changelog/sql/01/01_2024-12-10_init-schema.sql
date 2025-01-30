create table if not exists main_info
(
    id       bigserial primary key,
    application_name varchar(255) not null,
    artifact_id varchar(255) not null,
    launch_time varchar(255) not null,
    app_version varchar(255) not null,
    context_path varchar(255) not null,
    created_at timestamp
);

create table if not exists active_profiles
(
    main_info_id bigserial,
    active_profile varchar(255),
    primary key (main_info_id, active_profile),
    foreign key (main_info_id) references main_info (id) on delete cascade
);

create table if not exists metrics
(
    id       bigserial primary key,
    uptime varchar(255) not null,
    cpu_usage varchar(255) not null,
    memory_usage varchar(255) not null,
    build_version varchar(255),
    current_branch varchar(255) not null,
    last_commit varchar(255) not null,
    created_at timestamp
);
