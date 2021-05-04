drop table if exists Subscription; #Subscription
drop table if exists Service; #Service
drop table if exists Item; #New or used item
drop table if exists Sale; #Sale
drop table if exists Store;
drop table if exists PersonEmail;
drop table if exists Person;
drop table if exists Email;
drop table if exists Address;

create table Address (
	addressId int not null primary key auto_increment,
	street varchar(200) not null,
    city varchar(200) not null,
    state varchar(200) not null,
    zip varchar(200) not null,
    country varchar(200) not null
)engine=InnoDB,collate=latin1_general_cs;

create table Email (
	emailId int not null primary key auto_increment,
	address varchar(200) not null
)engine=InnoDB,collate=latin1_general_cs;

create table Person (
  personId int not null primary key auto_increment,
  legacyId varchar(200) unique not null,
  firstName varchar(200) not null,
  lastName varchar(200) not null,
  customerType ENUM('customer','gold','platinum','employee') not null,
  addressId int not null,
  foreign key (addressId) references Address(addressId)
)engine=InnoDB,collate=latin1_general_cs;

create table PersonEmail (
  personEmailId int not null primary key auto_increment,
  emailId int not null,
  personId int not null,
  foreign key (emailId) references Email(emailId),
  foreign key (personId) references Person(personId)
)engine=InnoDB,collate=latin1_general_cs;

create table Store (
	storeId int not null primary key auto_increment,
	legacyId varchar(200) unique not null,
    managerId int not null,
    addressId int not null,
    foreign key (managerId) references Person(personId),
    foreign key (addressId) references Address(addressId)
)engine=InnoDB,collate=latin1_general_cs;

create table Sale (
	saleId int not null primary key auto_increment,
    legacyId varchar(200) unique not null,
	storeId int not null,
    foreign key (storeId) references Store(storeId),
	customerId int not null,
    foreign key (customerId) references Person(personId),
    salespersonId int not null,
    foreign key (salespersonid) references Person(personId)
)engine=InnoDB,collate=latin1_general_cs;

#For gift cards the quantity is set to 1 and basePrice set to card amount
create table Item (
	productId int not null primary key auto_increment,
    productName varchar(200) not null,
    legacyId varchar(200) unique not null,
    saleId int, #Null indicates prototype
    foreign key (saleId) references Sale(saleId),
    
	newUsed ENUM('new','used','card'),
    basePrice int, #Base price in cents
	quantity int
)engine=InnoDB,collate=latin1_general_cs;

create table Service (
	productId int not null primary key auto_increment,
    productName varchar(200) not null,
    legacyId varchar(200) unique not null,
    saleId int, #Null indicates prototype
    foreign key (saleId) references Sale(saleId),
    
    baseRate int, #Base rate in cents per hour
	hours float,
    salespersonId int, #Also null for prototype
    foreign key (salespersonId) references Person(personId)
)engine=InnoDB,collate=latin1_general_cs;

create table Subscription (
	productId int not null primary key auto_increment,
    productName varchar(200) not null,
    legacyId varchar(200) unique not null,
    saleId int, #Null indicates prototype
    foreign key (saleId) references Sale(saleId),
    
    baseRate int, #Base rate in cents per year
	startDate date,
    endDate date
)engine=InnoDB,collate=latin1_general_cs;

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
    
select * from PersonEmail;
select * from Address;
select * from Sale;
select * from Person;
select * from Service;
select * from Subscription;
select * from Item;

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