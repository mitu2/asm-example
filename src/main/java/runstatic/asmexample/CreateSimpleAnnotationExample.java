package runstatic.asmexample;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

/**
 * @author chenmoand
 */
public class CreateSimpleAnnotationExample implements Opcodes {


    @interface SimpleAnnotation {
        String[] value() default {"默认值1", "默认值2"};
    }


    public static void main(String[] args) throws Exception {
        // 这里面给0即刻 ClassWriter.COMPUTE_MAXS和ClassWriter.COMPUTE_FRAMES暂时不需要用
        ClassWriter cw = new ClassWriter(0);

        // 构造
        cw.visit(
                V1_8,                                             // 生成的源码版本
                ACC_PUBLIC | ACC_ABSTRACT | ACC_ANNOTATION | ACC_INTERFACE,     // 生成的作用域
                "runstatic/asmexample/SimpleAnnotation",   // 生成的Class名称
                null,                                   // 泛型信息会传入此参数 现在不考虑泛型
                "",                                   // 因为是Annotation, 相当于是一个接口, 所有没有父类
                new String[]{
                        Type.getDescriptor(Annotation.class)     // 这个是实现的接口, 所有注解都默认实现的这个类
                }
        );


        // 注解的参数方法
        final MethodVisitor value = cw.visitMethod(
                ACC_PUBLIC | ACC_ABSTRACT,
                "value",
                Type.getMethodDescriptor(Type.getType(String[].class)),
                null,
                new String[0]
        );

        value.visitCode();
        // 访问默认值
        final AnnotationVisitor aDefault = value.visitAnnotationDefault().visitArray(null);
        aDefault.visit(null, "默认值1");
        aDefault.visit(null, "默认值2");
        // 这行必须加👇, 要不然默认值不会写入
        aDefault.visitEnd();
        value.visitEnd();
        cw.visitEnd();

        // 由于注解不能通过代码方式查看就通过文件方式打印
        final byte[] bytes = cw.toByteArray();
        File file = new File("./build/asmTarget/runstatic/asmexample");

        if (!file.isDirectory() && file.mkdirs()) {
            System.out.println("目录创建成功");
        }

        // 输出
        final OutputStream outputStream = new FileOutputStream("./build/asmTarget/runstatic/asmexample/SimpleAnnotation.class");
        outputStream.write(bytes);

    }
}
