import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

/**
 * @author chenmoand
 */
public class TypeTest {

    @Test
    public void doMethodTest() {
        System.out.println(Type.getMethodDescriptor(Type.VOID_TYPE));

    }

}
