insert into student (index_number, first_name, last_name) values ('ra10-2014', 'Stevan', 'Stević');
insert into student (index_number, first_name, last_name) values ('ra11-2014', 'Stefan', 'Stefanović');
insert into student (index_number, first_name, last_name) values ('ra12-2014', 'Aleksa', 'Stević');

insert into course (name) values ('Matematika');
insert into course (name) values ('Osnove programiranja');
insert into course (name) values ('Objektno programiranje');

insert into exam (student_id, course_id, date, grade) values (1, 1, '2016-02-01', 9);
insert into exam (student_id, course_id, date, grade) values (1, 2, '2016-04-19', 8);
insert into exam (student_id, course_id, date, grade) values (2, 1, '2016-02-01', 10);
insert into exam (student_id, course_id, date, grade) values (2, 2, '2016-04-19', 10);