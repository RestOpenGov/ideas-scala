# --- First database schema

# --- !Ups

INSERT INTO `user` VALUES
  (5, 
  'Nicolas',
  'Nicolas Melendez',      
  'nfmelendez@gmail.com',   
  'http://profile.ak.fbcdn.net/hprofile-ak-ash4/276893_244434932260649_848703319_n.jpg',  
  '2012-10-20 11:45:00');

INSERT INTO `subscription` VALUES
  (09, 1, 5),

# --- !Downs

delete from `user` where id = 5;
delete from `subscription` where id = 09;