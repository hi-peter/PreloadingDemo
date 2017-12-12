package com.meitu.plugin.injector

import com.meitu.plugin.utils.Logger
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

import java.util.jar.JarFile
import java.util.zip.ZipFile

/**
 * 字节码注入器
 * @author Ljq
 * @date 2017/12/12
 */

public class BytecodeFixInjector {

    private static final String INJECTOR = "injector" //classes及jar存放目录
    private static final String JAVA = ".java"
    private static final String CLASS = ".class"
    private static final String JAR = ".jar"

    private static ClassPool sClassPool
    private static BytecodeFixInjector sInjector

    private Project mProject
    private String mVersionName
    private com.meitu.plugin.extension.BytecodeFixExtension mExtension

    private boolean isDependenciesAdded

    private BytecodeFixInjector(Project project, String versionName, com.meitu.plugin.extension.BytecodeFixExtension extension) {
        this.mProject = project
        this.mVersionName = versionName
        this.mExtension = extension
        appendDefaultClassPath()
    }

    public
    static void init(Project project, String versionName, com.meitu.plugin.extension.BytecodeFixExtension extension) {
        sClassPool = ClassPool.default
        sInjector = new BytecodeFixInjector(project, versionName, extension)
    }

    public static BytecodeFixInjector getInjector() {
        if (null == sInjector) {
            throw new IllegalAccessException("init() hasn't been called !!!")
        }
        return sInjector
    }

    public synchronized File inject(File jar) {
        File destFile = null

        if (null == mExtension) {
            com.meitu.plugin.utils.Logger.e("can't find bytecodeFixConfig in your app build.gradle !!!")
            return destFile
        }

        if (null == jar) {
            com.meitu.plugin.utils.Logger.e("jar File is null before injecting !!!")
            return destFile
        }

        if (!jar.exists()) {
            com.meitu.plugin.utils.Logger.e(jar.name + " not exits !!!")
            return destFile
        }

        try {
            ZipFile zipFile = new ZipFile(jar)
            zipFile.close()
            zipFile = null
        } catch (Exception e) {
            com.meitu.plugin.utils.Logger.e(jar.name + " not a valid jar file !!!")
            return destFile
        }

        appendClassPathIfNecessary()

        def jarName = jar.name.substring(0, jar.name.length() - JAR.length())
        //压缩后的文件及重新打包的jar地址，在主工程injector目录
        def baseDir = new StringBuilder().append(mProject.projectDir.absolutePath)
                .append(File.separator).append(INJECTOR)
                .append(File.separator).append(mVersionName)
                .append(File.separator).append(jarName).toString()

        File rootFile = new File(baseDir)
        com.meitu.plugin.utils.FileUtils.clearFile(rootFile)
        rootFile.mkdirs()

        File unzipDir = new File(rootFile, "classes") //classes文件
        File jarDir = new File(rootFile, "jar")//jar文件

        JarFile jarFile = new JarFile(jar)
        mExtension.fixConfig.each { config ->
            if (!com.meitu.plugin.utils.TextUtil.isEmpty(config.trim())) {
                def configs = config.trim().split("##")
                if (null != configs && configs.length > 0) {
                    if (configs.length < 3) {
                        throw new IllegalArgumentException("参数配置有问题")
                    }

                    def className = configs[0].trim()
                    def methodName = configs[1].trim()
                    def injectValue = configs[2].trim()
                    def injectLine = 0
                    if (4 == configs.length) {
                        try {
                            injectLine = Integer.parseInt(configs[3])
                        } catch (Exception e) {
                            throw new IllegalArgumentException("行数配置有问题")
                        }
                    }

                    if (com.meitu.plugin.utils.TextUtil.isEmpty(className)) {
                        com.meitu.plugin.utils.Logger.e("className invalid !!!")
                        return
                    }

                    if (com.meitu.plugin.utils.TextUtil.isEmpty(methodName)) {
                        com.meitu.plugin.utils.Logger.e("methodName invalid !!!")
                        return
                    }

                    if (com.meitu.plugin.utils.TextUtil.isEmpty(injectValue)) {
                        com.meitu.plugin.utils.Logger.e("inject value invalid !!!")
                        return
                    }

                    def methodParams = new ArrayList<String>() //方法参数

                    if (methodName.contains("(") && methodName.contains(")")) {
                        def tempMethodName = methodName
                        methodName = tempMethodName.substring(0, tempMethodName.indexOf("(")).trim()
                        def params = tempMethodName.substring(tempMethodName.indexOf("(") + 1, tempMethodName.indexOf(")")).trim()
                        if (!com.meitu.plugin.utils.TextUtil.isEmpty(params)) {
                            if (params.contains(",")) {
                                params = params.split(",")
                                if (null != params && params.length > 0) {
                                    params.each { p ->
                                        methodParams.add(p.trim())
                                    }
                                }
                            } else {
                                methodParams.add(params)
                            }
                        }
                    }

                    if (className.endsWith(JAVA)) {
                        className = className.substring(0, className.length() - JAVA.length()) + CLASS
                    }

                    if (!className.endsWith(CLASS)) {
                        className += CLASS
                    }

                    def contain = com.meitu.plugin.utils.FileUtils.containsClass(jarFile, className)
                    if (contain) {
                        // 1、判断是否进行过解压缩操作
                        if (!com.meitu.plugin.utils.FileUtils.hasFiles(unzipDir)) {
                            com.meitu.plugin.utils.FileUtils.unzipJarFile(jarFile, unzipDir)
                        }

                        // 2、开始注入文件，需要注意的是，appendClassPath后边跟的根目录，没有后缀，className后完整类路径，也没有后缀
                        sClassPool.appendClassPath(unzipDir.absolutePath)

                        // 3、开始注入，去除.class后缀
                        if (className.endsWith(CLASS)) {
                            className = className.substring(0, className.length() - CLASS.length())
                        }

                        CtClass ctClass = sClassPool.getCtClass(className)

                        if (!ctClass.isInterface()) {
                            CtMethod ctMethod
                            if (methodParams.isEmpty()) {
                                ctMethod = ctClass.getDeclaredMethod(methodName)
                            } else {
                                //getDeclaredMethod第二个参数为CtClass数组，需将参数进行转化
                                CtClass[] params = new CtClass[methodParams.size()]
                                for (int i = 0; i < methodParams.size(); i++) {
                                    String param = methodParams.get(i)
                                    params[i] = sClassPool.getCtClass(param)
                                }
                                ctMethod = ctClass.getDeclaredMethod(methodName, params)
                            }

                            if ("{}".equals(injectValue)) {
                                CtClass exceptionType = sClassPool.get("java.lang.Throwable")
                                String returnValue = "{return null;}"
                                CtClass returnType = ctMethod.getReturnType()
                                if (CtClass.booleanType == returnType) {
                                    returnValue = "{return false;}"
                                } else if (CtClass.voidType == returnType) {
                                    returnValue = "{return;}"
                                } else if (CtClass.byteType == returnType || CtClass.shortType == returnType || CtClass.charType == returnType || CtClass.intType == returnType || CtClass.floatType == returnType || CtClass.doubleType == returnType || CtClass.longType == returnType) {
                                    returnValue = "{return 0;}"
                                } else {
                                    returnValue = "{return null;}"
                                }
                                //添加try-catch
                                ctMethod.addCatch(returnValue, exceptionType)
                            } else {
                                if (injectLine > 0) {
                                    ctMethod.insertAt(injectLine, injectValue)
                                } else if (injectLine == 0) {
                                    ctMethod.insertBefore(injectValue)
                                } else {
                                    //替换方法体
                                    if (!injectValue.startsWith("{")) {
                                        injectValue = "{" + injectValue
                                    }
                                    if (!injectValue.endsWith("}")) {
                                        injectValue = injectValue + "}"
                                    }
                                    ctMethod.setBody(injectValue)
                                }
                            }

                            ctClass.writeFile(unzipDir.absolutePath)
                            ctClass.detach()
                        } else {
                            com.meitu.plugin.utils.Logger.e(className + " is interface and can't inject code ！！！")
                        }
                    }
                }
            }
        }

        // 4、循环体结束，判断classes文件夹下是否有文件
        if (com.meitu.plugin.utils.FileUtils.hasFiles(unzipDir)) {

            destFile = new File(jarDir, jar.name)
            com.meitu.plugin.utils.FileUtils.clearFile(destFile)
            com.meitu.plugin.utils.FileUtils.zipJarFile(unzipDir, destFile) //打包jar

            if (null != mExtension && !mExtension.keepFixedClassFile) {
                com.meitu.plugin.utils.FileUtils.clearFile(unzipDir)
            }
        } else {
            com.meitu.plugin.utils.FileUtils.clearFile(rootFile)
        }

        jarFile.close()

        return destFile
    }

