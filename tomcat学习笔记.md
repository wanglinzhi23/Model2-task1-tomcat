#Tomcat学习笔记

## 1.tomcat架构

#### 1.整体架构

![](./imgs/bj1.png)

HTTP 服务器接收到请求之后把请求交给Servlet容器来处理，Servlet 容器通过Servlet接⼝调⽤业务

类。Servlet接⼝和Servlet容器这⼀整套内容叫作Servlet规范。

注意：Tomcat既按照Servlet规范的要求去实现了Servlet容器，同时它也具有HTTP服务器的功能。

Tomcat的两个重要身份

​	1）http服务器也可以看成一个连接器（Connector）

​	2）Servlet容器（Container）



#### 2.请求处理流程

![](./imgs/bj2.jpg)

当⽤户请求某个URL资源时

1）HTTP服务器会把请求信息使⽤ServletRequest对象封装起来

2）进⼀步去调⽤Servlet容器中某个具体的Servlet

3）在 2）中，Servlet容器拿到请求后，根据URL和Servlet的映射关系，找到相应的Servlet

4）如果Servlet还没有被加载，就⽤反射机制创建这个Servlet，并调⽤Servlet的init⽅法来完成初始化

5）接着调⽤这个具体Servlet的service⽅法来处理请求，请求处理结果使⽤ServletResponse对象封装

6）把ServletResponse对象返回给HTTP服务器，HTTP服务器会把响应发送给客户端



#### 3.tomcat 连接器组件 Coyote

Coyote 是Tomcat 中连接器的组件名称 , 是对外的接⼝。客户端通过Coyote与服务器建⽴连接、发送请

求并接受响应 。

（1）Coyote 封装了底层的⽹络通信（Socket 请求及响应处理）

（2）Coyote 使Catalina 容器（容器组件）与具体的请求协议及IO操作⽅式完全解耦

（3）Coyote 将Socket 输⼊转换封装为 Request 对象，进⼀步封装后交由Catalina 容器进⾏处理，处理请求完成后, Catalina 通过Coyote 提供的Response 对象将结果写⼊输出流

（4）Coyote 负责的是具体协议（应⽤层）和IO（传输层）相关内容

Tomcat Coyote ⽀持的 IO模型与协议:

应用层协议支持：

| 应用层协议 | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| http/1.1   | http1.1版本，也是web浏览器最常用的协议                       |
| AJP        | 用于喝WX集成（如Apcahe），以实现对静态资源的优化以及集群部署，当前支持AJP/1.3 |
| HTTP/2.0   | HTTP2.0大幅提升了web性能，下一代的http协议，自tomcat版本8.5以及9.0之后支持的 |

传输层IO模型支持：

| IO模型 | 描述                                                         |
| ------ | ------------------------------------------------------------ |
| BIO    | 传统阻塞式IO模型                                             |
| NIO    | 非阻塞IO，采用javaNIO类库实现                                |
| NIO2   | 异步IO，采用JDK7最新的NIO2类库实现                           |
| APR    | 采用Apache可移植运行库实现，是C/C++编写的本地库，如果选择该方案，需要单独安装APR库 |

在 8.0 之前 ，Tomcat 默认采⽤的I/O⽅式为 BIO，之后改为 NIO。 ⽆论 NIO、NIO2 还是 APR， 在性

能⽅⾯均优于以往的BIO。 如果采⽤APR， 甚⾄可以达到 Apache HTTP Server 的性能。

连接器内部结构：

![](./imgs/bj3.png)

组件介绍：

| 组件            | 作用描述                                                     |
| --------------- | ------------------------------------------------------------ |
| EndPoint        | EndPoint 是 Coyote 通信端点，即通信监听的接⼝，是具体Socket接收和发送处理器，是对传输层的抽象，因此EndPoint⽤来实现TCP/IP协议的 |
| Processor       | Processor 是Coyote 协议处理接⼝ ，如果说EndPoint是⽤来实现TCP/IP协议的，那么Processor⽤来实现HTTP协议，Processor接收来⾃EndPoint的Socket，读取字节流解析成Tomcat Request和Response对象，并通过Adapter将其提交到容器处理，Processor是对应⽤层协议的抽象 |
| ProtocolHandler | Coyote 协议接⼝， 通过Endpoint 和 Processor ， 实现针对具体协议的处理能⼒。Tomcat 按照协议和I/O 提供了6个实现类 ： AjpNioProtocol ，AjpAprProtocol， AjpNio2Protocol ， Http11NioProtocol ，Http11Nio2Protocol ，Http11AprProtocol |
| Adapter         | 由于协议不同，客户端发过来的请求信息也不尽相同，Tomcat定义了⾃⼰的Request类来封装这些请求信息。ProtocolHandler接⼝负责解析请求并⽣成Tomcat Request类。但是这个Request对象不是标准的ServletRequest，不能⽤Tomcat Request作为参数来调⽤容器。Tomcat设计者的解决⽅案是引⼊CoyoteAdapter，这是适配器模式的经典运⽤，连接器调⽤CoyoteAdapter的Sevice⽅法，传⼊的是Tomcat Request对象，CoyoteAdapter负责将Tomcat Request转成ServletRequest，再调⽤容器 |



