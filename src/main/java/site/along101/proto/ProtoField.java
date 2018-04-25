package site.along101.proto;

import com.google.protobuf.WireFormat;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProtoField {

    int order();

    WireFormat.FieldType keyType() default WireFormat.FieldType.STRING;

    WireFormat.FieldType fieldType();

    /**
     * Specifying Field summary which comment end of field define like:
     * int32 count = 10; //count
     *
     * @return
     */
    String summary() default "";

}
