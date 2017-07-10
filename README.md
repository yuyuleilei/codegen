代码生成，及基本框架

本项目引用于：http://git.oschina.net/htengen/codgen

使用说明：
http://www.le.com/ptv/vplay/30333091.html 

http://www.le.com/ptv/vplay/30333143.html

功能：自定义生成器（codgen）

生成基础项目：xxx-api，xxx-domain，xxx-service，xxx-web(spring-boot)，xxx-bfun全程无代码，功能强大，web项目包含config，全局统一异常处理，日志切面，生成后只需要改动jdbc信息即可使用。

生成项目代码：domain，service，serviceImpl，mapper，mapper.xml包含单表基本操作，配合生成的xxx-bfun实现更强大的单表操作，例子科参照生成后的xxx-web中的test目录的TestApplication.java，其中有大部分的例子可供参考。