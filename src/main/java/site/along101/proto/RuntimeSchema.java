package site.along101.proto;

import com.google.protobuf.WireFormat;
import lombok.Getter;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yinzuolong
 */
public class RuntimeSchema<T> {

    private final static Map<String, RuntimeSchema> cache = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, site.along101.proto.Field> fieldMap = new HashMap<>();

    public static <T> RuntimeSchema<T> getRuntimeSchema(Class<T> typeClass) {
        RuntimeSchema schema = cache.get(typeClass.getName());
        if (schema == null) {
            synchronized (cache) {
                schema = cache.get(typeClass.getName());
                if (schema == null) {
                    schema = createRuntimeSchema(typeClass);
                    cache.put(typeClass.getName(), schema);
                }
            }
        }
        return schema;
    }

    private static <T> RuntimeSchema<T> createRuntimeSchema(Class<T> typeClass) {
        RuntimeSchema<T> schema = new RuntimeSchema<>();
        final Map<String, java.lang.reflect.Field> fieldMap = findInstanceFields(typeClass);
        for (java.lang.reflect.Field f : fieldMap.values()) {
            if (f.getAnnotation(Deprecated.class) != null) {
                continue;
            }
            ProtoField protoField = f.getAnnotation(ProtoField.class);
            if (protoField == null) {
                String className = typeClass.getCanonicalName();
                String fieldName = f.getName();
                String message = String.format("%s#%s is not annotated with @ProtoField", className, fieldName);
                throw new RuntimeException(message);
            }
            site.along101.proto.Field field = new site.along101.proto.Field(protoField.order(), protoField.fieldType(), protoField.keyType(), f.getType(), f.getName());
            schema.fieldMap.put(f.getName(), field);

            //List
            if (Collection.class.isAssignableFrom(field.getJavaType()) && field.getType() == WireFormat.FieldType.MESSAGE) {
                Class<?> type = getGenericType(f, 0);
                assert type != null;
                getRuntimeSchema(type);
                continue;
            }
            //Map
            if (Map.class.isAssignableFrom(field.getJavaType())) {
                if (field.getKeyType() == WireFormat.FieldType.MESSAGE) {
                    Class<?> type = getGenericType(f, 0);
                    assert type != null;
                    getRuntimeSchema(type);
                    continue;
                }
                if (field.getType() == WireFormat.FieldType.MESSAGE) {
                    Class<?> type = getGenericType(f, 1);
                    assert type != null;
                    getRuntimeSchema(type);
                    continue;
                }
            }
            //Message
            if (field.getType() == WireFormat.FieldType.MESSAGE) {
                getRuntimeSchema(field.getJavaType());
            }
        }
        return schema;
    }

    private static Map<String, java.lang.reflect.Field> findInstanceFields(
            Class<?> typeClass) {
        LinkedHashMap<String, java.lang.reflect.Field> fieldMap = new LinkedHashMap<>();
        fill(fieldMap, typeClass);
        return fieldMap;
    }

    private static void fill(Map<String, java.lang.reflect.Field> fieldMap, Class<?> typeClass) {
        if (Object.class != typeClass.getSuperclass()) {
            fill(fieldMap, typeClass.getSuperclass());
        }

        for (java.lang.reflect.Field f : typeClass.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod)) {
                fieldMap.put(f.getName(), f);
            }
        }
    }


    static Class<?> getGenericType(java.lang.reflect.Field f, int index) {
        try {
            Type type = ((ParameterizedType) f.getGenericType())
                    .getActualTypeArguments()[index];
            if (type instanceof GenericArrayType) {
                int dimensions = 1;
                Type componentType = ((GenericArrayType) type)
                        .getGenericComponentType();
                while (componentType instanceof GenericArrayType) {
                    dimensions++;
                    componentType = ((GenericArrayType) componentType)
                            .getGenericComponentType();
                }

                if (dimensions == 1) {
                    return Array.newInstance((Class<?>) componentType, 0)
                            .getClass();
                }

                final int[] arg = new int[dimensions];
                arg[0] = 0;
                return Array.newInstance((Class<?>) componentType, arg)
                        .getClass();
            }

            if (type instanceof ParameterizedType) {
                Object rawType = ((ParameterizedType) type).getRawType();
                if (Class.class == rawType)
                    return Class.class;

                if (Enum.class == rawType)
                    return Enum.class;

                return null;
            }

            return (Class<?>) type;
        } catch (Exception e) {
            return null;
        }
    }
}
