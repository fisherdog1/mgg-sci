select * from PersonEmail;
select * from Address;
select * from Sale;
select * from Store;
select * from Person;
select * from Service;
select * from Subscription;
select * from Item;

select count(i.productId) as count, i.startDate, i.endDate from Subscription i join Sale s on i.saleId = s.saleId where
	i.legacyId = 'foof00' and
    i.saleId is not null;

update Subscription s set 
	startDate = '2015-01-20',
    endDate = '2016-01-20' where
    s.saleId = 10 and
	s.legacyId = 'foof00';

select s.productName, s.baseRate from Subscription s where
	s.legacyId = 'foof00';

select count(i.productId) as count, i.quantity from Item i join Sale s on i.saleId = s.saleId where
	i.legacyId = 'foof70' and
    i.quantity > 0;

select count(i.productId) as count, i.basePrice from Item i join Sale s on i.saleId = s.saleId where
	i.legacyId = 'foof20' and
    i.saleId is not null;

select count(i.productId) as count, i.hours from Service i join Sale s on i.saleId = s.saleId where
	i.legacyId = 'foof10' and
    i.saleId is not null;

update Item i set 
	quantity = 3 where
    i.saleId = 1 and
	i.legacyId = 'foof70';
    
update Item i set 
	basePrice = 4000 where
    i.saleId = 2 and
	i.legacyId = 'foof20';
    
update Service s set 
	hours = 3.0 where
    s.saleId = 8 and
	s.legacyId = 'foof10';
    
select * from Item i where i.saleId is not null;

insert into Subscription (productName, legacyId, saleId, baseRate, startDate, endDate) values (
	'TestSub',
    'f7f7f7',
    9,
    100000,
    '2015-01-20',
    '2017-01-20');

insert into Item (productName, legacyId, newUsed, basePrice, saleId, quantity) values (
    'iPod Nano',
    'f00f70',
    'new',
    10000,
    1,
    1);

insert into Sale (legacyId, storeId, customerId, salespersonId) values (
	'ffffff',
    3,
    4,
    4);

insert into Subscription (productName, legacyId, baseRate, startDate, endDate) values (
	'Nintendo Power',
    'f1f2f3',
	12000,
    '2015-01-20',
    '2017-01-20');

insert into Service (productName, legacyId, baseRate, hours, salespersonId) values (
	'Repair',
    'f71452',
    2000,
    1.0,
    1);

insert into Address (street, city, state, zip, country) values (
	'1 Havey Avenue',
    'Cleveland',
    'OH',
    '44177', 
    'US');
    
select count(e.emailId) as count from Email e where
	e.address = 'nmichelotti1@sbwire.com';
    
insert into Email (address) values (
	'testemail@gmail.com');
    
insert into Person (legacyId, firstName, lastName, customerType, addressId) values (
	'00ff7f',
    'Bobby',
    'Tables',
    'gold',
    1);
    
select count(p.personId) as count, p.personId from Person p where
	p.legacyId = '00ff7f' and
    p.firstName = 'Bobby' and
    p.lastName = 'Tables' and
    p.customerType = 'gold';
    
insert into PersonEmail (personId, emailId) values (
	1,
    38);
    
select count(pe.personId) from PersonEmail pe where
	pe.personId = 1 and
    pe.emailId = 38;

select i.productName, i.newUsed, i.basePrice from Item i where
	i.legacyId = 'foof70';

select count(a.addressId) as count, a.addressId from Address a where
	a.street = '1337 Havey Avenue' and
    a.city = 'Cleveland' and
    a.state = 'OH' and
    a.zip = '44177' and
    a.country = 'US';
    
insert into Store (legacyId, managerId, addressId) values (
	'f6f6f6',
    1,
    1);
    
insert into Item (productName, legacyId, newUsed, basePrice, quantity) values (
    'iPod Nano',
    'f00f70',
    'new',
    10000,
    0);
    
select count(s.storeId) as count, s.storeId from Store s where
	s.legacyId = 'f6f6f6';