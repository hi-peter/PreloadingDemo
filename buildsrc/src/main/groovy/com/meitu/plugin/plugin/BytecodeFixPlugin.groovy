package com.meitu.plugin.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 自定义的gradle插件,Module名必须为buildsrc
 *
 * @author Ljq
 * @date 2017/12/12
 */
public class BytecodeFixPlugin implements Plugin<Project> {

    private static final String VERSION_NAME = "1.0"
    private static final String CONFIG_GLID = "com.bumptech.glide.load.data.HttpUrlFetcher##loadData(com.bumptech.glide.Priority)##{java.lang.Class cls = java.lang.Class.forName(\"com.meitu.preload.manager.PreloadingManager\");java.lang.reflect.Method method = cls.getMethod(\"getInstance\" , new Class[0]);Object obj = method.invoke(cls,new Object[0]);method = cls.getMethod(\"addRequestCount\" , new Class[0]);method.invoke(obj,new Object[0]);java.io.InputStream is =  loadDataWithRedirects(this.glideUrl.toURL(), 0, null, this.glideUrl.getHeaders());method = cls.getMethod(\"reduceRequestCount\" , new Class[0]);method.invoke(obj,new Object[0]); return is;}##-1"

    @Override
    void apply(Project project) {

        def android = project.extensions.findByType(AppExtension.class)
//        def versionName = android.defaultConfig.versionName

        project.extensions.create("bytecodeFixConfig", com.meitu.plugin.extension.BytecodeFixExtension)
        com.meitu.plugin.extension.BytecodeFixExtension extension = project.bytecodeFixConfig

        com.meitu.plugin.utils.Logger.enable = extension.logEnable
        ArrayList<String> configs = new ArrayList<>()
        configs.add(CONFIG_GLID) //添加glide要注入的代码及路径
        extension.setFixConfig(configs)
       // 注册Transfrom
        android.registerTransform(new com.meitu.plugin.transform.BytecodeFixTransform(project, VERSION_NAME, extension))
    }
}
