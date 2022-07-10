# fake.client

# 说明

类ETL产品可以连接 hadoop, redis, mongodb, 达梦, kingbase 等众多外部组件。测试工作中需要大量频繁的访问第三方组件操作, 却不要求每个组件有复杂的调用，为了提高测试效率，简化繁琐的服务器切换和命令的使用，于是我利用第三方组件提供的API，将简单读写操作封装成HTTP接口，于是产生了这个工具，名称也因此而来。 

后来, 为了进一步简化操作，尝试着将各种简单工具代码封装成接口, 如文件转换, hash和MD5计算等。

其他还包括了调用Python的第三方库, 完成文本摘要或绘制标签云功能。出于个人兴趣, 还将文本摘要的代码用Java重新实现了一遍, 也作为接口封装进了这个工具中。

今后也许会加入更多……这样的话……可以改名了(逃)

# 如何使用

将其作为一个maven工程引入, 编译并打包为jar文件即可。配置文件见 src/main/resources/application.yml。bin 目录下的 start.sh 和 shutdown.sh 是我提供的一个简单的启停命令。

# 启用Python

bin 目录下提供了 install-openssl.sh 和 install-python3.sh，允许在 linux 简单启用 python 的脚本。 如果服务器没有安装ssl, 需要先执行install-openssl.sh；在执行 install-python3.sh 前，需要自行准备 python3 的源码包并修改此脚本，指定源码包的位置，执行后python会自动安装到 fake.client/python 目录中
