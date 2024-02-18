create table if not exists datapoint
(
    id             serial primary key,
    group_id       bigint       not null,
    indicator_code varchar(255) not null,
    long_value     bigint,
    numeric_value  numeric(32, 16),
    text_value     text,
    create_time    timestamptz  not null default current_timestamp,
    update_time    timestamptz  not null default current_timestamp
);

create index if not exists idx_datapoint_group_id on datapoint (group_id);
create unique index if not exists idx_datapoint_group_id_indicator_code on datapoint (group_id, indicator_code);

comment on table datapoint is '数据点';
comment on column datapoint.id is 'ID';
comment on column datapoint.group_id is '分组ID';
comment on column datapoint.indicator_code is '指标编码';
comment on column datapoint.long_value is '长整型值';
comment on column datapoint.numeric_value is '数值型值';
comment on column datapoint.text_value is '文本值';
comment on column datapoint.create_time is '创建时间';
comment on column datapoint.update_time is '更新时间';

create table if not exists indicator
(
    id          serial primary key,
    code        varchar(255) not null,
    name        varchar(255) not null,
    description text,
    create_time timestamptz  not null default current_timestamp,
    update_time timestamptz  not null default current_timestamp
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_indicator_code ON indicator (code);


