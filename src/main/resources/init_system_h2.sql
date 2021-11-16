/* system_user */
INSERT INTO system_user(ID,NAME,PASSWORD,SALT,SEX,DESCRIPTION,IS_LOCKED,CREATE_DATE,REGISTER_IP,OPENID,PROVIDERID) VALUES 
(1,'admin',NULL,NULL,0,"超级管理员",0,'2014-02-01 00:12:40','127.0.0.1','admin','local');
/*  .system_role   		*/
INSERT INTO system_role(ID,NAME,DESCRIPTION) VALUES
(1,'admin','超级管理员：拥有全部权限的角色'),
(2,'viewer','审阅者：拥有任何对象的浏览权限的角色'),
(3,'user-superadmin','用户超级管理员：拥有对用户的任意操作权限的角色'),
(4,'user-admin','用户管理员：拥有对用户的浏览、增加和编辑(不包括删除)权限的角色'),
(5,'security-admin','安全管理员：拥有对角色和权限的任意操作，对用户分配角色及对角色分配权限的权限');
/*  .system_permission   		*/
INSERT INTO `system_permission` VALUES (1,'*:*:*','全部权限','1',1),(2,'admin:role','对用户分配角色 ','721fbbca1ae44716918fdb3921deacb1',0),(3,'admin:setting','系统设置 ','800a35a4c142413c9d902f4d40245ec3',0),(4,'admin:article','文章管理 ','0b24b967550543469941c7b29e27f277',0),(5,'admin:articleCategory','文章分类 ','0b24b967550543469941c7b29e27f277',0),(6,'admin:admin','添加管理员','721fbbca1ae44716918fdb3921deacb1',0),(7,'admin:permission','权限管理','721fbbca1ae44716918fdb3921deacb1',0),(8,'admin:permissionCategory','权限分类','721fbbca1ae44716918fdb3921deacb1',0);
/*  .permission_category   		*/
INSERT INTO `permission_category` VALUES ('0b24b967550543469941c7b29e27f277','文章管理',1),('1','超级权限',1),('721fbbca1ae44716918fdb3921deacb1','用户管理',1),('800a35a4c142413c9d902f4d40245ec3','系统设置',1);
/*  .system_user_role   		*/
INSERT INTO system_user_role(USERID,ROLEID) VALUES 
(1,1);
/*  .system_role_permission  		*/
INSERT INTO system_role_permission(ROLEID,PERMISSIONID) VALUES 
(1,1),
(1,2),
(1,3),
(1,4),
(1,5),
(1,6);
/* .article_category */
INSERT INTO article_category VALUES ('6d38b6b1afb4458fb6a2eb5a5ff9641e','2015-02-21 07:56:56','2015-02-21 07:56:56',1,'教育频道',3,'729f5f63a805472c8216887abe318bb3',NULL),('729f5f63a805472c8216887abe318bb3','2015-02-17 12:06:03','2015-02-17 12:06:03',0,'一级分类',1,'',NULL),('a51b07543d8f42b68d6b85864b277b47','2015-02-17 12:06:16','2015-02-17 12:06:16',1,'二级分类',2,'729f5f63a805472c8216887abe318bb3',NULL),('f910a34c9cbd4ece98363ca8398d995b','2015-02-21 08:05:05','2015-02-21 08:05:05',2,'学校教育',1,'6d38b6b1afb4458fb6a2eb5a5ff9641e',NULL);
/*  .system_setting  		*/
INSERT INTO system_setting(id,site_name,certtext,site_enabled,site_close_message) VALUES (1,'瑞克科技','Cms Game',1,'测试阶段<a herf="#">查看更多.....</a>');
/* .crawler_rule */
INSERT INTO `crawler_rule` VALUES (1,'39健康-老人健康',NULL,NULL,0,0,0,0,500,'GBK','http://oldman.39.net/nutrition/','',2,0,'class=listbox','',NULL,NULL,0,0,NULL,NULL,'','','id=contentText','IFRAME,align=right','','',NULL,0,0,0,'',80,'39健康=美食汇,老人','','','','','','','','','f910a34c9cbd4ece98363ca8398d995b',1000);

