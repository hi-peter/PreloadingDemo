package com.meitu.plugin.extension
/**
 * 字节码处理配置项
 * @author Ljq
 * @date 2017/12/12
 */

public class BytecodeFixExtension {

    /**
     * 字节码修复器是否可用，默认可用
     */
    boolean enable = true

    /**
     * 是否开启日志功能，默认开启
     */
    boolean logEnable = true

    /**
     * 是否保留修复过的jar文件，默认保留
     */
    boolean keepFixedJarFile = true

    /**
     * 是否保留修复过的class文件，默认保留
     */
    boolean keepFixedClassFile = true

    /**
     * 构建字节码所依赖的第三方包绝对路径，默认包含了Android.jar文件
     */
    ArrayList<String> dependencies = new ArrayList<String>()

    /**
     * 配置文件集合，配置格式：className##methodName(param1,param2...paramN)##injectValue##injectLine
     * className：表示全类名
     例如：com.tencent.av.sdk.NetworkHelp
     methodName(param1,param2...paramN)：表示方法名及相关参数，参数只写类型且必须以逗号(,)分隔，非基础数据类型要写全路径
     例如：getAPInfo(android.content.Context)
     例如：getAPInfo(android.content.Context, int)
     injectValue：表示待插入代码块，注意代码块要有分号(;)，其中$0表示this；$1表示第一个参数；$2表示第二个参数；以此类推
     例如：$1 = null;System.out.println("I have hooked this method by BytecodeFixer Plugin !!!");
     $1 = null;就是表示把第一个参数置空；接着是打印一句日志
     【注意：】如果injectValue为{}表示给原有方法添加try-catch操作
     injectLine：表示插在方法中的哪一行，该参数可选，如果省略该参数则默认把injectValue插在方法的最开始处
     injectLine > 0 插入具体行数
     injectLine = 0 插入方法最开始处
     injectLine < 0 替换方法体
     */
    ArrayList<String> fixConfig = new ArrayList<>();

    boolean getEnable() {
        return enable
    }

    void setEnable(boolean enable) {
        this.enable = enable
    }

    boolean getLogEnable() {
        return logEnable
    }

    void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable
    }

    boolean getKeepFixedJarFile() {
        return keepFixedJarFile
    }

    void setKeepFixedJarFile(boolean keepFixedJarFile) {
        this.keepFixedJarFile = keepFixedJarFile
    }

    boolean getKeepFixedClassFile() {
        return keepFixedClassFile
    }

    void setKeepFixedClassFile(boolean keepFixedClassFile) {
        this.keepFixedClassFile = keepFixedClassFile
    }

    ArrayList<String> getDependencies() {
        return dependencies
    }

    void setDependencies(ArrayList<String> dependencies) {
        this.dependencies = dependencies
    }

    ArrayList<String> getFixConfig() {
        return fixConfig
    }

    void setFixConfig(ArrayList<String> fixConfig) {
        this.fixConfig = fixConfig
    }

}