    //添加默认依赖
    private void appendDefaultClassPath() {
        if (null == mProject) return
        def androidJar = new StringBuffer().append(mProject.android.getSdkDirectory())
                .append(File.separator).append("platforms")
                .append(File.separator).append(mProject.android.compileSdkVersion)
                .append(File.separator).append("android.jar").toString()
        Logger.e("androidJar:" + androidJar)
        File file = new File(androidJar)
        if (!file.exists()) {
            androidJar = new StringBuffer().append(mProject.rootDir.absolutePath)
                    .append(File.separator).append("local.properties").toString()

            Properties properties = new Properties()
            properties.load(new File(androidJar).newDataInputStream())

            def sdkDir = properties.getProperty("sdk.dir")

            androidJar = new StringBuffer().append(sdkDir)
                    .append(File.separator).append("platforms")
                    .append(File.separator).append(mProject.android.compileSdkVersion)
                    .append(File.separator).append("android.jar").toString()

            file = new File(androidJar)
        }

        if (file.exists()) {
            sClassPool.appendClassPath(androidJar);
        } else {
            com.meitu.plugin.utils.Logger.e("couldn't find android.jar file !!!")
        }
    }

    //添加依赖
    private void appendClassPathIfNecessary() {
        if (!isDependenciesAdded && null != mExtension && null != mExtension.dependencies) {
            isDependenciesAdded = true
            mExtension.dependencies.each { dependence ->
                File file = new File(dependence)
                if (file.isDirectory()) {
                    sClassPool.appendPathList(dependence)
                } else {
                    sClassPool.appendClassPath(dependence)
                }
            }
        }
    }
}
