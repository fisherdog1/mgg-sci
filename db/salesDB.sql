drop table if exists Subscription;
drop table if exists Service;
drop table if exists Item;
drop table if exists Sale;
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
	address varchar(200) unique not null
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

alter table PersonEmail
	add constraint personEmail_nodup unique(personId, emailId);

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
    legacyId varchar(200) not null,
    saleId int, #Null indicates prototype
    foreign key (saleId) references Sale(saleId),
    
	newUsed ENUM('new','used','card'),
    basePrice int, #Base price in cents
	quantity int
)engine=InnoDB,collate=latin1_general_cs;

alter table Item
	add constraint item_nodup unique(saleId, legacyId);

create table Service (
	productId int not null primary key auto_increment,
    productName varchar(200) not null,
    legacyId varchar(200) not null,
    saleId int, #Null indicates prototype
    foreign key (saleId) references Sale(saleId),
    
    baseRate int, #Base rate in cents per hour
	hours float,
    salespersonId int, #Also null for prototype
    foreign key (salespersonId) references Person(personId)
)engine=InnoDB,collate=latin1_general_cs;

alter table Service
	add constraint service_nodup unique(saleId, legacyId);

create table Subscription (
	productId int not null primary key auto_increment,
    productName varchar(200) not null,
    legacyId varchar(200) not null,
    saleId int, #Null indicates prototype
    foreign key (saleId) references Sale(saleId),
    
    baseRate int, #Base rate in cents per year
	startDate date,
    endDate date
)engine=InnoDB,collate=latin1_general_cs;

#Technically this should stop subscriptions from overlapping and allow
#duplicates otherwise. Not sure how to do this.
alter table Subscription
	add constraint subscription_nodup unique(saleId, legacyId);