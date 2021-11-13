package runstatic.asmexample;

import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author chenmoand
 */
public class CreateSimpleEnum implements Opcodes {

    public enum Color {
        RED, GREEN
    }

    public static void main(String[] args) throws Exception {

        // 这里面给0即刻 ClassWriter.COMPUTE_MAXS和ClassWriter.COMPUTE_FRAMES暂时不需要用
        ClassWriter cw = new ClassWriter(0);

        // 构造
        cw.visit(
                V1_8,                                      // 生成的源码版本
                ACC_PUBLIC | ACC_ENUM,              // 生成的作用域
                "runstatic/asmexample/SimpleEnumClass",                        // 生成的Class名称
                "Ljava/lang/Enum<Lrunstatic/asmexample/SimpleEnumClass;>;", // 泛型信息
                Type.getInternalName(Enum.class),          // 这个是他的父类, 没有就给个Object.class就好
                new String[0]                              // 这个是实现的接口, 没有就给个空的String数组就好
        );

        // SimpleEnumClass.RED 代表常量
        cw.visitField(
                ACC_PUBLIC | ACC_STATIC | ACC_FINAL | ACC_ENUM,     // 作用域
                "RED",                                               // 常量名称
                "Lrunstatic/asmexample/SimpleEnumClass;",        // 常量类型
                null,                                             // 是否有泛型信息
                null                                                 // 默认值
        );

        // SimpleEnumClass.GREEN 代表常量
        cw.visitField(
                ACC_PUBLIC | ACC_STATIC | ACC_FINAL | ACC_ENUM,   // 作用域
                "GREEN",                                           // 常量名称
                "Lrunstatic/asmexample/SimpleEnumClass;",      // 常量类型
                null,                                           // 是否有泛型信息
                null                                               // 默认值
        );

        // $VALUES 就是存放所有枚举的一个数组
        cw.visitField(
                ACC_PRIVATE | ACC_STATIC | ACC_FINAL | ACC_SYNTHETIC,       // 作用域
                "$VALUES",                                                   // 常量名称
                "[Lrunstatic/asmexample/SimpleEnumClass;",               // 常量类型
                null,                                                     // 是否有泛型信息
                null                                                          // 默认值
        );

        // 静态代码块
        // 这里面是做SimpleEnumClass.RED和SimpleEnumClass.GREEN的初始化并存入到SimpleEnumClass.$VALUES数组里面
        {
            final MethodVisitor clinit = cw.visitMethod(
                    ACC_STATIC,                                // 众所周知静态代码块是静态的
                    "<clinit>",                            // 静态代码块<clinit> 而不是类名这里面需要注意
                    Type.getMethodDescriptor(Type.VOID_TYPE),  // 返回值但构造器没有返回值 ()V
                    null,                             // 泛型没有就给null
                    new String[0]                              // 抛出的异常列表
            );

            clinit.visitCode();

            clinit.visitTypeInsn(NEW, "runstatic/asmexample/SimpleEnumClass");
            clinit.visitInsn(DUP);
            clinit.visitLdcInsn("RED");
            clinit.visitInsn(ICONST_0);
            clinit.visitMethodInsn(
                    INVOKESPECIAL,
                    "runstatic/asmexample/SimpleEnumClass",
                    "<init>",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.INT_TYPE),
                    false
            );

            clinit.visitFieldInsn(
                    PUTSTATIC,
                    "runstatic/asmexample/SimpleEnumClass",
                    "RED",
                    "Lrunstatic/asmexample/SimpleEnumClass;"
            );
            clinit.visitTypeInsn(NEW, "runstatic/asmexample/SimpleEnumClass");
            clinit.visitInsn(DUP);

            clinit.visitLdcInsn("GREEN");
            clinit.visitInsn(ICONST_2);
            clinit.visitMethodInsn(
                    INVOKESPECIAL,
                    "runstatic/asmexample/SimpleEnumClass",
                    "<init>",
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.INT_TYPE),
                    false
            );
            clinit.visitFieldInsn(
                    PUTSTATIC,
                    "runstatic/asmexample/SimpleEnumClass",
                    "GREEN",
                    "Lrunstatic/asmexample/SimpleEnumClass;"
            );

            clinit.visitInsn(ICONST_2);
            clinit.visitTypeInsn(ANEWARRAY, "runstatic/asmexample/SimpleEnumClass");
            clinit.visitInsn(DUP);
            clinit.visitInsn(ICONST_0);
            clinit.visitFieldInsn(
                    GETSTATIC,
                    "runstatic/asmexample/SimpleEnumClass",
                    "RED",
                    "Lrunstatic/asmexample/SimpleEnumClass;"
            );
            clinit.visitInsn(AASTORE);
            clinit.visitInsn(DUP);
            clinit.visitInsn(ICONST_1);
            clinit.visitFieldInsn(
                    GETSTATIC,
                    "runstatic/asmexample/SimpleEnumClass",
                    "GREEN",
                    "Lrunstatic/asmexample/SimpleEnumClass;"
            );

