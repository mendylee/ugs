/*==============================================================*/
/* DBMS name:      PostgreSQL 8                                 */
/* Created on:     2015/9/28 14:52:09                           */
/*==============================================================*/


/*==============================================================*/
/* Table: ugs_growing_active_group                              */
/*==============================================================*/
create table ugs_growing_active_group (
   type_code            VARCHAR(30)          not null,
   rule_group_id        INT4                 not null,
   constraint PK_UGS_GROWING_ACTIVE_GROUP primary key (type_code)
);

/*==============================================================*/
/* Table: ugs_growing_rule_group                                */
/*==============================================================*/
create table ugs_growing_rule_group (
   rule_group_id        SERIAL not null,
   type_code            VARCHAR(30)          not null,
   rule_group_name      VARCHAR(60)          not null,
   status               INT2                 not null,
   constraint PK_UGS_GROWING_RULE_GROUP primary key (rule_group_id)
);

comment on column ugs_growing_rule_group.status is
'0-新建，10-审核失败待修改，1-待审核，2-可使用，3-禁用，4-删除';

/*==============================================================*/
/* Index: idx_growing_rule_group_uni                            */
/*==============================================================*/
create unique index idx_growing_rule_group_uni on ugs_growing_rule_group (
type_code
);

/*==============================================================*/
/* Table: ugs_growing_rule_list                                 */
/*==============================================================*/
create table ugs_growing_rule_list (
   rule_list_id         SERIAL not null,
   rule_group_id        INT4                 not null,
   rule_code            VARCHAR(50)          not null,
   rule_name            VARCHAR(100)         not null,
   plugin_id            INT4                 not null,
   add_date             TIMESTAMP            null,
   user_id              INT4                 null,
   constraint PK_UGS_GROWING_RULE_LIST primary key (rule_list_id)
);

/*==============================================================*/
/* Index: idx_growing_rule_list_uni                             */
/*==============================================================*/
create unique index idx_growing_rule_list_uni on ugs_growing_rule_list (
rule_group_id,
rule_code
);

/*==============================================================*/
/* Table: ugs_growing_rule_parameter                            */
/*==============================================================*/
create table ugs_growing_rule_parameter (
   rule_list_id         INT4                 not null,
   param_name           VARCHAR(50)          not null,
   param_value          VARCHAR(255)         not null,
   constraint PK_UGS_GROWING_RULE_PARAMETER primary key (rule_list_id, param_name)
);

/*==============================================================*/
/* Table: ugs_growing_rule_plugin                               */
/*==============================================================*/
create table ugs_growing_rule_plugin (
   plugin_id            SERIAL not null,
   plugin_name          VARCHAR(100)         not null,
   plugin_class         VARCHAR(200)         not null,
   description          VARCHAR(255)         null,
   add_date             TIMESTAMP            null,
   version              VARCHAR(20)          null,
   constraint PK_UGS_GROWING_RULE_PLUGIN primary key (plugin_id)
);

/*==============================================================*/
/* Table: ugs_growing_type                                      */
/*==============================================================*/
create table ugs_growing_type (
   type_code            VARCHAR(30)          not null,
   type_name            VARCHAR(60)          not null,
   status               INT2                 not null,
   constraint PK_UGS_GROWING_TYPE primary key (type_code)
);

comment on column ugs_growing_type.status is
'0-无效，1-有效';

/*==============================================================*/
/* Table: ugs_observer                                          */
/*==============================================================*/
create table ugs_observer (
   observer_id          SERIAL not null,
   client_name          VARCHAR(100)         not null,
   subscibe             VARCHAR(200)         not null,
   callback_url         VARCHAR(200)         not null,
   client_ip            VARCHAR(20)          null,
   add_date             TIMESTAMP            null,
   constraint PK_UGS_OBSERVER primary key (observer_id)
);

/*==============================================================*/
/* Table: ugs_point_type                                        */
/*==============================================================*/
create table ugs_point_type (
   point_type_id        SERIAL not null,
   type_code            VARCHAR(30)          not null,
   point_type_code      VARCHAR(30)          not null,
   point_type_name      VARCHAR(60)          not null,
   constraint PK_UGS_POINT_TYPE primary key (point_type_id)
);

/*==============================================================*/
/* Index: idx_point_type_uni                                    */
/*==============================================================*/
create  index idx_point_type_uni on ugs_point_type (
type_code,
point_type_code
);

/*==============================================================*/
/* Table: ugs_process_log                                       */
/*==============================================================*/
create table ugs_process_log (
   log_id               SERIAL not null,
   rule_group_id        INT4                 not null,
   add_date             DATE                 not null,
   user_name            VARCHAR(100)         null,
   description          VARCHAR(255)         null,
   constraint PK_UGS_PROCESS_LOG primary key (log_id)
);

/*==============================================================*/
/* Table: ugs_user_grade_config                                 */
/*==============================================================*/
create table ugs_user_grade_config (
   grade_id             SERIAL not null,
   point_type_id        INT4                 not null,
   type_code            VARCHAR(30)          not null,
   grade_name           VARCHAR(60)          not null,
   constraint PK_UGS_USER_GRADE_CONFIG primary key (grade_id)
);

/*==============================================================*/
/* Table: ugs_user_grade_config_list                            */
/*==============================================================*/
create table ugs_user_grade_config_list (
   grade_id             INT4                 not null,
   level                INT2                 not null,
   point                INT4                 not null,
   constraint PK_UGS_USER_GRADE_CONFIG_LIST primary key (grade_id, level)
);

