DROP TABLE IF EXISTS t_tephra_workbench_domain;
CREATE TABLE t_tephra_workbench_domain
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用key',
  c_name VARCHAR(255) NOT NULL COMMENT '显示名称',
  c_register DATETIME DEFAULT NULL COMMENT '注册时间',
  c_valid DATE DEFAULT NULL COMMENT '有效日期',
  c_status INT DEFAULT 0 COMMENT '状态',

  PRIMARY KEY pk_tephra_workbench_domain(c_id),
  UNIQUE KEY uk_tephra_workbench_domain_key(c_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS t_tephra_workbench_user;
CREATE TABLE t_tephra_workbench_user
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_domain_id CHAR(36) NOT NULL COMMENT '所属域ID',
  c_username VARCHAR(255) NOT NULL COMMENT '用户名',
  c_password CHAR(32) DEFAULT NULL COMMENT '密码MD5值',
  c_realname VARCHAR(255) NOT NULL COMMENT '真实姓名',
  c_register DATETIME DEFAULT NULL COMMENT '注册时间',
  c_status INT DEFAULT 0 COMMENT '状态',

  PRIMARY KEY pk_tephra_workbench_user(c_id),
  UNIQUE KEY uk_tephra_workbench_user_domain_username(c_domain_id, c_username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS t_tephra_workbench_domain_2;
CREATE TABLE t_tephra_workbench_domain_2
(
  id CHAR(36) NOT NULL COMMENT '主键',
  name VARCHAR(255) NOT NULL COMMENT '显示名称',
  register DATETIME DEFAULT NULL COMMENT '注册时间',
  valid DATE DEFAULT NULL COMMENT '有效日期',
  status INT DEFAULT 0 COMMENT '状态',

  PRIMARY KEY pk_tephra_workbench_domain(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

