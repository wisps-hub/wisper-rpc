package com.wisper.common.protocol.coder;

import com.wisper.common.message.Message;
import com.wisper.common.protocol.serialize.Serializer;
import com.wisper.common.utils.ZKUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        System.out.println(">>编码器开始编码: " + message);
        //1 写入消息类型 2字节 16bit
        out.writeShort(message.getMessageType());
        //2 写入序列化协议类型 2字节 16bit
        //Serializer.Algorithm serializeAlg = ZKUtils.getSerializeAlg();
        Serializer.Algorithm serializeAlg = Serializer.Algorithm.getAlgorithm();
        System.out.println("编码序列化算法: " + serializeAlg);
        out.writeShort(serializeAlg.ordinal());
        //序列化数据
        byte[] content = serializeAlg.serialize(message);
        //3 写入数据正文长度
        out.writeInt(content.length);
        //4 写入数据正文到输入缓冲区
        out.writeBytes(content);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        System.out.println(">>解码器开始解码: " + in);
        //1 读取消息类型
        short messageType = in.readShort();
        Class<?> messageClass = Message.getMessageClass(messageType);
        //2 读取序列化算法
        short serializeAlgc = in.readShort();
        Serializer.Algorithm serializeAlg = Serializer.Algorithm.getByOrdinal(serializeAlgc);
        //3 读取数据正文长度
        int contentLen = in.readInt();
        //4 读取数据正文
        byte[] content = new byte[contentLen];
        in.readBytes(content);
        //5 反序列化
        System.out.println("解码序列化算法: " + serializeAlg);
        Object messageObj = serializeAlg.deserializer(messageClass, content);
        System.out.println("解码成功: " + messageObj);
        list.add(messageObj);
    }
}