#### 4.servlet容器（Catalina）

Tomcat是⼀个由⼀系列可配置（conf/server.xml）的组件构成的Web容器，⽽Catalina是Tomcat的

servlet容器。从另⼀个⻆度来说，Tomcat 本质上就是⼀款 Servlet 容器， 因为 Catalina 才是 Tomcat 的核⼼ ， 其

他模块都是为Catalina 提供⽀撑的。 ⽐如 ： 通过 Coyote 模块提供链接通信，Jasper 模块提供 JSP 引

擎，Naming 提供JNDI 服务，Juli 提供⽇志服务。

![](./imgs/bj4.png)



catalina内部结构：

![](./imgs/bj5.jpg)

其实，可以认为整个Tomcat就是⼀个Catalina实例，Tomcat 启动的时候会初始化这个实例，Catalina

实例通过加载server.xml完成其他实例的创建，创建并管理⼀个Server，Server创建并管理多个服务，

每个服务⼜可以有多个Connector和⼀个Container。

⼀个Catalina实例（容器）

⼀个 Server实例（容器）

多个Service实例（容器）

每⼀个Service实例下可以有多个Connector实例和⼀个Container实例

组件描述：

| 组件      | 描述                                                         |
| --------- | ------------------------------------------------------------ |
| catalina  | 负责解析Tomcat的配置⽂件（server.xml） , 以此来创建服务器Server组件并进⾏管理 |
| Server    | 服务器表示整个Catalina Servlet容器以及其它组件，负责组装并启动Servlaet引擎,Tomcat连接器。Server通过实现Lifecycle接⼝，提供了⼀种优雅的启动和关闭整个系统的⽅式 |
| Service   | 服务是Server内部的组件，⼀个Server包含多个Service。它将若⼲个Connector组件绑定到⼀个Container |
| Container | 容器，负责处理⽤户的servlet请求，并返回对象给web⽤户的模块   |



container容器内部结构：

![](./imgs/bj6.png)

组件描述：

| 组件    | 描述                                                         |
| ------- | ------------------------------------------------------------ |
| Engine  | 表示整个Catalina的Servlet引擎，⽤来管理多个虚拟站点，⼀个Service最多只能有⼀个Engine，但是⼀个引擎可包含多个Host |
| Host    | 代表⼀个虚拟主机，或者说⼀个站点，可以给Tomcat配置多个虚拟主机地址，⽽⼀个虚拟主机下可包含多个Context |
| Context | 表示⼀个Web应⽤程序， ⼀个Web应⽤可包含多个Wrapper           |
| Wrapper | 表示⼀个Servlet，Wrapper 作为容器中的最底层，不能包含⼦容器  |

上述组件的配置其实就体现在conf/server.xml中。



## 2.tomcat核心配置

server标签：

```xml
<!--Server 根元素，创建⼀个Server实例，⼦标签有 Listener、GlobalNamingResources、Service-->
<Server>
    <!--定义监听器-->
    <Listener/>
    <!--定义服务器的全局JNDI资源 -->
    <GlobalNamingResources/>
    <!--定义⼀个Service服务，⼀个Server标签可以有多个Service服务实例-->
    <Service/>
</Server>
```

Service标签：

```xml
<!--
该标签⽤于创建 Service 实例，默认使⽤ org.apache.catalina.core.StandardService。
默认情况下，Tomcat 仅指定了Service 的名称， 值为 "Catalina"。
Service ⼦标签为 ： Listener、Executor、Connector、Engine，
其中：
Listener ⽤于为Service添加⽣命周期监听器，
Executor ⽤于配置Service 共享线程池，
Connector ⽤于配置Service 包含的链接器，
Engine ⽤于配置Service中链接器对应的Servlet 容器引擎
-->
<Service name="Catalina">
...
</Service>
```

Executor标签：

