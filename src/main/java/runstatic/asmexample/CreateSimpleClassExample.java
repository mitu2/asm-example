package runstatic.asmexample;

import org.objectweb.asm.*;

import java.io.PrintStream;

/**
 * @author chenmoand
 */
public class CreateSimpleClassExample implements Opcodes {

    public CreateSimpleClassExample() {
        System.out.println("Hello word!");
    }

    public static void main(String[] args) throws Exception {
        // 这里面给0即刻 ClassWriter.COMPUTE_MAXS和ClassWriter.COMPUTE_FRAMES暂时不需要用
        ClassWriter cw = new ClassWriter(0);

        // 构造
        cw.visit(
                V1_8,                                      // 生成的源码版本
                ACC_PUBLIC,                                // 生成的作用域
                "runstatic/asmexample/SimpleClass",  // 生成的Class名称
                null,                             // 泛型信息会传入此参数 现在不考虑泛型
                Type.getInternalName(Object.class),        // 这个是他的父类, 没有就给个Object.class就好
                new String[0]                              // 这个是实现的接口, 没有就不传
        );


        // 创建一个基本的构造器
        // 注意: 构造器也是方法
        final MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,                                // 生成构造器的作用域
                "<init>",                            // 构造器都叫<init> 而不是类名这里面需要注意
                Type.getMethodDescriptor(Type.VOID_TYPE),  // 返回值但构造器没有返回值 ()V
                null,                             // 泛型没有就给null
                new String[0]                              // 抛出的异常列表
        );

        // 访问开始
        mv.visitCode();

        final Label start = new Label();
        mv.visitLabel(start);
        // 拿出this变量
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(Object.class),
                "<init>",                                 // 方法名
                Type.getMethodDescriptor(Type.VOID_TYPE),       // 描述 ()V
                false                                  // 是不是接口
        );


        // GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
        mv.visitFieldInsn(
                GETSTATIC,
                Type.getInternalName(System.class),
                "out",
                Type.getDescriptor(PrintStream.class)

        );

        // LDC "Hello word!"
        mv.visitLdcInsn("hello word!");

        // INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(PrintStream.class),
                "println",
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)),
                false
        );

        mv.visitInsn(RETURN);

        final Label end = new Label();

        mv.visitLabel(end);
        mv.visitLocalVariable("this", "Lrunstatic/asmexample/SimpleClass;", null, start, end, 0);
        mv.visitMaxs(2, 1);

        // 访问结束
        mv.visitEnd();

        final byte[] bytes = cw.toByteArray();

        // final OutputStream outputStream = new FileOutputStream(new File("./SimpleClass.class"));
        // outputStream.write(bytes);

        final Class<?> aClass = SimpleClassLoader.getInstance().forBytes("runstatic.asmexample.SimpleClass", bytes);

        final Object instance = aClass.getDeclaredConstructor().newInstance();
        System.out.println(instance);


    }


}
