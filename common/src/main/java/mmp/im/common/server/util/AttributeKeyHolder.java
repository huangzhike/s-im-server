package mmp.im.common.server.util;

import io.netty.util.AttributeKey;

import java.util.List;

public class AttributeKeyHolder {

    public static AttributeKey<Long> CHANNEL_ID = AttributeKey.valueOf("CHANNEL_ID");

    // 收到的SEQ列表，用于消息去重
    public static AttributeKey<List<Long>> REV_SEQ_LIST = AttributeKey.valueOf("REV_SEQ_LIST");

}
