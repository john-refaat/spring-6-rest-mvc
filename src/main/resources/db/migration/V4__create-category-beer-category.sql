drop table if exists beer_category;
drop table if exists category;

create table category(
	id varchar(100) not null primary key,
	description varchar(100),
	created_date timestamp,
    last_modified_date timestamp,
	version SMALLINT
) engine=InnoDB;

create table beer_category(
	beer_id varchar(100) not null,
    category_id varchar(100) not null,
    primary key(beer_id, category_id),
	constraint beer_id_fk foreign key (beer_id) references beer(id),
	constraint category_id_fk foreign key (category_id) references category(id)
) engine=InnoDB;
