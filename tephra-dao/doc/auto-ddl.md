# 自动建表

Model初始化完毕后，会检测Model对应的表是否存在，如果不存在并且该Model所在目录下包含名为`create.sql`的DDL文件时，将自动执行该DDL文件创建表结构。
