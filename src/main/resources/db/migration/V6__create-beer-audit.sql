drop table if exists beer_audit;

create table beer_audit (
    beer_style tinyint check (beer_style between 0 and 9),
    price numeric(38,2),
    quantity_on_hand INT,
    version SMALLINT,
    audit_created_date timestamp(6),
    created_date timestamp(6),
    update_date timestamp(6),
    beer_name varchar(50),
    audit_id varchar(100) not null,
    id varchar(100) not null,
    audit_event_type varchar(255),
    principal_name varchar(255),
    upc varchar(255),
    primary key (audit_id)
);
