package com.wisper.common.protocol.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.esotericsoftware.kryo.Kryo;
import com.google.gson.*;
import com.wisper.common.config.ProtocolConfig;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public interface Serializer {

    Kryo kryo = new Kryo();

    //反序列化
    <T> T deserializer(Class<T> clazz, byte[] bytes);

    //序列化
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer {

        JAVA {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException("[JAVA]反序列化失败");
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("[JAVA]序列化失败");
                }
            }
        }, JSON {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }, HESSIAN {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    HessianInput hessianInput = new HessianInput(bis);
                    return (T) hessianInput.readObject();
                } catch (Exception e) {
                    throw new RuntimeException("[HESSIAN]反序列化失败");
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    HessianOutput hessianOutput = new HessianOutput(bos);
                    hessianOutput.writeObject(object);
                    return bos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("[HESSIAN]序列化失败");
                }
            }
        },
//        KRYO {
//            @Override
//            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
//                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); Input input = new Input(bis)) {
//                    return kryo.readObject(input, clazz);
//                } catch (Exception e) {
//                    throw new RuntimeException(String.format("[KRYO]反序列化失败, %s", e.getCause().getMessage()));
//                }
//            }
//
//            @Override
//            public <T> byte[] serialize(T object) {
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                Output output = new Output(bos);
//                kryo.writeObject(output, object);
//                return output.toBytes();
//            }
//        }, PROTOSTUFF {
//            @Override
//            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
//                Schema schema = RuntimeSchema.getSchema(clazz);
//                try {
//                    T obj = clazz.getDeclaredConstructor().newInstance();
//                    ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
//                    return obj;
//                } catch (Exception e) {
//                    throw new RuntimeException("[PROTOSTUFF]反序列化失败");
//                }
//            }
//
//            @Override
//            public <T> byte[] serialize(T object) {
//                // 获取对象的 schema
//                Schema schema = RuntimeSchema.getSchema(object.getClass());
//                // 使用 LinkedBuffer 来创建缓冲区（默认大小 1024）
//                // 也可自行指定 LinkedBuffer.DEFAULT_BUFFER_SIZE
//                LinkedBuffer buffer = LinkedBuffer.allocate();
//                // 序列化对象为字节数组
//                byte[] bytes;
//                try {
//                    bytes = ProtostuffIOUtil.toByteArray(object, schema, buffer);
//                } finally {
//                    buffer.clear();
//                }
//                return bytes;
//            }
//        }
        ;

        public static Algorithm getByOrdinal(int ordinal) {
            Algorithm[] values = Algorithm.values();
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
            return null;
        }

        public static Algorithm getAlgorithm(String name) {
            if (StringUtils.isNotBlank(name)) {
                for (Algorithm algorithm : Algorithm.values()) {
                    if (algorithm.name().equals(name)) {
                        return algorithm;
                    }
                }
            }
            return Algorithm.HESSIAN;
        }

        public static Algorithm getAlgorithm() {
            return getAlgorithm(ProtocolConfig.serializer);
        }
    }

    Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //json -> class
            try {
                final String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            //class -> json
            return new JsonPrimitive(src.getName());
        }
    }

}
