drop table if exists beer_order_shipment;

create table beer_order_shipment(
	id varchar(100) not null primary key,
	beer_order_id varchar(100) not null,
    tracking_number varchar(50),
    created_date timestamp,
	last_modified_date timestamp,
	version BIGINT,
    constraint bos_beer_order_fk foreign key (beer_order_id) references beer_order(id)
) engine=InnoDB;

alter table beer_order add column beer_order_shipment_id varchar(100);
alter table beer_order add constraint bo_beer_order_shipment_fk foreign key (beer_order_shipment_id) references beer_order_shipment(id);