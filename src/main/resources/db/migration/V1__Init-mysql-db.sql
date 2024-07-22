
    drop table if exists beer;

    drop table if exists customer;

    create table beer (
        beer_style tinyint not null check (beer_style between 0 and 9),
        price decimal(38,2) not null,
        quantity_on_hand integer,
        version SMALLINT,
        created_date datetime(6),
        update_date datetime(6),
        beer_name varchar(50) not null,
        id varchar(100) not null,
        upc varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table customer (
        version SMALLINT,
        created_date datetime(6),
        last_modified_date datetime(6),
        id varchar(100) not null,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;
