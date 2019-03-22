# DataX

DataX 是阿里巴巴集团内被广泛使用的离线数据同步工具/平台，实现包括 MySQL、SQL Server、Oracle、PostgreSQL、HDFS、Hive、HBase、OTS、ODPS 等各种异构数据源之间高效的数据同步功能。

# Features

DataX本身作为数据同步框架，将不同数据源的同步抽象为从源头数据源读取数据的Reader插件，以及向目标端写入数据的Writer插件，理论上DataX框架可以支持任意数据源类型的数据同步工作。同时DataX插件体系作为一套生态系统, 每接入一套新数据源该新加入的数据源即可实现和现有的数据源互通。

# System Requirements

- Linux
- [JDK(1.8以上，推荐1.8) ](http://www.oracle.com/technetwork/cn/java/javase/downloads/index.html) 
- [Python(推荐Python2.6.X) ](https://www.python.org/downloads/)
- [Apache Maven 3.x](https://maven.apache.org/download.cgi) (Compile DataX)

# Quick Start

* 工具部署
  
  * 方法一、直接下载DataX工具包：[DataX下载地址](http://gitlab.egomsl.com/datax/datax-all/tags) 下载对应版本
    
    下载后解压至本地某个目录，进入bin目录，即可运行同步作业：
    
    ``` shell
    $ cd  {YOUR_DATAX_HOME}/bin
    $ python datax.py {YOUR_JOB.json}
    ```
    自检脚本：
    python {YOUR_DATAX_HOME}/bin/datax.py {YOUR_DATAX_HOME}/job/job.json
  * 方法二、下载DataX源码，自己编译：[DataX源码](http://gitlab.egomsl.com/datax/datax-all.git)
    
    (1)、下载DataX源码：
    
    ``` shell
    $ git clone http://gitlab.egomsl.com/datax/datax-all.git
    ```
    
    (2)、通过maven打包：
    
    ``` shell
    $ cd  {DataX_source_code_home}
    $ mvn validate
    $ mvn -U clean package -Dmaven.test.skip=true
      
      cd 
    ```
    
    打包成功，日志显示如下：
    
    ``` 
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary for datax-all 1.0.0:
    [INFO]
    [INFO] datax-all .......................................... SUCCESS [  0.135 s]
    [INFO] datax-common ....................................... SUCCESS [  3.659 s]
    [INFO] datax-transformer .................................. SUCCESS [  1.615 s]
    [INFO] datax-core ......................................... SUCCESS [  3.882 s]
    [INFO] plugin-rdbms-util .................................. SUCCESS [  1.095 s]
    [INFO] mysqlreader ........................................ SUCCESS [  0.928 s]
    [INFO] drdsreader ......................................... SUCCESS [  0.444 s]
    [INFO] sqlserverreader .................................... SUCCESS [  0.419 s]
    [INFO] postgresqlreader ................................... SUCCESS [  0.700 s]
    [INFO] oraclereader ....................................... SUCCESS [  0.440 s]
    [INFO] odpsreader ......................................... SUCCESS [  2.199 s]
    [INFO] otsreader .......................................... SUCCESS [  2.443 s]
    [INFO] otsstreamreader .................................... SUCCESS [  1.563 s]
    [INFO] plugin-unstructured-storage-util ................... SUCCESS [  2.112 s]
    [INFO] txtfilereader ...................................... SUCCESS [  3.759 s]
    [INFO] hdfsreader ......................................... SUCCESS [  9.974 s]
    [INFO] streamreader ....................................... SUCCESS [  0.401 s]
    [INFO] ossreader .......................................... SUCCESS [  1.800 s]
    [INFO] ftpreader .......................................... SUCCESS [  1.474 s]
    [INFO] mongodbreader ...................................... SUCCESS [  1.679 s]
    [INFO] rdbmsreader ........................................ SUCCESS [  1.060 s]
    [INFO] hbase11xreader ..................................... SUCCESS [  6.061 s]
    [INFO] hbase094xreader .................................... SUCCESS [  7.966 s]
    [INFO] mysqlwriter ........................................ SUCCESS [  0.376 s]
    [INFO] drdswriter ......................................... SUCCESS [  0.400 s]
    [INFO] odpswriter ......................................... SUCCESS [  0.876 s]
    [INFO] txtfilewriter ...................................... SUCCESS [  1.307 s]
    [INFO] ftpwriter .......................................... SUCCESS [  1.160 s]
    [INFO] hdfswriter ......................................... SUCCESS [  3.048 s]
    [INFO] streamwriter ....................................... SUCCESS [  0.654 s]
    [INFO] otswriter .......................................... SUCCESS [  1.254 s]
    [INFO] oraclewriter ....................................... SUCCESS [  1.117 s]
    [INFO] sqlserverwriter .................................... SUCCESS [  0.920 s]
    [INFO] postgresqlwriter ................................... SUCCESS [  0.642 s]
    [INFO] osswriter .......................................... SUCCESS [  1.465 s]
    [INFO] mongodbwriter ...................................... SUCCESS [  1.296 s]
    [INFO] adswriter .......................................... SUCCESS [  3.559 s]
    [INFO] ocswriter .......................................... SUCCESS [ 11.899 s]
    [INFO] rdbmswriter ........................................ SUCCESS [  0.465 s]
    [INFO] hbase11xwriter ..................................... SUCCESS [  2.170 s]
    [INFO] hbase094xwriter .................................... SUCCESS [  1.436 s]
    [INFO] hbase11xsqlwriter .................................. SUCCESS [  8.272 s]
    [INFO] hbase11xsqlreader .................................. SUCCESS [  7.440 s]
    [INFO] elasticsearchwriter ................................ SUCCESS [  3.489 s]
    [INFO] distribution ....................................... SUCCESS [08:48 min]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  10:38 min
    [INFO] Finished at: 2019-02-25T13:50:52+08:00
    [INFO] ------------------------------------------------------------------------

    ```
    
    打包成功后的DataX包位于 {DataX_source_code_home}/distribution/target/datax-{version}/ ，结构如下：
    
    ``` shell
    $ cd  {DataX_source_code_home}
    $ ls ./target/datax-{version}/
    bin		conf		job		lib		log		log_perf	plugin
    ```


* 配置示例：从stream读取数据并打印到控制台
  
  * 第一步、创建创业的配置文件（json格式）
    
    可以通过命令查看配置模板： python datax.py -r {YOUR_READER} -w {YOUR_WRITER}
    
    ``` shell
    $ cd  {YOUR_DATAX_HOME}/bin
    $  python datax.py -r streamreader -w streamwriter
    DataX (UNKNOWN_DATAX_VERSION), From Alibaba !
    Copyright (C) 2010-2015, Alibaba Group. All Rights Reserved.
    Please refer to the streamreader document:
        http://gitlab.egomsl.com/datax/datax-all/blob/master/streamreader/doc/streamreader.md 
    
    Please refer to the streamwriter document:
         http://gitlab.egomsl.com/datax/datax-all/blob/master/streamwriter/doc/streamwriter.md 
     
    Please save the following configuration as a json file and  use
         python {DATAX_HOME}/bin/datax.py {JSON_FILE_NAME}.json 
    to run the job.
    
    {
        "job": {
            "content": [
                {
                    "reader": {
                        "name": "streamreader", 
                        "parameter": {
                            "column": [], 
                            "sliceRecordCount": ""
                        }
                    }, 
                    "writer": {
                        "name": "streamwriter", 
                        "parameter": {
                            "encoding": "", 
                            "print": true
                        }
                    }
                }
            ], 
            "setting": {
                "speed": {
                    "channel": ""
                }
            }
        }
    }
    ```
    
    根据模板配置json如下：
    
    ``` json
    #stream2stream.json
    {
      "job": {
        "content": [
          {
            "reader": {
              "name": "streamreader",
              "parameter": {
                "sliceRecordCount": 10,
                "column": [
                  {
                    "type": "long",
                    "value": "10"
                  },
                  {
                    "type": "string",
                    "value": "hello，你好，世界-DataX"
                  }
                ]
              }
            },
            "writer": {
              "name": "streamwriter",
              "parameter": {
                "encoding": "UTF-8",
                "print": true
              }
            }
          }
        ],
        "setting": {
          "speed": {
            "channel": 5
           }
        }
      }
    }
    ```
    
  * 第二步：启动DataX
    
    ``` shell
    $ cd {YOUR_DATAX_DIR_BIN}
    $ python datax.py ./stream2stream.json 
    ```
    
    同步结束，显示日志如下：
    
    ``` shell
    ...
    2015-12-17 11:20:25.263 [job-0] INFO  JobContainer - 
    任务启动时刻                    : 2015-12-17 11:20:15
    任务结束时刻                    : 2015-12-17 11:20:25
    任务总计耗时                    :                 10s
    任务平均流量                    :              205B/s
    记录写入速度                    :              5rec/s
    读出记录总数                    :                  50
    读写失败总数                    :                   0
    ```

# Contact us

Google Groups: [DataX-user](https://github.com/alibaba/DataX)




