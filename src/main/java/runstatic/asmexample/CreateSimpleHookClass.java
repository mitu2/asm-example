package runstatic.asmexample;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author chenmoand
 */
public class CreateSimpleHookClass {

    public int sum(int a, int b) {
        if (a < 0 || b < 0) {
            return 0;
        }
        return a + b;
    }

    private static final String SUM_DESC = Type.getMethodDescriptor(Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE);

    public static void main(String[] args) throws Exception {
        String className = "runstatic.asmexample.SourceClass";
        ClassReader cr = new ClassReader(className);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);


        for (MethodNode mn : cn.methods) {
            if (Objects.equals(mn.name, "sum") && Objects.equals(mn.desc, SUM_DESC)) {
                InsnList insnList = new InsnList();
                LabelNode l1 = new LabelNode(new Label());
                insnList.add(new VarInsnNode(ILOAD, 1));
                insnList.add(new JumpInsnNode(IFLT, l1));
                LabelNode l2 = new LabelNode(new Label());
                insnList.add(new VarInsnNode(ILOAD, 2));
                insnList.add(new JumpInsnNode(IFLT, l2));
                insnList.add(new FrameNode(F_SAME, 0, null, 0, null));
                insnList.add(new InsnNode(ICONST_1));
                insnList.add(new InsnNode(IRETURN));
                InsnList instructions = mn.instructions;
                instructions.add(insnList);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);

        Class<?> aClass = SimpleClassLoader.getInstance().forBytes(className, cw.toByteArray(), new File("./out/production/classes/runstatic/asmexample/SourceClass.class"));

//        SourceClass o = (SourceClass) aClass.getDeclaredConstructor().newInstance();

        Object o = aClass.getDeclaredConstructor().newInstance();

        Method sum = aClass.getDeclaredMethod("sum", int.class, int.class);
        System.out.println(sum.invoke(o, -1, 4));

    }

}
