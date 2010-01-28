/*
 * Copyright (C) 2009-2010 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.asm;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.parboiled.support.Checks;

/**
 * Adds one constructor for each of the ParserClassNode.constructors,
 * which simply delegates to the respective super constructor.
 */
class ConstructorGenerator implements ClassTransformer, Opcodes {

    private final ClassTransformer nextTransformer;

    public ConstructorGenerator(ClassTransformer nextTransformer) {
        this.nextTransformer = nextTransformer;
    }

    public ParserClassNode transform(@NotNull ParserClassNode classNode) throws Exception {
        Checks.ensure(!classNode.constructors.isEmpty(),
                "Could not extend parser class '" + classNode.getParentType().getClassName() +
                        "', no constructor visible to derived classes found");

        for (MethodNode constructor : classNode.constructors) {
            createConstuctor(classNode, constructor);
        }

        return nextTransformer != null ? nextTransformer.transform(classNode) : classNode;
    }

    @SuppressWarnings({"unchecked"})
    private void createConstuctor(ParserClassNode classNode, MethodNode constructor) {
        MethodNode newConstructor =
                new MethodNode(ACC_PUBLIC, constructor.name, constructor.desc, constructor.signature,
                        (String[]) constructor.exceptions.toArray(new String[constructor.exceptions.size()]));

        Type[] argTypes = Type.getArgumentTypes(constructor.desc);
        for (int i = 0; i <= argTypes.length; i++) {
            newConstructor.instructions.add(new VarInsnNode(ALOAD, i));
        }
        newConstructor.instructions.add(new MethodInsnNode(INVOKESPECIAL, classNode.getParentType().getInternalName(),
                "<init>", constructor.desc));
        newConstructor.instructions.add(new InsnNode(RETURN));

        classNode.methods.add(newConstructor);
    }

}