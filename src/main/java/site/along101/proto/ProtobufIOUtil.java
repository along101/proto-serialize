package site.along101.proto;

import java.io.OutputStream;
import java.util.Map;

/**
 * @author yinzuolong
 */
public class ProtobufIOUtil {

    public static <T> byte[] toByteArray(T message) {
        RuntimeSchema schema = RuntimeSchema.getRuntimeSchema(message.getClass());
        Map<String, Field> map = schema.getFieldMap();
        for (Field field : map.values()) {
            //List

            //Map
            switch (field.getType()) {
                case INT32:
                case UINT32:
                case SINT32:
                case FIXED32:
                case SFIXED32:
                case INT64:
                case UINT64:
                case SINT64:
                case FIXED64:
                case SFIXED64:
                case BOOL:
                case FLOAT:
                case DOUBLE:
                case STRING:
                case BYTES:
                case ENUM:
                case MESSAGE:
            }
        }
        return null;
    }

    public static <T> int writeTo(OutputStream os, T message) {
        return 0;
    }
}
