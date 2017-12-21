*本文主要阐述笔者在接入tinker遇到的问题以及个人解决方案，仅供参考*
- 枪简介
- 如何学会用这把枪
- 如何造子弹
- 如何和其他装备混合使用
- 如何有效管理子弹
- 如何让子弹自动上膛

## 枪简介
### tinker 何方神圣
笔者引用[tinker](https://github.com/Tencent/tinker)官方文档解释

> Tinker是微信官方的Android热补丁解决方案，它支持动态下发代码、So库以及资源，让应用能够在不需要重新安装的情况下实现更新

从简短的解释中可以看出tinker的威力所在。<br>
tinker底层实现原理非常复杂，由于笔者能力有限，仅仅看了一些皮毛内容，便浅尝辄止；关于更多相关tinker介绍
可以看看如下几篇文章:

1. [Tinker Dexdiff算法解析](https://www.zybuluo.com/dodola/note/554061)
2. [微信Tinker的一切都在这里，包括源码](http://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=2649286384&idx=1&sn=f1aff31d6a567674759be476bcd12549&scene=4#wechat_redirect)
3. [Tinker：技术的初心与坚持](https://github.com/WeMobileDev/article/blob/master/Tinker%EF%BC%9A%E6%8A%80%E6%9C%AF%E7%9A%84%E5%88%9D%E5%BF%83%E4%B8%8E%E5%9D%9A%E6%8C%81.md)
<p align='right'>摘抄自tinker官方团队</p>

### 所谓的对比

没有对比就没有伤害，笔者依旧会放出这张图供各位享有

![compare](https://user-gold-cdn.xitu.io/2017/12/15/1605a3c193b7fea2?w=860&h=972&f=png&s=126915)

**当然阿里的[Sophix](https://help.aliyun.com/document_detail/51416.html?spm=5176.doc53287.6.540.27QCjm)在某些方面(非侵入式、及时生效)做到了更好**，如果团队想要快速实现热修复，它可能是一个更快速的解决方案；另外腾讯团队bugly基于tinker推出的[tinker-patch-sdk](https://bugly.qq.com/docs/user-guide/instruction-manual-android-upgrade/?v=20171123163535)一站式解决方案，可能由于它免费，业界采用这种方案较多。上述的两种解决方案，都已经自动的帮我们管理了补丁的crud以及自动加载、合成等操作，笔者为什么还要煞费苦心去造轮子呢？
![pain](https://user-gold-cdn.xitu.io/2017/12/18/160678aa2f13c5a4?w=121&h=140&f=jpeg&s=4983)
### 为什么选择tinker
热修复前几年发展势头挺猛的，且这项技术愈发成熟，对应的解决方案一套接着一套，要想真正意义上实现它并开源，这项工作是非常艰难的，感谢大厂的大牛同志开源了他们解决方案。笔者是今年年前开始接触tinker的，在这之前假装做了一些调研工作。

1. github优质项目评判标准(ps:个人看法)

    >* star、fork 数
    >* 持续维护更新
    >* issues持续解决修复
    >* 大厂 or 技术大牛出品那就更好了
2. tinker如何杀出重围，拔得头衔的？
    - [官方解释](https://github.com/Tencent/tinker/wiki#%E4%B8%BA%E4%BB%80%E4%B9%88%E4%BD%BF%E7%94%A8tinker)
    - 个人看法
        - 代码开源且免费试用
        - 能与[andresguard](https://github.com/shwenzhang/AndResGuard) 、加固等较好的结合
        - 能解决大部分问题
## 如何学会用这把枪

### 前期准备


1. 需要大致了解补丁产生、到修复过程的过程
![Patch synthesis process](https://user-gold-cdn.xitu.io/2017/12/18/16067cb6c03907c8?w=1642&h=780&f=png&s=77110)
2. 关于实现原理
    * 可以简单了解下Android [ClassLoader](http://androidxref.com/7.1.1_r6/xref/libcore/dalvik/src/main/java/dalvik/system/)的加载流程，参考笔者之前[热修复前期预备知识](https://juejin.im/post/5a3bc8666fb9a0452b49690f)
    * tinker是基于Android原生的ClassLoader，开发了自己的ClassLoader，然后加载patch文件的字节码
    * 基于android原生的aapt(Android Asset Packaging Tool)，开发了自己的aapt, 完成patch文件资源的加载
    * 微信团队基于dex文件格式，研发了自己的DexDiff算法，比较两个apk的区别，从而生成patch文件
3. 文档是最好的老师之一
    > **[官方文档](https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97) 一定要仔细阅读 一定要仔细阅读 一定要仔细阅读！！！**

### 接入流程
*官方文档对使用说明这块做了非常详细的说明，笔者不打算赘述，主要是阐述下接入过程中遇到的一些问题,做一个简单的日志记录*
#### 基础实现篇
1. gradle 接入

    想想平时为什么在build.gradle 中，可以很方便的使用一些构造关键字，类似于

    ```gradle
    android{
        signingConfigs{}
        defaultConfig{}
        dexOptions{}
        lintOptions{}
        compileOptions{}
        flavorDimensions
        ...
    }
    ```
    上述脚本中，他们从何而来？其实所有的一切都是来自官方自定义的Android插件源码中
    ```com.android.tools.build:gradle:X.X.X``` <br/>
    恰巧tinker通过自定义gradle-plugin（插件）来构建补丁包任务，我们可以在build.gradle 添加构建参数。例如

    ```gradle
    tinkerPatch{
         oldApk = getOldApkPath()
         tinkerId = versionName
         ignoreChange = ["assets/sample_meta.txt"]

    }
    ```

    参数  | 含义
    ------------- | -------------
    tinkerId  | 用于确定补丁包运行在哪个基准包上，比如：基准包的tinkerID为2.5.6，补丁包的tinkerId 也必须是2.5.6，否则补丁包在合成的时候就会抛出异常
    ignoreChaned  | 指定不受影响的资源路径 ,忽略那些资源修改 ,在编译时会忽略该文件的新增、删除与修改

    另外，**笔者强烈推荐将tinker相关gradle配置分离成独立脚本**，app build.gradle

    ```gradle
    apply from: './tinker.gradle'
    ```

2. 代码改造

    按照官方文档所述，需要将Application类以及继承逻辑迁移到自己ApplicationLike继承类中。在实际开发场景中，项目可能很庞大且复杂，影响性难以评估，鉴于此，笔者考虑不对代码进行迁移，按照如下方案进行替代：<br/>
    1. 通过 ```tinker-Annotation``` 插件生成的```GenerateTinkerApplication```
    2. 项目基类```BaseApplication``` extends ```GenerateTinkerApplication```
    3. ```MyApplication``` extends ```BaseApplication```

    这样做会发现，代码几乎未改动。接下来，生成补丁包、加载补丁包、重启一些列操作后，程序竟然崩溃了？
    ![xiadu](https://user-gold-cdn.xitu.io/2017/12/18/1606888b0df94f4b?w=231&h=223&f=jpeg&s=11813)
    笔者手机是Android7.1.1系统。通过错误日志发现启动页BaseApplication.get() 获取到的全局context实例为null，关于问题的更多的描述可以参考[problem](https://github.com/Tencent/tinker/issues/622) <br/> 笔者在解决这个问题钻了不少牛角尖，最后在issues上提了一下这个问题，tinker作者回复并给出了原因
    ![answer](https://user-gold-cdn.xitu.io/2017/12/18/16068926bbc55011?w=1638&h=1612&f=png&s=329203)
    其中[wiki文章](https://github.com/WeMobileDev/article/blob/master/Android_N%E6%B7%B7%E5%90%88%E7%BC%96%E8%AF%91%E4%B8%8E%E5%AF%B9%E7%83%AD%E8%A1%A5%E4%B8%81%E5%BD%B1%E5%93%8D%E8%A7%A3%E6%9E%90.md)<br/>
    最终，笔者依旧按照官方文档就行application的改造迁移。<br/>

#### 自定义拓展篇
- 补丁合成流程
    1. 检查补丁文件合法性
    2. 唤醒补丁合成进程
    3. 补丁合成ing
    4. 补丁合成结果回调
    5. 补丁合成后续操作
    6. 删除补丁文件
    7. 重启加载补丁、效果展示

- 通过自定义某些监听类，可以实现如下功能点
    - 补丁合成、加载过程相关日志上报
    - 自定义一些操作行为
        - 补丁合成完成后续动作e.g 重启
        - 合成成功 缓存当前合成成功记录
        - ....

笔者通过自定义```DefaultPatchReporter```,做了以下两件事儿

```java
@Override
    public void onPatchResult(File patchFile, boolean success, long cost) {
        super.onPatchResult(patchFile, success, cost);
        if (success) {
            DLog.w("合成消耗时间：" + cost);
            DLog.w("@@@@ L42", "CustomerPatchReporter:onPatchResult() -> " + "补丁合成成功：" + patchFile.getAbsolutePath());
            String fileMd5 = FileMd5Util.getFileMd5(patchFile);
            if (!TextUtils.isEmpty(fileMd5)) {
                PatchLogReporter.updatePatchCompositeCnt(fileMd5);
                // 将当前合成的补丁文件md5 保存到本地sp
                SharedPreferences sp = context.getSharedPreferences(TinkerManager.SP_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit().putString(TinkerManager.SP_KEY_PATCH, fileMd5);
                SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
            }
        } else {
            // 合成失败
            DLog.w("@@@@ L42", "CustomerPatchReporter:onPatchResult() -> " + "补丁合成失败");
        }
    }
```
将合成成功的补丁文件md5值记录到缓存文件，**防止重复去合成**

```java
@Override
    public void onPatchPackageCheckFail(File patchFile, int errorCode) {
        //  如果补丁文件不删除 每次重启 调用tinker加载补丁 都会回调该方法
        String errorInfo = TranslateErrorCode.onLoadPackageCheckFail(errorCode);
        DLog.w("@@@@ L54", "CustomerPatchReporter:onPatchPackageCheckFail() -> " + TranslateErrorCode.onLoadPackageCheckFail(errorCode));
        // 补丁合成失败 上报补丁合成失败原因
        String fileMd5 = FileMd5Util.getFileMd5(patchFile);
        if (!TextUtils.isEmpty(fileMd5)) {
            // 错误日志上传到服务器
            PatchLogReporter.reportPatchCompositeErrorInfo(fileMd5, errorCode, errorInfo);
        }
        super.onPatchPackageCheckFail(patchFile, errorCode);
        if (errorCode == ERROR_PACKAGE_CHECK_TINKER_ID_NOT_EQUAL) {
            Tinker.with(context).cleanPatchByVersion(patchFile);
        }
    }
```
合成过程中，如果出现了异常，**将错误日志上传到服务器中**。注意一些回调方法可能运行在不同的进程中<br/>
更多相关自定义内容，请自行参考[Tinker自定义扩展](https://github.com/Tencent/tinker/wiki/Tinker-%E8%87%AA%E5%AE%9A%E4%B9%89%E6%89%A9%E5%B1%95)

## 如何造子弹

### 生成基准包

这一过程较为简单，基础打包命令即可，额外需要注意的两点

>* 任何情况下都需要备份基准包吗？
>* 如何管理多渠道包？

1. 通过```-Pparams=true```动态参数传递进行判断

```gradle
android.applicationVariants.all { variant ->
    def buildTypeName = variant.buildType.name
    tasks.all {
         it.doLast {
             def isNeedBackup = project.hasProperty("isNeedBackup") ? project.getProperties().get("isNeedBackup") : "false"
             // 根据此变量判断是否需要备注
             // ... 基准文件拷贝逻辑
         }
    }
}
```


2. 由于笔者未使用```gradle productFlavors```方式进行多渠道编包, 主要是由于两个原因

 * 编译速度
 * 不同渠道基准apk无法使用同一个补丁包:[原因](https://github.com/Tencent/tinker/wiki/Tinker-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98#%E5%A6%82%E4%BD%95%E5%85%BC%E5%AE%B9%E5%A4%9A%E6%B8%A0%E9%81%93%E5%8C%85)

笔者采用的是美团提供的解决方案[walle](https://github.com/Meituan-Dianping/walle)

编包命令

1. 未采用walle

   ```gradle
   ./gradlew assembleXXX -PisNeedBackup=true --stacktrace
   ```
2. 采用walle

   ```gradle
    ./gradlew assembleReleaseChannels -PisNeedBackup=true --stacktrace
   ```
上述仅作为参考，不同情况做不同处理

### 生成补丁

1. 路径指定

    ```gradle
    def bakPath = file("${rootDir}/tinkerBackup/${versionNamePrefix}")
    ext {
        // 基准apk路径
        tinkerOldApkPath = "${bakPath}/app-debug-2.6.6.apk"
        // 基准apk mapping 文件
        tinkerApplyMappingPath = "${bakPath}/app-debug-2017-12-13-mapping.txt"
        // 基准apk R文件 -> excute assembleRelease 后，会在bak 目录下生成R文件
        tinkerApplyResourcePath = "${bakPath}/app-debug-2017-12-13-R.txt"
    }
    ```

2. 任务执行

    ```gradle
    ./gradlew tinkerPatchXXX -PisNeedBackup=false --stacktrace
    ```
## 如何和其他装备混合使用

### 兼容andreguard资源压缩工具

当然你可以提前将old apk and new apk 生成出来，然后分别在``tinkerPatch``中配置oldApk,newApk ，执行``tinkerPatchXXX`` 将补丁文件生成出来，这没有任何问题。但是能否通过gradle脚本来帮我们实现这一步骤呢？依旧是可以！想想这个问题
> patch文件到底是如何生成的？

无外乎是通过oldApk 、newApk 对比生成出来的，所以最核心的就是离不开这两个文件，所以实现如下功能即可

1. 能对``oldApk`` 备份吗？(ps: andresguard 可以指定 ``finalApkBackupPath`` 输出apk路径)
2. ``oldApk`` 、``applyMapping``、``applyResourceMapping`` 路径指定正确吗？
3. 能保证newApk 是 执行``andresguardXXX``任务后产生的吗？

![code](https://user-gold-cdn.xitu.io/2017/12/19/1606eac55a8f0688?w=198&h=135&f=jpeg&s=4003)

从第一小点说起，对``oldApk`` 进行备份，无外乎就一个``copy``逻辑(此处就不贴代码)，程序员最拿手的活。这个地方笔者维护了一个``prefix.txt``文件，主要为了统一备份文件的前缀，便于生成补丁包时指定``oldApk`` 、``applyMapping``、``applyResourceMapping`` 这些路径。

```gradle
File intoFileDir = file(bakPath.absolutePath) // bakPath 备份路径
if (intoFileDir.exists()) {
    // 如果存在备份 删除
    println("============================= will delete history baseapk...")
    delete(intoFileDir)
}
println("=================================: start copy file to destination")
def newPrefix = "${project.name}-${variant.baseName}-${versionName}-${date}"
// 将newPrefix 文件内容写入到一个临时文件，方便后期生成补丁包：方便设置基准内容
File prefixVersionFile = new File("${bakPath.absolutePath}/prefix.txt")
if (!prefixVersionFile.parentFile.exists()) {
    prefixVersionFile.parentFile.mkdirs()
}
if (!prefixVersionFile.exists()) {
    prefixVersionFile.createNewFile()
}
prefixVersionFile.write(newPrefix)
```

对于第二点：

```gradle
// 修正版本前缀显示 形如v1.2.2
def versionNamePrefix = "v${getVersionName()}"
// 定义备份文件位置
def bakPath = file("${rootDir}/tinkerBackup/${versionNamePrefix}")
ext {
    // 是否启用tinker
    tinkerEnable = enableTinker.toBoolean()

    def prefix = readPrefixContent(bakPath.absolutePath)
    println('------------prefix = ' + prefix)
    // 基准apk路径
    tinkerOldApkPath = "${bakPath}/${prefix}.apk"
    // 基准apk mapping 文件
    tinkerApplyMappingPath = "${bakPath}/${prefix}-mapping.txt"
    // 基准apk R文件 -> excute assembleRelease 后，会在bak 目录下生成R文件
    tinkerApplyResourcePath = "${bakPath}/${prefix}-R.txt"
    // 指定多渠道包路径 生成对应渠道patch文件
    tinkerBuildFlavorDirectory = "${bakPath}/"
}
```
其中``readPrefixContent`` 方法就是读取 备份逻辑时将 文件前缀 写入到``prefix.txt``文件的内容。这样如果变更备份相关文件的文件名，只需要修改prefix.txt即可

至于第三点：<br/>
就需要开发者了解``gradle for android `` 的一些api，例如：``doFirst doLast ...``的含义，怎样才能做到让``resguardXXX``任务执行先与``tinkerPatchXXX``呢？

```gradle
if ("tinkerPatch${buildTypeName}".equalsIgnoreCase(it.name)) {
    // 将当前it任务（tinkerPatchRelease）临时存放到tempPointer
    def tempPointer = it
    def resguardTask
    tasks.all {
        if (it.name.equalsIgnoreCase("resguard${taskName.capitalize()}")) {
            resguardTask = it
            tempPointer.doFirst({
                // 指定tinkerPatch newApk路径 以生成补丁包
                it.buildApkPath = "${buildDir}/outputs/apk/${andResOutputPrefix}/${ouputApkNamePrefix}_${andResSuffix}.apk"
                    // ..
        })
        tempPointer.dependsOn tinkerPatchPrepare
        tempPointer.dependsOn resguardTask
        }
    }
}

```

从代码中，可以看出：找到``tinkerPatchXXX`` 任务 和 ``resugardXXX`` 任务，通过``dependsOn`` 设置执行顺序，细心的朋友会发现 我还使用了一次``dependsOn``依赖了另外一个任务``tinkerPatchPrepare``。这是干啥的呢？接着往下看....<br/>

笔者最开始的想法是能不能 不在

```gradle
ext {
    appName = "dlandroidzdd"
    // 是否启用tinker
    tinkerEnable = enableTinker.toBoolean()
    // 基准apk路径
    tinkerOldApkPath = ""
    // 基准apk proguard mapping 文件
    tinkerApplyMappingPath = ""
    // 基准apk R文件 -> excute assembleRelease 后，会在bak 目录下生成R文件
    tinkerApplyResourcePath = ""

    // 指定多渠道包路径 生成对应渠道patch文件
    tinkerBuildFlavorDirectory = ""
}

tinkerPatch{
    oldApk = getOldApkPath()
    ....

    buildConfig {
        applyMapping = getApplyMappingPath()
    }
}
```
这些代码块进行赋值操作呢？想想``dependsOn``强大的作用，能不能在执行``tinkerPatchXXX``生成补丁任务之前，通过``project.tinkerPatch.XX = ?`` 这种形式进行指定赋值呢？二话没说，笔者撸起袖子就开干了

```gradle
task tinkerPatchPrepare << {
    File intoFileDir = file(bakPath.absolutePath)
    if (intoFileDir.exists()) {
        def prefix = null
        File prefiFile = new File("${bakPath.absolutePath}/prefix.txt")
        if (new File("${bakPath.absolutePath}/prefix.txt").exists()) {
            prefix = prefiFile.getText()
        }
        if (null != prefix) {
            // 如果存在备份  对全局变量赋值操作
            project.tinkerPatch.oldApk = "${bakPath}/${prefix}.apk"
            project.tinkerPatch.buildConfig.applyMapping = "${bakPath}/${prefix}-mapping.txt"
            project.tinkerPatch.buildConfig.applyResourceMapping = "${bakPath}/${prefix}-resource_mapping.txt"
        }
    }
}
```

似乎这样可行？<br/>
第一次，修改基准**java**代码，故意不在ext变量中进行指定基准文件，执行补丁生成任务，加载补丁，修复bug 完美~<br/>
第二次，改动布局，依旧故意不在ext变量中进行指定基准文件，执行补丁生成任务，竟然抛出了如下错误
![error](https://user-gold-cdn.xitu.io/2017/12/19/1606ed440156e7fc?w=1235&h=938&f=jpeg&s=269065)
根据错误日志分析，resource.arsc(App的资源索引表)发生改变了，根据项目实际情况，笔者怀疑：

**以下两段代码未生效?**

```gradle
...
project.tinkerPatch.buildConfig.applyMapping = "${bakPath}/${prefix}-mapping.txt"
project.tinkerParch.buildConfig.applyResourceMapping=${bakPath}/${prefix}-resource_mapping.txt
...
```
为了验证这个怀疑是正确的，笔者屏蔽上述代码，手动在ext中对mapping path等全局变量进行手动赋值，执行生成补丁包任务命名``tinkerPatchXXX`` ，果然不出所料，补丁包正常打出来了，这也就论证了这两行代码未起到实际作用。<br/>
为什么会出现这种情况呢？笔者再次怀疑：

**有没有可能在调用上述两段代码前，已经将对应的基准文件路径读出来 并缓存到内存变量中呢？执行``tinkerPatch`` task 时 使用的是内存变量**

笔者按照下述2个步骤印证了上述怀疑

1. 查看``tinker-patch-gradle-plugin``项目源码

![tinker-code](https://user-gold-cdn.xitu.io/2017/12/19/1606f61b6aebee8a?w=2902&h=1670&f=png&s=836832)
从标注可以看出

* 如果applyResourceMapping合法，gradle log会打印出路径
* 赋值操作是在``afterEvaluate`` action中执行的

2. 动手操作

依然依赖``tinkerPatchPrepare`` 任务时，执行补丁生成命令
![tinker-log](https://user-gold-cdn.xitu.io/2017/12/19/1606f6bca3d27b7b?w=1392&h=194&f=png&s=40262)
不依赖``tinkerPatchPrepare`` 任务时，手动对基准文件进行赋值
![tinker-log1](https://user-gold-cdn.xitu.io/2017/12/19/1606f6fe7b1df75c?w=2542&h=196&f=png&s=60681)

根据源码分析可得，如果mapping文件合法，标注的箭头后应该加上resource_mapping.txt文件的路径，这也就确定了通过方法设置时机不对，也就是我们下面要提到的``afterEvaluate``任务执行时机，官方文档阐述

> Adds an action to execute immediately after this project is evaluated.

大致意思就是：解析完成(配置、语法等) 所有配置，task依赖关系已经生成，这个任务才会执行，也就是说它执行先于task执行，tinker已经将变量读取并存储到其他内存变量中了，后续无论如何改变都已无效，因为他不会再次去读取。千言万语回城一句话**获取时机先于设置时机**

### 兼容walle多渠道打包方案

明确一点：采用官方的提供的``productFlavors`` 会改变源码``BuildConfig`` 变更，进而导致classes.dex差异，所以笔者采用了``walle``多渠道打包方案，**所有渠道包都可以使用同一个补丁**<br/>
仔细想想，其实兼容walle，无外乎也需要解决上述的三个问题，oldapk、newapk 、mapping等文件，如果完成了兼容``andresguard`` ，那兼容``walle`` 易如反掌了。此处不再赘述

### 共同兼容？
关于如何同时兼容``andresguard`` 和 ``walle`` ，怎样通过``dependsOn``让``andresguard``先执行呢？ 似乎只能修改源码，笔者尝试在``walle``提一个issue ，发现[drakeet](https://github.com/drakeet)已经提出了此问题

![walle&andresguard](https://user-gold-cdn.xitu.io/2017/12/18/1606a3cdc99b2788?w=2140&h=1464&f=png&s=554834)

那就坐等修复吧~

### 总结

笔者在处理这块的时候，对``gradle for android`` 也是了解不多，大部分都是靠自己猜测然后论证，可能有些地方解释不标准,望见谅！笔者未在网上找到较好的教材，依旧推荐[官方文档](https://docs.gradle.org/current/dsl/org.gradle.api.Task.html)吧，后续有时间对这块进行深入学习 ，然后再分享一篇吧~

## 如何有效管理子弹

### nodejs + mysql 搭建后端api接口

实现如下功能即可

>* 基础账户体系、APP crud 、Appversion crud
>* 补丁crud操作，提供对外(web、app)接口实现
>* 错误日志上传

笔者采用了以下库完成了基础功能开发

* [express](http://www.expressjs.com.cn/)
* body-parser
* cookie-parser
* [mysql](https://github.com/mysqljs/mysql)
* uuid
* [multer](https://github.com/expressjs/multer)
* ....

数据库表：用户、应用、引用版本、补丁、错误日志<br/>
每一个路由基本上都需要实现基础crud功能，关于这块代码，无外乎就是一些基础sql 和 相关库api的使用</br>

> 关于获取指定版本最新补丁文件？

``patch_code`` 记录当前补丁index, int 类型递增值，``patch_code`` 最大的那个补丁文件即是最新的补丁文件

> 关于更新**指定**补丁文件下载数量、合成成功数量、相关日志

考虑到APP端代码，每一个补丁表维护一个``patch_md5``，以上数据均通过它进行维护更新

> 其他问题

笔者在对接口进行联调时，发现接口无法调用，发现数据库经常断开连接，经过排查 **当数据库连接超过一定时间没有活动后，会自动关闭该连接**，关于这个问题，网上大部分的做法就是 `` mysql.createPool(config)`` 创建连接池，当然，这里笔者也是采用这种做法


### 补丁管理web平台

采用了``bootstrap``前端框架，界面凑合看吧
![patch-web](https://user-gold-cdn.xitu.io/2017/12/20/160748fbccf2714e?w=3346&h=898&f=png&s=164119)

## 如何让子弹自动上膛
### 补丁加载流程
![patch-load-process](https://user-gold-cdn.xitu.io/2017/12/20/160749179b6cb511?w=1324&h=1496&f=png&s=99655)
两个建议

* 将这块的实现放到``Service`` 去实现<br/>
* 将热修复管理实现代码抽取成一个独立的功能组件

