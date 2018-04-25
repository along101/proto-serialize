package site.along101.proto;

import com.google.protobuf.WireFormat;
import lombok.Getter;

@Getter
public class Field {

    private final int order;
    private final WireFormat.FieldType type;
    /**
     * for map
     */
    private final WireFormat.FieldType keyType;
    private final Class<?> javaType;
    private final String name;

    public Field(int order, WireFormat.FieldType type, WireFormat.FieldType keyType, Class<?> javaType, String name) {
        this.order = order;
        this.type = type;
        this.keyType = keyType;
        this.javaType = javaType;
        this.name = name;
    }
}
