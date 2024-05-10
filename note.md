FINISH:
* 实现client端向server端获取配置属性数据的逻辑
* 在变更配置值时记录版本更新，以便实现增量查询
* 长轮询模式

TODO:
* 思考一下怎么实现配置中心的历史版本
* 更新配置值的时候的并发问题的处理，要考虑一下
* 试一下事件视图的数据结构来处理配置数据的存储
* 实现注入到@Value注解修饰的字段
  1. 在BeanPostProcessor中扫表所有的Bean中使用了@Value的字段，保存其Metadata
  2. 实现对相关字段的数值的注入
  3. 基于config-server的version，实现监听服务端配置版本更新，在监听到变更事件时刷新所有@Value属性的字段值
  > 对@Value注解的处理，基本上与apollo的SpringValueProcessor差不多
* 监听到config-server数据变更的时候，刷新注册成Bean的配置对象的值
  * 可以往回翻翻之前rpc集成apollo的时候，讲的apollo怎么更新配置属性的内容，做下笔记
  * 用发布EnvironmentChangeEvent的形式来通知spring上下文从propertysource中刷新配置属性值（仅对使用ConfigurationProperties的配置生效，该event来自于spring-cloud-context库）



在Spring中注入配置属性的流程
1. 编写一个自定义PropertySource，该PropertySource负责从配置中心中获取配置属性
2. 在Spring启动时插入自定义的PropertySource
3. Spring在初始化Bean时，会调用PropertySource的getProperty方法，获取配置属性，此时会按照优先级从我们插入的PropertySource中获取配置属性


Spring的@Value注解实际上的作用是把注解参数中的值赋值给修饰的变量
```
@Value("my.name")
private String name; // name="my.name"
```
之所以@Value注解可以用来读取配置，起作用的其实是`${}`，spring会将使用`${}`包裹起来的字符串视作一个需要解析的属性值路径，
然后从Environment中获取此key对应的配置值来替换掉`${xxx}`这一部分，即`@Value("${my.name}")`等同于`@Value("zst")`

SpringValueProcessor，本质上是吧@Value中的属性key提取并记录，然后再监听到服务端配置变更时，根据变更的key来找到对应的placeholder，刷新对应的值