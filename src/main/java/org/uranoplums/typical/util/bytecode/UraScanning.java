/*
 * Copyright 2013-2015 the Uranoplums Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * $Id: UraScanner.java$
 */
package org.uranoplums.typical.util.bytecode;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;

import org.uranoplums.typical.exception.UraSystemRuntimeException;
import org.uranoplums.typical.lang.UraSerialDataObject;

/**
 * UraScanningクラス。<br>
 *
 * @since 2015/08/12
 * @author syany
 */
public class UraScanning extends UraSerialDataObject implements UraScanner, UraAnalysis {
    /**  */
    private static final long serialVersionUID = -4322556069635774866L;
    /**  */
    protected UraScanner scanner = this;
    /**  */
    protected UraAnalysis analysis = this;

    /**
     * デフォルトコンストラクタ。<br>
     */
    public UraScanning() {
        super();
    }

    /**
     * コンストラクタ。<br>
     * @param scanner
     */
    public UraScanning(UraScanner scanner) {
        super();
        this.scanner = scanner;
    }

    /**
     * @param scanner scannerを設定します。
     */
    public final void setScanner(UraScanner scanner) {
        this.scanner = scanner;
    }

    /*
     * (非 Javadoc)
     *
     * @see org.uranoplums.typical.util.bytecode.UraScanner#scanEntry(javassist.bytecode.ClassFile)
     */
    @Override
    public void scanEntry(ClassFile classFile) throws BadBytecode {
        // 仮メソッド
        System.out.println(classFile.getName());
    }

    /* (非 Javadoc)
     * @see org.uranoplums.typical.util.bytecode.UraAnalysis#execute()
     */
    @Override
    public void execute() {}

    /**
     * 。<br>
     * @param file
     */
    public void analysisFiles(File file) {
        scanFiles(file);
        // 走査結果を解析する。
        analysis.execute();
    }

    /**
     * 。<br>
     * @param file
     */
    public void scanFiles(File file) {

        if (file.isDirectory()) {
            for (final File child : file.listFiles()) {
                scanFiles(child);
            }
        } else if (file.getName().endsWith("jar") || file.getName().endsWith("zip")) {
                try {
                    scanJar(new JarFile(file));
                } catch (IOException ex) {
                    throw new UraSystemRuntimeException(ex);
                }
        } else if (file.getName().endsWith("class")) {
            BufferedInputStream bif;
            try {
                bif = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                ClassFile classFile;
                classFile = new ClassFile(new DataInputStream(bif));
                scanner.scanEntry(classFile);
            } catch (FileNotFoundException ex) {
                throw new UraSystemRuntimeException(ex);
            } catch (BadBytecode ex) {
                throw new UraSystemRuntimeException(ex);
            } catch (IOException ex) {
                throw new UraSystemRuntimeException(ex);
            }
        }
    }

    /**
     * 。<br>
     * @param jarFile
     * @throws UraSystemRuntimeException
     */
    public void scanJar(JarFile jarFile) {
        Enumeration<JarEntry> entries = jarFile.entries();
        try {
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    DataInputStream in = new DataInputStream(new BufferedInputStream(jarFile.getInputStream(entry)));
                    try {
                        ClassFile classFile = new ClassFile(in);
                        scanner.scanEntry(classFile);
                    } finally {
                        in.close();
                    }
                }
            }
        } catch (BadBytecode ex) {
            throw new UraSystemRuntimeException(ex);
        } catch (IOException ex) {
            throw new UraSystemRuntimeException(ex);
        }
    }
}
