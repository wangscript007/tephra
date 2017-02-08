# Carousel访问支持

carousel模块提供访问[Carousel](https://github.com/heisedebaise/carousel)服务的封装。

## 发布微服务

使用carousel将服务注册到Carousel服务总线，可以通过以下几种方式：
- 使用[CarouselHelper](doc/helper.md)的register接口，将服务注册到Carousel总线；
- 实现[CarouselRegister](doc/register.md)接口，在系统启动时自动将指定服务注册到Carousel总线；
- 修改[配置参数](src/main/resources/carousel.tephra.config)${tephra.carousel.service.all}为true，则系统启动时可自动将所有服务全部注册到Carousel总线。

## 调用微服务

通过[CarouselHelper](doc/helper.md)的service接口，可以直接调用注册到Carousel总线的服务；如果总线中存在多个相同的服务则随即选择一个执行；如果本地有存在相应的服务，则优先调用本地服务以加快处理性能。

[CarouselHelper](doc/helper.md)

[CarouselRegister](doc/register.md)

[配置参数](src/main/resources/carousel.tephra.config)