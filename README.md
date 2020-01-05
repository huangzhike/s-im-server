# s-im-server

IM服务器，本来是打算搞一个支持安卓和Web的IM项目，前端方面就从网易云信开源的DEMO修改，使用Vue加上Google的ProtoBuf和后端经过WebSocket通信，不过搞着搞着就不想弄了，额。

后端部分其实该有的功能都有了，不过因为前端没完成，也没怎么测试，所以其实也说不上完成吧。

这个项目其实是我开始第一份工作的时候就有想法了，那个时候经常泡在一个即时通讯论坛学习，后来换工作后有了时间，就开始自己动手了，然后有大半年时间忙着学别的东西，就把项目丢一边了，然后现在也不想捡起来，唉，为什么我老是挖坑然后烂尾呢。

---

这个项目使用Netty作为通信框架，Google的ProtoBuf进行序列化，消息队列使用的是RabbitMQ，数据库暂时使用的是Redis和MySQL。

简单说一下项目的结构吧：

* Auth是一个HTTP接口的模块，主要是提供登录注册这些功能，当然因为我不想分太细，把发送文件之类的接口也加上去了，总之HTTP相关的接口都在这里。

* Client是拿来模拟测试客户端的，不过一直没弄。

* Common存放一些不知道放哪的代码，协议定义和一些工具类都在这里。

* Database是消息持久化相关的，感觉独立出来有点多余。

* Gate是IM的核心部分，维护和客户端的长连接，转发消息。

* Logic是接受Gate通过MQ转发过来的消息，然后进行持久化等操作。


简单的流程是这样的：

客户端先通过HTTP接口向Auth发起登录请求，成功则返回一个JWT的Token和可用的Gate地址列表，客户端收到响应后根据Token向Gate发起长连接，Gate校验通过后连接则建立。

同时客户端也会携带Token，向HTTP发起请求，拉取最近会话列表（SessionList）、好友列表（FriendList）、群列表（GroupList）、用户信息（UserInfo）等，群成员列表（GroupMemberList）则会在需要的时候才懒加载。

发送消息时，客户端通过Gate转发到DistributeGate，简单的说就是一个中心服务器，所有的Gate都会连接到这里，这里保存有当前所有登录用户的信息，包括客户端连接到具体哪个Gate，所以会把消息路由到对应的Gate，再由该Gate转发到对应的客户端。

所以消息发送顺序可能为：客户端A -> GateA -> DistributeGate -> GateB -> 客户端B

消息的持久化则通过MQ解耦合，DistributeGate收到消息后会将消息通过MQ转发到Logic，然后再消费数据进行持久化。

目前我觉得有问题的地方主要有几个：

* DistributeGate明显是一个中心，如果它崩了，那整个系统就废了，我想到的解决方法大概是：

    * DistributeGate做一个集群，使用分布式存储，Raft之类的；
    
    * 也考虑过通过一个路由算法，根据消息的接收Id直接路由到对应的Gate，这样就不用维护状态了，不过这个肯定是不行的，因为当增加一个Gate后，已登录和后登录的用户会出现不一致；

* 目前所有的Gate都是直接通过TCP直连DistributeGate的，可靠性是通过应用层的消息确认重传保证的，当时也是脑抽，现在觉得应该直接用RPC比较好；

* 消息持久化这里，目前消息直接使用的是Redis的持久化，好友关系这些使用的才是MySQL，也就是分了两个部分，吭，Redis显然是不适合大量数据持久化的，所以考虑使用Hbase之类的替代，或者统一用MySQL，定时批量从MQ消费消息并持久化；

* 身份验证这里使用的是JWT的Token，包含了UserId，这样很方便客户端和Gate建立连接时验证，直接解码获取Id则可，不需要再查数据库，Gate完全和数据库分开了，只是单纯的维护连接和转发消息。但是JWT有一个问题，服务端签发Token后不能直接使Token无效，只能等待Token到期，当然也可以通过向客户端发送指令让客户端清空Token的间接方法，但是Token其实还是有效的。

