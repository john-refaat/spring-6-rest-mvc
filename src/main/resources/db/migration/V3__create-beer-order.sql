drop table if exists beer_order_line;
drop table if exists beer_order;

create table beer_order(
		id varchar(100) not null,
		created_date datetime(6),
        last_modified_date datetime(6),
		version SMALLINT,
		customer_id varchar(100),
        primary key (id),
        foreign key (customer_id) references customer(id)
) ENGINE=INNODB;

create table beer_order_line(
	id varchar(100) not null,
    beer_id varchar(100) not null,
    created_date datetime(6),
	last_modified_date datetime(6),
    order_quantity INT,
    quantity_allocated INT,
    version SMALLINT,
    beer_order_id varchar(100),
    primary key (id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (beer_order_id) REFERENCES beer_order(id)
) ENGINE=INNODB;
