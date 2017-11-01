# MinaSocket
 这是一款基于Apache的Mina 2.0.16版本写的Socket长连接库,Mina官网可以查看[这里](http://mina.apache.org/)
 
 本库是对我在项目中使用的Mina和长连接的一个封装,亲测有效,在网络良好的情况下,几乎能够保证100%的连接和通讯;
 
 
###### 它有以下几点特点
* 使用广播监听当前网络情况,网络断开之后,广播通知服务,停止重新连接,这样在完全无网络情况下,服务不再重新连接Socket,可以节约资源;
* 每次重连的请求会尝试10次,当10次连接不成功,会重新释放所有资源,然后再次重新连接,保证所有资源都为最新(这部分是我在使用过程中,发现如果资源不释放,会出现一些奇怪的问题,比如连接成功发送一次就断开..);
* 默认使用了Mina的KeepAliveMessageFactory来实现心跳监测,使业务和心跳分开,使前后台不再需要区分心跳包和业务包;

###### 如何使用

1. 在procject的gradle下加入以下代码
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. 在app的gradle中加入依赖
```
	dependencies {
	        compile 'com.github.jianfeng318:SocketLibrary:Socket'
	}
```
3. Socket是基于Service的,所以在你需要启动socket的页面加入如下代码
```
   SocketConfig socketConfig = new SocketConfig.Builder(getApplicationContext())
                .setIp(HOST)//ip
                .setPort(PORT)//端口
                .setReadBufferSize(10240)//readBuffer
                .setIdleTimeOut(30)//客户端空闲时间,客户端在超过此时间内不向服务器发送数据,则视为idle状态,则进入心跳状态
                .setTimeOutCheckInterval(10)//客户端连接超时时间,超过此时间则视为连接超时
                .setRequestInterval(10)//请求超时间隔时间
                .setHeartbeatRequest("(1,1)\r\n")//与服务端约定的发送过去的心跳包
                .setHeartbeatResponse("(10,10)\r\n") //与服务端约定的接收到的心跳包
                .builder();
        ContentServiceHelper.bindService(this, socketConfig);
```
`SocketConfig ` 为配置项,包含了连接服务器的ip,端口,连接超时时间,客户端空闲超时时间,心跳包和心跳回复等,这些都是必须要配置的;然后绑定这个服务

4. 在你要接收服务器数据的页面,去实现`SocketResponseListener`这个接口,然后像这样
```
 SessionManager.getInstance().setReceivedResponseListener(this);
```

服务器返回的数据会回调到这里

**注意: 这里的数据,是订阅所有commond返回的所有数据,没有做任何解析,使用者需要在这里将数据解析之后,分发给真正需要显示的页面;**

```
  @Override
    public void socketMessageReceived(String msg) {
       // server response
    }
```

###### 存在问题
由于每个连接是基于IOSession的,我在初次封装时候,将`SessionManager`写成了单例,
所以一个service中调用`SessionManager.getInstance().setSeesion(mSession);`,
设置了session之后,如果再启动另一个service,会将之前的session覆盖,导致当前app中只能同时使用一个socket与服务端通讯,这个问题正在解决..
