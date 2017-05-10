# broccoli
简单的实现springmvc的功能  
1.通过动态代理实现注解方式的spring AOP,以及简单的IOC    
- 支持Jdk原生代理和Cglib代理  
- 支持多个代理的叠加效果  
- 通过spi实现代理的扩展  
- 目前实现了Before,After,Aroud 三种通知方式.

2.基于netty实现http连接,接受get,post方法  
- 实现controller requestMapping注解方式  

整体基本用法可参照示例,和spring的使用方式基本一致