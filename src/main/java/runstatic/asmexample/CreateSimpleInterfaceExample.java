package runstatic.asmexample;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.*;

/**
 * @author chenmoand
 */
public class CreateSimpleInterfaceExample implements Opcodes {


    public interface SimpleInterface {

        void methodOne();

        String methodTwo();

        int methodThree(Object obj);

    }


    public static void main(String[] args) throws IOException {

        // 这里面给0即刻 ClassWriter.COMPUTE_MAXS和ClassWriter.COMPUTE_FRAMES暂时不需要用
        ClassWriter cw = new ClassWriter(0);

        // 构造
        cw.visit(
                V1_8,                                      // 生成的源码版本
                ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,  // 生成的作用域
                "runstatic/asmexample/SimpleInterface",  // 生成的Class名称
                null,                             // 泛型信息会传入此参数 现在不考虑泛型
                Type.getInternalName(Object.class),        // 这个是他的父类, 没有就给个Object.class就好
                new String[0]                              // 这个是实现的接口, 没有就不传
        );

        // methodOne
        cw.visitMethod(
                ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                "methodOne",
                Type.getMethodDescriptor(Type.VOID_TYPE),
                null,
                new String[0]
        );


        // methodTwo
        cw.visitMethod(
                ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                "methodTwo",
                Type.getMethodDescriptor(Type.getType(String.class)),
                null,
                new String[0]
        );


        // methodThree
        cw.visitMethod(
                ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
                "methodThree",
                Type.getMethodDescriptor(Type.INT_TYPE, Type.getType(Object.class)),
                null,
                new String[0]
        );


        // 访问结束
        cw.visitEnd();

        // 由于接口不能通过代码方式查看就通过文件方式打印
        final byte[] bytes = cw.toByteArray();
        File file = new File("./build/asmTarget/runstatic/asmexample");

        if (!file.isDirectory() && file.mkdirs()) {
            System.out.println("目录创建成功");
        }

        // 输出
        final OutputStream outputStream = new FileOutputStream("./build/asmTarget/runstatic/asmexample/SimpleInterface.class");
        outputStream.write(bytes);
    }

}
