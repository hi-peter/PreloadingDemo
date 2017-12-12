package com.meitu.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

/**
 * 字节码类处理器
 *
 * @author Ljq
 * @date 2017/12/12
 */

public class BytecodeFixTransform extends Transform {

    private static final String DEFAULT_NAME = "BytecodeFixTransform"

    private com.meitu.plugin.extension.BytecodeFixExtension mExtension;

    BytecodeFixTransform(Project project, String versionName, com.meitu.plugin.extension.BytecodeFixExtension extension) {
        this.mExtension = extension
        com.meitu.plugin.utils.Logger.enable = extension.logEnable
        com.meitu.plugin.injector.BytecodeFixInjector.init(project, versionName, mExtension)
    }

    // Transfrom在Task列表中的名字
    @Override
    public String getName() {
        return DEFAULT_NAME
    }

    // 指定input的类型
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transfrom的作用范围
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    public boolean isIncremental() {
        return false
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        if (null == transformInvocation) {
            throw new IllegalArgumentException("transformInvocation is null !!!")
        }
        // inputs就是输入文件的集合
        Collection<TransformInput> inputs = transformInvocation.inputs
        if (null == inputs) {
            throw new IllegalArgumentException("TransformInput is null !!!")
        }

        TransformOutputProvider outputProvider = transformInvocation.outputProvider;

        if (null == outputProvider) {
            throw new IllegalArgumentException("TransformInput is null !!!")
        }

        for (TransformInput input : inputs) {

            if (null == input) continue;

            // Transfrom的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
            //目录
            for (DirectoryInput directoryInput : input.directoryInputs) {

                if (directoryInput) {

                    if (null != directoryInput.file && directoryInput.file.exists()) {

                        //TODO 这里可以对input的文件做处理，比如代码注入！

                        File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                        FileUtils.copyDirectory(directoryInput.file, dest);
                    }
                }
            }

            //jar文件
            for (JarInput jarInput : input.jarInputs) {
                if (jarInput) {
                    if (jarInput.file && jarInput.file.exists()) {
                        String jarName = jarInput.name;
                        String md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)

                        if (jarName.endsWith(".jar")) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }

                        // 在这里jar文件进行动态修复
                        File injectedJarFile = null
                        if (null != mExtension && mExtension.enable) {
                            injectedJarFile = com.meitu.plugin.injector.BytecodeFixInjector.injector.inject(jarInput.file)
                        }

                        File dest = outputProvider.getContentLocation(DigestUtils.md5Hex(jarName + md5Name), jarInput.contentTypes, jarInput.scopes, Format.JAR);

                        if (dest) {
                            if (dest.parentFile) {
                                if (!dest.parentFile.exists()) {
                                    dest.parentFile.mkdirs()
                                }
                            }

                            if (!dest.exists()) {
                                dest.createNewFile();
                            }

                            if (null != injectedJarFile && injectedJarFile.exists()) {
                                FileUtils.copyFile(injectedJarFile, dest) // 将input的目录复制到output指定目录，这里传注入代码后压缩的jar
                                com.meitu.plugin.utils.Logger.e(jarInput.file.name + " has successful hooked !!!")
                                if (null != mExtension && !mExtension.keepFixedJarFile) {
                                    injectedJarFile.delete()
                                }
                            } else {
                                FileUtils.copyFile(jarInput.file, dest)
                            }
                        }
                    }
                }
            }
        }
    }
}