---

主要的模块：

Gate：

直接和客户端连接的部分，根据和客户端的连接协议不同，可以有TCP的方式，WebSocket的方式，等等，因为在应用层有确认重传的功能，所以使用UDP协议，可靠性也是有保障的，即使是HTTP方式也是没问题的，就像网页微信那样，使用长轮询，通过版本号保证消息的顺序和可靠，当然没必要这么麻烦。


所有的Gate都会和DistributeGate建立连接，Gate可以随意增加，Gate收到消息后通过DistributeGate路由到对应的Gate执行下发。

所以这里消息都是要经过服务器中转的，而不是点对点直连，点对点实现太复杂了，要考虑内网穿透等等问题，但是中转就好多了，还可以很方便地实行监控审查，过滤拦截，或者收集数据分析，啧啧，P2P做得到吗？

Gate启动后会主动连接DistributeGate，并向DistributeGate发送消息注册自己，这里应该改为RPC请求的方式，当时写的时候钻了牛角尖，没考虑到这点，也没考虑到注册没完成时就收到客户端连接请求的情况，应该加上一个状态标识的，还有生命周期什么的，不过也不想改了。

DistributeGate维护了一个Gate信息的Map结构，使用的是Redis进行存储，收到Gate的注册请求后，保存GateId和对应的GateInfo，包含IP，对应的协议类型。

客户端发起连接时会带上Token发送ClientLogin消息，Gate在收到ClientLogin类型消息后会用UserId标记该Channel，校验完成，否则其它类型的消息一律拒绝，关闭Channel。

同时Gate会发送一个ClientStatusHandler的消息到DistributeGate，DistributeGate再更新该用户的状态，Redis存储，UserId和对应登录的GateId列表（一个账号多端登录）。

多端登录这里，应该改为用协议标识，目前是用GateId标识的，所以可能会存在一个账号在多个安卓端或Web端登录的情况。

另外DistributeGate收到Gate转发的用户登录消息后，除了通过MQ推送给Logic做对应的处理外，还可以查找该用户的在线好友，或者别的端，主动推送登录或者下线消息。

发送好友消息这里，Gate收到消息后会直接转发给DistributeGate，再由它路由到对应的Gate下发消息，这里要注意自己别的端的账号也要同步下发。

发送群消息也是类似，只是多了一个通过群Id查找群成员的步骤。

对于发给离线的用户的消息，会直接显示为已发送，当对应的用户上线后，在拉取历史消息的同时，每个会话会附带有一个消息Id，代表该会话中已接收的最新的消息的Id，因为消息Id是单调递增的，所以该会话内所有大于该Id的消息都是离线后收到的消息。

具体的方法就是，客户端收到消息后向服务器发送一个包含了最新消息Id和所属的会话的回执，服务端收到后会进行标记，已读未读消息也可以用相同的方式实现。

不过现在想起来，这种做法不太好，因为每条消息都是需要确认的，一条回执就是两条消息，非常不好，所以这个应该交给服务端DistributeGate完成，不需要客户端的消息。

正在输入，消息撤回的实现逻辑也很简单，发送文件和图片这些，直接使用HTTP接口上传即可，最后返回URL通过消息发送。

添加好友功能可以直接通过HTTP接口实现，黑名单功能另外在DistributeGate做一层拦截即可。

其实DistributeGate的消息处理应该改为类似Pipeline的职责链模式比较好，方便增加新的逻辑，啧，算了。


Logic：

主要是持久化消息，比如用户的登录下线记录，聊天消息，等等。

对于聊天消息，无论是好友私聊还是群聊，都会更新会话Session，代表聊天的两方，存储在Redis里，为了方便查找，好友消息会生成两个会话From-To和To-From，群聊只会生成From-Group。

已读未读之类的，也是保存了From-To-MsgId在Redis，当然群会话和普通会话也是分开存放的。

