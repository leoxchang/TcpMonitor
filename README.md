# TcpMonitor
基于reactor-netty实现的tcp/http代理工具，目的是解决tcp/http调式过程中的遇到的问题，打印请求报文和响应报文，便于排查问题。  
因为netty强大的处理能力、极快的响应时间，后期可以把它改造成可以用于反向代理的工具。具体的qps没有测试过，后期可以与nginx对比测试一下。  
目前支持tcp/http代理，http目前暂不支持https，后续添加。打印请求报文和响应报文。具体的使用方法如下：
启动参数
* -m 代理模式tcp或者http，必输
* -h 代理的ip，必输
* -p 代理的端口，必输
* -lh 监听的本地ip，默认127.0.0.1
* -lp 监听的本地端口，默认9999
* -c 报文解密的编码，默认UTF-8

启动示例  
java -jar TcpMonitor.jar -m http -h 127.0.0.1 -p 9201
