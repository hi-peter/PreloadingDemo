### 代码注入简介
> 本插件是基于[Javassist](http://jboss-javassist.github.io/javassist/)开发的一款轻量级的字节码修复插件，它可以有效的修复第三方Jar包中出现的一些bug，例如：NullPointerException，NumberFormatException，IndexOutOfBoundsException等。它利用[Gradle](http://tools.android.com/tech-docs/new-build-system/transform-api)1.5.0版本后的[Transform API](http://google.github.io/android-gradle-dsl/javadoc/) 在项目打包时动态的对class文件进行修复。

> 参考：http://blog.csdn.net/u010386612/article/details/51131642

### 插件使用
- **引入插件** ：在`根项目`的build.gradle添加如下配置：
```gradle
apply plugin: 'groovy'
```
- **添加需注入的代码及位置** ：在BytecodeFixPlugin添加：
```
 ArrayList<String> configs = new ArrayList<>()
 configs.add("要注入的代码及位置") //添加glide要注入的代码及路径，格式参考BytecodeFixExtension中的fixConfig注释说明
 extension.setFixConfig(configs)
```
```
- **配置说明**
 - `enable`  true | false
  -- BytecodeFixer插件是否可用
 - `logEnable`  true | false
 -- 是否允许打印日志
 - `keepFixedJarFile` true | false
 -- 是否保存修复过的Jar文件
 - `keepFixedClassFile` true | false
 -- 是否保存修复过的class文件
 - `dependencies`
 -- 依赖的第三方Jar文件或class文件的绝对路径
 - `fixConfig`注入配置，格式为A##B##C##D
    -- A：表示待修复的类名，例如：com.tencent.av.sdk.NetworkHelp
    -- B：表示待修复的方法名，例如：getAPInfo(android.content.Context)
    -- C：表示修复内容，例如：$1 = null;System.out.println("I have hooked this method by BytecodeFixer plugin !!!");
    -- D：表示把修复内容插入在方法的哪一行，`D > 0` 表示插在具体的行数，`D == 0`表示插在方法的最开始处，`D < 0`表示替换方法的全部内容
