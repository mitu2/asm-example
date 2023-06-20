package runstatic.asmexample;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author chenmoand
 */
public class SimpleClassLoader extends ClassLoader {

    private SimpleClassLoader() {
        super(ClassLoader.getSystemClassLoader());
    }

    private static final SimpleClassLoader instance = new SimpleClassLoader();

    public static SimpleClassLoader getInstance() {
        return instance;
    }


    public Class<?> forBytes(String name, byte[] bytes) {
        return super.defineClass(name, bytes, 0, bytes.length);
    }

    public Class<?> forBytes(String name, byte[] bytes, File file) throws Exception {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (FileOutputStream fs = new FileOutputStream(file)) {
            fs.write(bytes);
        }
        return forBytes(name, bytes);
    }

}
