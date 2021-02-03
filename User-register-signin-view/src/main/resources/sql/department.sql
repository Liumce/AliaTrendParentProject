create table if not exists users(
  id bigint not null PRIMARY KEY auto_increment,
  email varchar(100) not null,
  password varchar(100) not null,
  name varchar(100) not null,
  createdAt BIGINT NOT NULL,
  UNIQUE (email)
);