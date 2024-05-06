FINISH:
* 实现client端向server端获取配置属性数据的逻辑
* 在变更配置值时记录版本更新，以便实现增量查询

TODO:
* 思考一下怎么实现配置中心的历史版本
* 添加长轮询模式
* 更新配置值的时候的并发问题的处理，要考虑一下
* 试一下事件视图的数据结构来处理配置数据的存储
* 
在Spring中注入配置属性的流程
1. 编写一个自定义PropertySource，该PropertySource负责从配置中心中获取配置属性
2. 在Spring启动时插入自定义的PropertySource
3. Spring在初始化Bean时，会调用PropertySource的getProperty方法，获取配置属性，此时会按照优先级从我们插入的PropertySource中获取配置属性