之所以使用Redis，只是因为它的数据结构方便保存和查找而已，虽然这个做法的确不太好。



---

消息重传：

虽然TCP提供了确认重传，但是不能保证应用层一定可靠，而且可能还要兼容其他协议，所以应用层的确认重传是有必要的。

每个消息都有全局唯一的MsgId，单调递增，这个Id不是由客户端生成的，只有在DistributeGate收到转发时，由一个序列号生成器生成，目前是由一个AtomicLong生成的，并且没有做重启后序列号状态的恢复。

也可以用Redis或者数据库来实现。

因为消息的MsgId不是由客户端生成的，所以发送时还需要一个客户端Id，称为序列号Seq（其实UUID也可以，不需要单调递增），每个客户端发送的消息的Seq唯一即可，同一条消息该客户端重发时Seq必须唯一，只需要在一个通道内唯一即可，不需要全局唯一。

~~发送消息后，以消息的Seq为Key，包装了时间戳，消息，以及Channel的MessageContext为Value，放入一个ConcurrentHashMap中。~~

~~收到消息的确认ACK回复后，根据Seq将消息移除。~~

~~后台会有一个线程遍历Map，对于超时的消息会重新发送，多次重发失败的消息则会被丢弃。~~

很ZZ的做法，其实只要客户端和Gate的消息使用确认重传的方式就好了，其它地方全部使用RPC即可，我脑子一定是有坑。

另外，可能由于各种原因，客户端会重发同一条消息，对于这种情况，每个Channel维护了一个List，存放已接收的消息的Seq，用于去重。

有些消息就不用重传了，比如心跳。

对于发送成功的解释，这里只要把消息发送给DistributeGate成功即可，不需要等待接受端的确认，因为这样最简单了。


---

协议设计

消息协议这里，使用的是二进制序列化，简单点也可以用JSON文本。

协议头定长，包含：4字节的魔数标记，1字节的命令号，2字节的消息体长度。

现在我觉得这个协议设计很ZZ，首先一个无用的标记就占了协议头的一大半，重要的内容却分配了一点点空间。

如果需要的话，还可以加上校验和加密之类的协议头，不过这里没必要。

解析好之后就是根据命令号从Map获取对应的消息处理器了，不过这里可以考虑用数组替代Map。

消息体基本都是ProtoBuf序列化，之所以不全部都用ProtoBuf，因为考虑到可能会有不使用ProtoBuf的情况，所以做了兼容。

另外ProtoBuf不支持自动识别，嵌套起来又麻烦，所以就这样了。

```protobuf
// 心跳
message Heartbeat {
}

//
message Acknowledge {
    int64 ack = 1;
}

// 好友消息
message FriendMessage {
    int64 seq = 1;
    int64 seqId = 2;
    string from = 3;
    string to = 4;
    string type = 5;
    string content = 6;
}

// 群消息
message GroupMessage {
    int64 seq = 1;
    int64 seqId = 2;
    string from = 3;
    string to = 4;
    string type = 5;
    string content = 6;
    // 仅用于转发
    repeated string broadcastIdList = 7;
}

//
message ClientLogin {
    int64 seq = 1;
    string userId = 2;
    string token = 3;
    string clientInfo = 4;
}

message ClientLogout {
    int64 seq = 1;
    string userId = 2;
    string token = 3;
    string clientInfo = 4;
}

// 在线状态
message ClientStatus {
    int64 seq = 1;
    string userId = 2;
    string serverId = 3;
    bool status = 4;
    string clientInfo = 5;
    // 仅用于转发
    repeated string broadcastIdList = 6;
}

// 已读
message ReadMessage {
    string from = 1;
    string to = 2;
    int64 seqId = 3;
    Type type = 4;
    enum Type {
        FRIEND = 0;
        GROUP = 1;
    }
}

// 正在输入
message Inputting {
    string from = 1;
    string to = 2;
}
```































