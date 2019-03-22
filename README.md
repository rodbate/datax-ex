![Datax-logo](http://gitlab.egomsl.com/datax/datax-all/blob/master/images/DataX-logo.jpg)


# 源码构建

```
mvn validate
mvn -U clean package -Dmaven.test.skip=true

cd distribution/target/datax-{version}
```


# DataX

DataX 是阿里巴巴集团内被广泛使用的离线数据同步工具/平台，实现包括 MySQL、Oracle、SqlServer、Postgre、HDFS、Hive、ADS、HBase、TableStore(OTS)、MaxCompute(ODPS)、DRDS 等各种异构数据源之间高效的数据同步功能。



# Features

DataX本身作为数据同步框架，将不同数据源的同步抽象为从源头数据源读取数据的Reader插件，以及向目标端写入数据的Writer插件，理论上DataX框架可以支持任意数据源类型的数据同步工作。同时DataX插件体系作为一套生态系统, 每接入一套新数据源该新加入的数据源即可实现和现有的数据源互通。



# DataX详细介绍

##### 请参考：[DataX-Introduction](http://gitlab.egomsl.com/datax/datax-all/blob/master/introduction.md)



# Quick Start

##### Download [DataX下载地址](http://gitlab.egomsl.com/datax/datax-all/tags)

##### 请点击：[Quick Start](http://gitlab.egomsl.com/datax/datax-all/blob/master/userGuid.md)



# Support Data Channels 

DataX目前已经有了比较全面的插件体系，主流的RDBMS数据库、NOSQL、大数据计算系统都已经接入，目前支持数据如下图，详情请点击：[DataX数据源参考指南](http://gitlab.egomsl.com/datax/datax-all/blob/wiki/DataX-all-data-channels)

| 类型           | 数据源        | Reader(读) | Writer(写) |文档|
| ------------ | ---------- | :-------: | :-------: |:-------: |
| RDBMS 关系型数据库 | MySQL      |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/mysqlreader/doc/mysqlreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/mysqlwriter/doc/mysqlwriter.md)|
|              | Oracle     |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/oraclereader/doc/oraclereader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/oraclewriter/doc/oraclewriter.md)|
|              | SQLServer  |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/sqlserverreader/doc/sqlserverreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/sqlserverwriter/doc/sqlserverwriter.md)|
|              | PostgreSQL |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/postgresqlreader/doc/postgresqlreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/postgresqlwriter/doc/postgresqlwriter.md)|
|              | DRDS |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/drdsreader/doc/drdsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/drdswriter/doc/drdswriter.md)|
|              | 通用RDBMS(支持所有关系型数据库)         |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/rdbmsreader/doc/rdbmsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/rdbmswriter/doc/rdbmswriter.md)|
| 阿里云数仓数据存储    | ODPS       |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/odpsreader/doc/odpsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/odpswriter/doc/odpswriter.md)|
|              | ADS        |           |     √     |[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/adswriter/doc/adswriter.md)|
|              | OSS        |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/ossreader/doc/ossreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/osswriter/doc/osswriter.md)|
|              | OCS        |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/ocsreader/doc/ocsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/ocswriter/doc/ocswriter.md)|
| NoSQL数据存储    | OTS        |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/otsreader/doc/otsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/otswriter/doc/otswriter.md)|
|              | Hbase0.94  |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/hbase094xreader/doc/hbase094xreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/hbase094xwriter/doc/hbase094xwriter.md)|
|              | Hbase1.1   |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/hbase11xreader/doc/hbase11xreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/hbase11xwriter/doc/hbase11xwriter.md)|
|              | Phoenix4.x   |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/hbase11xsqlreader/doc/hbase11xsqlreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/hbase11xsqlwriter/doc/hbase11xsqlwriter.md)|
|              | MongoDB    |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/mongoreader/doc/mongoreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/mongowriter/doc/mongowriter.md)|
|              | Hive       |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/hdfsreader/doc/hdfsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/hdfswriter/doc/hdfswriter.md)|
| 无结构化数据存储     | TxtFile    |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/txtfilereader/doc/txtfilereader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/txtfilewriter/doc/txtfilewriter.md)|
|              | FTP        |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/ftpreader/doc/ftpreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/ftpwriter/doc/ftpwriter.md)|
|              | HDFS       |     √     |     √     |[读](http://gitlab.egomsl.com/datax/datax-all/blob/master/hdfsreader/doc/hdfsreader.md) 、[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/hdfswriter/doc/hdfswriter.md)|
|              | Elasticsearch       |         |     √     |[写](http://gitlab.egomsl.com/datax/datax-all/blob/master/elasticsearchwriter/doc/elasticsearchwriter.md)|





