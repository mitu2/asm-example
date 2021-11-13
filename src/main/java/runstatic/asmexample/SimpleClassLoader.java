package runstatic.asmexample;

/**
 * @author chenmoand
 */
public class SimpleClassLoader extends ClassLoader {

    private SimpleClassLoader() {}

    private static final SimpleClassLoader instance = new SimpleClassLoader();

    public static SimpleClassLoader getInstance() {
        return instance;
    }


    public Class<?> forBytes(String name, byte[] bytes) {
        return super.defineClass(name, bytes, 0, bytes.length);
    }

}