```xml
<!--
Connector 标签
Connector 标签⽤于创建链接器实例
默认情况下，server.xml 配置了两个链接器，⼀个⽀持HTTP协议，⼀个⽀持AJP协议
⼤多数情况下，我们并不需要新增链接器配置，只是根据需要对已有链接器进⾏优化
默认情况下，Service 并未添加共享线程池配置。 如果我们想添加⼀个线程池， 可以在
<Service> 下添加如下配置：
name：线程池名称，⽤于 Connector中指定
namePrefix：所创建的每个线程的名称前缀，⼀个单独的线程名称为
namePrefix+threadNumber
maxThreads：池中最⼤线程数
minSpareThreads：活跃线程数，也就是核⼼池线程数，这些线程不会被销毁，会⼀直存在
maxIdleTime：线程空闲时间，超过该时间后，空闲线程会被销毁，默认值为6000（1分钟），单位
毫秒
maxQueueSize：在被执⾏前最⼤线程排队数⽬，默认为Int的最⼤值，也就是⼴义的⽆限。除⾮特
殊情况，这个值 不需要更改，否则会有请求不会被处理的情况发⽣
prestartminSpareThreads：启动线程池时是否启动 minSpareThreads部分线程。默认值为
false，即不启动
threadPriority：线程池中线程优先级，默认值为5，值从1到10
className：线程池实现类，未指定情况下，默认实现类为
org.apache.catalina.core.StandardThreadExecutor。如果想使⽤⾃定义线程池⾸先需要实现
org.apache.catalina.Executor接⼝
-->
<Executor name="commonThreadPool"
    namePrefix="thread-exec-"
    maxThreads="200"
    minSpareThreads="100"
    maxIdleTime="60000"
    maxQueueSize="Integer.MAX_VALUE"
    prestartminSpareThreads="false"
    threadPriority="5"
    className="org.apache.catalina.core.StandardThreadExecutor"/>
```

Connector标签：

Connector 标签⽤于创建链接器实例

默认情况下，server.xml 配置了两个链接器，⼀个⽀持HTTP协议，⼀个⽀持AJP协议

⼤多数情况下，我们并不需要新增链接器配置，只是根据需要对已有链接器进⾏优化

```xml
<!--
port：
端⼝号，Connector ⽤于创建服务端Socket 并进⾏监听， 以等待客户端请求链接。如果该属性设置
为0， Tomcat将会随机选择⼀个可⽤的端⼝号给当前Connector 使⽤
protocol：
当前Connector ⽀持的访问协议。 默认为 HTTP/1.1 ， 并采⽤⾃动切换机制选择⼀个基于 JAVA
NIO 的链接器或者基于本地APR的链接器（根据本地是否含有Tomcat的本地库判定）
connectionTimeOut:
Connector 接收链接后的等待超时时间， 单位为 毫秒。 -1 表示不超时。
redirectPort：
当前Connector 不⽀持SSL请求， 接收到了⼀个请求， 并且也符合security-constraint 约束，
需要SSL传输，Catalina⾃动将请求重定向到指定的端⼝。
executor：
指定共享线程池的名称， 也可以通过maxThreads、minSpareThreads 等属性配置内部线程池。
可以使⽤共享线程池
Engine 标签
Engine 表示 Servlet 引擎
Host 标签
Host 标签⽤于配置⼀个虚拟主机
URIEncoding:
⽤于指定编码URI的字符编码， Tomcat8.x版本默认的编码为 UTF-8 , Tomcat7.x版本默认为ISO-
8859-1
-->
<!--org.apache.coyote.http11.Http11NioProtocol ， ⾮阻塞式 Java NIO 链接器-->
<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000"
redirectPort="8443" />
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
```

也可以使⽤共享线程池

```xml
<Connector port="8080"
      protocol="HTTP/1.1"
      executor="commonThreadPool"
      maxThreads="1000"
      minSpareThreads="100"
      acceptCount="1000"
      maxConnections="1000"
      connectionTimeout="20000"
      compression="on"
      compressionMinSize="2048"
      disableUploadTimeout="true"
      redirectPort="8443"
      URIEncoding="UTF-8" />
```

Engine标签

Engine 表示 Servlet 引擎

```xml
<!--
name： ⽤于指定Engine 的名称， 默认为Catalina
defaultHost：默认使⽤的虚拟主机名称， 当客户端请求指向的主机⽆效时， 将交由默认的虚拟主机处
理， 默认为localhost
-->
<Engine name="Catalina" defaultHost="localhost">
...
</Engine>
```

Host标签

Host 标签⽤于配置⼀个虚拟主机

```xml
<Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
...
</Host>
```

Context标签

Context 标签⽤于配置⼀个Web应⽤，如下：

```xml
<Host name="www.abc.com" appBase="webapps" unpackWARs="true"
autoDeploy="true">
<!--
docBase：Web应⽤⽬录或者War包的部署路径。可以是绝对路径，也可以是相对于 Host appBase的
相对路径。
path：Web应⽤的Context 路径。如果我们Host名为localhost， 则该web应⽤访问的根路径为：
http://localhost:8080/web_demo。
-->
    <Context docBase="/Users/yingdian/web_demo" path="/web3"></Context>
    <Valve className="org.apache.catalina.valves.AccessLogValve"
          directory="logs"
          prefix="localhost_access_log" suffix=".txt"
          pattern="%h %l %u %t &quot;%r&quot; %s %b" />
</Host>
```



