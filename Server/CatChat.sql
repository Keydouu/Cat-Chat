create table if not exists `messages` (
    `message_id` int(11) not null auto_increment primary key,
    `sender_Email` varchar(255) not null,
    `receiver_Email` varchar(255) not null,
    `message` mediumblob not null,
    `messageType` varchar(10) default 'text',
    `fileName` varchar(255),
    `date` datetime not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists `users` (
    `user_id` int(11) not null auto_increment primary key,
    `email` varchar(255) not null,
    `password` varchar(255) not null,
    `name` varchar(255) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists `users_groups` (
    `user_id` int(11) not null,
    `group_id` int(11) not null,
    primary key (`user_id`, `group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists `Groupe` (
    `Groupe_id` int(11) not null auto_increment primary key,
    `Groupe_name` varchar(255) not null,
    `Groupe_description` varchar(255),
    `Groupe_admin_id` int(11) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists `Groupe_Message_content` (
    `GMC_id` int(11) not null auto_increment primary key,
    `Groupe_id` int(11) not null,
    `sender_name` varchar(255) not null,
    `sender_Email` varchar(255) not null,
    `content` mediumblob not null,
    `messageType` varchar(10) default 'text',
    `fileName` varchar(255),
    `date` datetime not null,
    foreign key (`Groupe_id`) references `Groupe`(`Groupe_id`) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists `Groupe_Messages`(
    `message_id` int(11) not null auto_increment primary key,
    `receiver_id` int(11) not null,
    `GMC_id` int(11) not null,
    foreign key (`receiver_id`) references `users` (`user_id`) on delete cascade,
    foreign key (`GMC_id`) references `Groupe_Message_content` (`GMC_id`) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into users (email, password, name) values ('Youness@gmail.com','159753','Youness');
insert into users (email, password, name) values ('Ayoub@gmail.com','123456','Ayoub');
insert into users (email, password, name) values ('Yassin@gmail.com','azerty','yassin');
insert into users (email, password, name) values ('Ayman@gmail.com','456789','Ayman');
insert into users (email, password, name) values ('Rachid@gmail.com','258456','Rachid');
insert into users (email, password, name) values ('Mohamed@gmail.com','qsdfgh','mohamed');