/*==============================================================*/
/* Table: ugs_user_info                                         */
/*==============================================================*/
create table ugs_user_info (
   uid_seq_id           SERIAL not null,
   uid                  INT8                 not null,
   type_code            VARCHAR(30)          not null,
   add_date             TIMESTAMP            not null,
   constraint PK_UGS_USER_INFO primary key (uid_seq_id)
);

/*==============================================================*/
/* Index: idx_user_info_query                                   */
/*==============================================================*/
create  index idx_user_info_query on ugs_user_info (
uid,
type_code
);

/*==============================================================*/
/* Table: ugs_user_point                                        */
/*==============================================================*/
create table ugs_user_point (
   uid_seq              INT8                 not null,
   point_type_id        INT4                 not null,
   point                INT4                 not null,
   edit_date            TIMESTAMP            not null,
   constraint PK_UGS_USER_POINT primary key (uid_seq, point_type_id)
);

/*==============================================================*/
/* Index: idx_user_point_query                                  */
/*==============================================================*/
create  index idx_user_point_query on ugs_user_point (
uid_seq,
point_type_id
);

/*==============================================================*/
/* Table: ugs_user_point_history                                */
/*==============================================================*/
create table ugs_user_point_history (
   history_id           SERIAL not null,
   uid_seq              INT8                 not null,
   add_date             TIMESTAMP            not null,
   type_code            VARCHAR(30)          not null,
   type_name            VARCHAR(60)          not null,
   rule_code            VARCHAR(50)          not null,
   rule_name            VARCHAR(100)         not null,
   point                INT4                 not null,
   description          VARCHAR(255)         null,
   constraint PK_UGS_USER_POINT_HISTORY primary key (history_id)
);

comment on column ugs_user_point_history.point is
'如果是扣分则为负数';

/*==============================================================*/
/* Index: idx_point_history_query                               */
/*==============================================================*/
create  index idx_point_history_query on ugs_user_point_history (
uid_seq,
type_code,
add_date,
rule_code
);

/*==============================================================*/
/* Index: idx_point_history_query_code                          */
/*==============================================================*/
create  index idx_point_history_query_code on ugs_user_point_history (
uid_seq,
rule_code,
add_date
);

alter table ugs_growing_active_group
   add constraint FK_UGS_GRWO_REF_UGS_GROW_1 foreign key (type_code)
      references ugs_growing_type (type_code)
      on delete restrict on update restrict;

alter table ugs_growing_active_group
   add constraint FK_UGS_GRWO_REF_UGS_GROW_GROUP foreign key (rule_group_id)
      references ugs_growing_rule_group (rule_group_id)
      on delete restrict on update restrict;

alter table ugs_growing_rule_group
   add constraint FK_UGS_GROW_GROUP_REF_UGS_GROW foreign key (type_code)
      references ugs_growing_type (type_code)
      on delete restrict on update restrict;

alter table ugs_growing_rule_list
   add constraint FK_UGS_GROW_REF_UGS_GROW_LIST2 foreign key (rule_group_id)
      references ugs_growing_rule_group (rule_group_id)
      on delete restrict on update restrict;

alter table ugs_growing_rule_list
   add constraint FK_UGS_GROW_REF_UGS_GROW_LIST foreign key (plugin_id)
      references ugs_growing_rule_plugin (plugin_id)
      on delete restrict on update restrict;

alter table ugs_growing_rule_parameter
   add constraint FK_UGS_GROW_REF_UGS_GROW_PARAM foreign key (rule_list_id)
      references ugs_growing_rule_list (rule_list_id)
      on delete restrict on update restrict;

alter table ugs_point_type
   add constraint FK_UGS_POIN_REFERENCE_UGS_GROW foreign key (type_code)
      references ugs_growing_type (type_code)
      on delete restrict on update restrict;

alter table ugs_process_log
   add constraint FK_UGS_PROC_REF_UGS_GROW_LOG foreign key (rule_group_id)
      references ugs_growing_rule_group (rule_group_id)
      on delete restrict on update restrict;

alter table ugs_user_grade_config
   add constraint FK_UGS_USER_REF_UGS_POIN_CONFIG foreign key (point_type_id)
      references ugs_point_type (point_type_id)
      on delete restrict on update restrict;

alter table ugs_user_grade_config
   add constraint FK_UGS_USER_REF_UGS_GROW_CONF foreign key (type_code)
      references ugs_growing_type (type_code)
      on delete restrict on update restrict;

alter table ugs_user_grade_config_list
   add constraint FK_UGS_USER_REFERENCE_UGS_USER foreign key (grade_id)
      references ugs_user_grade_config (grade_id)
      on delete restrict on update restrict;

alter table ugs_user_info
   add constraint FK_UGS_USER_REF_UGS_GROW_INFO foreign key (type_code)
      references ugs_growing_type (type_code)
      on delete restrict on update restrict;

alter table ugs_user_point
   add constraint FK_UGS_USER_REFE_UGS_USER_POINT foreign key (uid_seq)
      references ugs_user_info (uid_seq_id)
      on delete restrict on update restrict;

alter table ugs_user_point
   add constraint FK_UGS_USER_REF_UGS_POIN_TYPE foreign key (point_type_id)
      references ugs_point_type (point_type_id)
      on delete restrict on update restrict;

alter table ugs_user_point_history
   add constraint FK_UGS_USER_REF_UGS_USER_HIS foreign key (uid_seq)
      references ugs_user_info (uid_seq_id)
      on delete restrict on update restrict;