## 3.tomcat源码剖析

tomcat流程源码可以分成两步来分析：1.初始化过程      2.请求处理过程

#### 1.初始化过程解析

tomcat入口函数：BootStrap.main方法

![](./imgs/bj8.png)

load(args)方法执行流程：

![](./imgs/bj7.jpg)

start方法执行流程：

![](./imgs/bj10.jpg)

NioEndPoint的start方法：

![](./imgs/bj9.png)

可以看到这里会开启两种线程任务，一个是处理请求的处理（Puller）任务，一个则是监听（accept）任务

其中puller线程会轮询selector，默认是系统cpu处理器核心数（不小于2）

accept线程任务：

![](./imgs/bj11.png)

进一步查看accept线程任务：

![](./imgs/bj12.png)

请求处理：

puller线程会轮询selector，当有请求过来时，会讲selectionKey交给NioEndpoint.processKey方法处理

![](./imgs/bj13.jpg)



## 4.tomcat的类加载

#### 1.jvm类加载机制

![](./imgs/bj14.png)

另外：⽤户可以⾃定义类加载器（Java编写，⽤户⾃定义的类加载器，可加载指定路径的 class ⽂件）

　　当 JVM 运⾏过程中，⽤户⾃定义了类加载器去加载某些类时，会按照下⾯的步骤（⽗类委托机制）

　　1） ⽤户⾃⼰的类加载器，把加载请求传给⽗加载器，⽗加载器再传给其⽗加载器，⼀直到加载器

树的顶层

　　2 ）最顶层的类加载器⾸先针对其特定的位置加载，如果加载不到就转交给⼦类

　　3 ）如果⼀直到底层的类加载都没有加载到，那么就会抛出异常 ClassNotFoundException

　 因此，按照这个过程可以想到，如果同样在 classpath 指定的⽬录中和⾃⼰⼯作⽬录中存放相同的

class，会优先加载 classpath ⽬录中的⽂件

双亲委派机制:

当某个类加载器需要加载某个.class⽂件时，它⾸先把这个任务委托给他的上级类加载器，递归这个操

作，如果上级的类加载器没有加载，⾃⼰才会去加载这个类。

作⽤:

防⽌重复加载同⼀个.class。通过委托去向上⾯问⼀问，加载过了，就不⽤再加载⼀遍。保证数据

安全。保证核⼼.class不能被篡改。通过委托⽅式，不会去篡改核⼼.class，即使篡改也不会去加载，即使

加载也不会是同⼀个.class对象了。不同的加载器加载同⼀个.class也不是同⼀个.class对象。这样

保证了class执⾏安全（如果⼦类加载器先加载，那么我们可以写⼀些与java.lang包中基础类同名

的类， 然后再定义⼀个⼦类加载器，这样整个应⽤使⽤的基础类就都变成我们⾃⼰定义的类了。

）Object类 -----> ⾃定义类加载器（会出现问题的，那么真正的Object类就可能被篡改了）

#### 2.tomcat类加载

![](./imgs/bj15.png)

- 引导类加载器 和 扩展类加载器 的作⽤不变

- 系统类加载器正常情况下加载的是 CLASSPATH 下的类，但是 Tomcat 的启动脚本并未使⽤该变量，⽽是加载tomcat启动的类，⽐如bootstrap.jar，通常在catalina.bat或者catalina.sh中指定。位于CATALINA_HOME/bin下

- Common 通⽤类加载器加载Tomcat使⽤以及应⽤通⽤的⼀些类，位于CATALINA_HOME/lib下，⽐如servlet-api.jar

- Catalina ClassLoader ⽤于加载服务器内部可⻅类，这些类应⽤程序不能访问

- Shared ClassLoader ⽤于加载应⽤程序共享类，这些类服务器不会依赖

- Webapp ClassLoader，每个应⽤程序都会有⼀个独⼀⽆⼆的Webapp ClassLoader，他⽤来加载本应⽤程序 /WEB-INF/classes 和 /WEB-INF/lib 下的类。

>  tomcat 8.5 默认改变了严格的双亲委派机制⾸先从 Bootstrap Classloader加载指定的类
>
> 如果未加载到，则从 /WEB-INF/classes加载
>
> 如果未加载到，则从 /WEB-INF/lib/*.jar 加载
>
> 如果未加载到，则依次从 System、Common、Shared 加载（在这最后⼀步，遵从双亲委派
>
> 机制）