            clinit.visitInsn(AASTORE);

            clinit.visitFieldInsn(
                    PUTSTATIC,
                    "runstatic/asmexample/SimpleEnumClass",
                    "$VALUES",
                    "[Lrunstatic/asmexample/SimpleEnumClass;"
            );

            clinit.visitInsn(RETURN);
            clinit.visitMaxs(4, 0);

            clinit.visitEnd();
        }

        // 构造器
        // 这里面是做 声明一个参数为String Int 的私有构造器
        // 如RED 相当于 new SimpleClassEnum("RED", 1);
        // 第一个参数代表名称第二个代表位置
        {
            // 创建一个基本的构造器
            // 注意: 构造器也是方法
            final MethodVisitor mv = cw.visitMethod(
                    ACC_PRIVATE,                                // 生成构造器的作用域
                    "<init>",                            // 构造器都叫<init> 而不是类名这里面需要注意
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.INT_TYPE),  // 返回值但构造器没有返回值 ()V
                    null,                             // 泛型没有就给null
                    new String[0]                              // 抛出的异常列表
            );

            // 访问开始
            mv.visitCode();

            final Label start = new Label();

            mv.visitLabel(start);

            // 拿出this变量
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(
                    INVOKESPECIAL,
                    Type.getInternalName(Enum.class),
                    "<init>",                                 // 方法名
                    Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.INT_TYPE),       // 描述 ()V
                    false                                  // 是不是接口
            );

            mv.visitInsn(RETURN);

            final Label end = new Label();

            mv.visitLabel(end);
            mv.visitLocalVariable("this", "Lrunstatic/asmexample/SimpleEnumClass;", null, start, end, 0);
            mv.visitMaxs(3, 3);

            // 访问结束
            mv.visitEnd();
        }

        // valueOf() 方法
        // 这里面做了是声明一个参数为String的静态方法
        // 内部调用的还是Enum.valueOf(Class, String)的方法
        // 如SimpleEnumClass.valueOf("RED") === Enum.valueOf(SimpleEnumClass.class, "RED")
        {
            final MethodVisitor valueOf = cw.visitMethod(
                    ACC_PUBLIC | ACC_STATIC,
                    "valueOf",
                    "(Ljava/lang/String;)Lrunstatic/asmexample/SimpleEnumClass;",
                    null,
                    new String[0]

            );

            valueOf.visitCode();

            valueOf.visitLdcInsn("Lrunstatic/asmexample/SimpleEnumClass;.class");

            valueOf.visitVarInsn(ALOAD, 0);
            valueOf.visitMethodInsn(
                    INVOKESTATIC,
                    Type.getInternalName(Enum.class),
                    "valueOf",
                    Type.getMethodDescriptor(Type.getType(Enum.class), Type.getType(Object.class), Type.getType(String.class)),
                    false
            );

            valueOf.visitTypeInsn(CHECKCAST, "runstatic/asmexample/SimpleEnumClass");
            valueOf.visitInsn(ARETURN);

            valueOf.visitLocalVariable(
                    "name",
                    Type.getDescriptor(String.class),
                    null,
                    new Label(),
                    new Label(),
                    0
            );

            valueOf.visitMaxs(2, 1);
            valueOf.visitEnd();

        }

        // values() 方法
        // 这里面做的相当于返回了SimpleEnumClass.$VALUES这个常量
        {
            final MethodVisitor values = cw.visitMethod(
                    ACC_PUBLIC | ACC_STATIC,
                    "values",
                    "()[Lrunstatic/asmexample/SimpleEnumClass;",
                    null,
                    new String[0]
            );

            values.visitCode();

            values.visitFieldInsn(
                    GETSTATIC,
                    "runstatic/asmexample/SimpleEnumClass",
                    "$VALUES",
                    "[Lrunstatic/asmexample/SimpleEnumClass;"
            );

            values.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "[Lrunstatic/asmexample/SimpleEnumClass;",
                    "clone",
                    Type.getMethodDescriptor(Type.getType(Object.class)),
                    false

            );

            values.visitTypeInsn(CHECKCAST, "[Lrunstatic/asmexample/SimpleEnumClass;");
            values.visitInsn(ARETURN);

            values.visitMaxs(1, 0);


            values.visitEnd();
        }

        // 类访问结束 (本方法写与不写没什么区别)
        cw.visitEnd();

        // 拿出构建的字节码
        final byte[] bytes = cw.toByteArray();

//         final OutputStream outputStream = new FileOutputStream(new File("./SimpleEnumClass.class"));
//         outputStream.write(bytes);

        final Class aClass = SimpleClassLoader.getInstance().forBytes("runstatic.asmexample.SimpleEnumClass", bytes);
        final Enum red = Enum.valueOf(aClass, "RED");
        final Enum green = Enum.valueOf(aClass, "GREEN");
        System.out.println(red);
        System.out.println(green);
        final Method values = aClass.getDeclaredMethod("values");
        System.out.println(Arrays.asList((Enum[]) values.invoke(null)));

    }


}

