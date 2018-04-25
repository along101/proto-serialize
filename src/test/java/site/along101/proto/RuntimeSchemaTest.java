package site.along101.proto;

import org.junit.Test;

/**
 * @author yinzuolong
 */
public class RuntimeSchemaTest {

    @Test
    public void testGetRuntimeSchema() {
        RuntimeSchema<AllTypesPojos> schema = RuntimeSchema.getRuntimeSchema(AllTypesPojos.class);
        System.out.println(schema);
    }
}
