package com.meitu.plugin.task

import org.gradle.api.tasks.bundling.Jar;

/**
 * 构建Jar包的任务器
 *
 * @author Ljq
 * @date 2017/12/12
 */

public class BuildJarTask extends Jar {

    BuildJarTask() {
        group = "BytecodeFixBuildJarTask"
    }
}
