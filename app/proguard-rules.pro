# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
##-------------------------5.基本不用动区域--------------------------
##指定代码的压缩级别
#-optimizationpasses 5
#
##包明不混合大小写
#-dontusemixedcaseclassnames
#
##不去忽略非公共的库类
#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
#
##混淆时是否记录日志
#-verbose
#
##优化  不优化输入的类文件
#-dontoptimize
#
##预校验
#-dontpreverify
#
## 保留sdk系统自带的一些内容 【例如：-keepattributes *Annotation* 会保留Activity的被@override注释的onCreate、onDestroy方法等】
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
#
# 记录生成的日志数据,gradle build时在本项根目录输出
# apk 包内所有 class 的内部结构
-dump build/proguard/class_files.txt
# 未混淆的类和成员
-printseeds build/proguard/seeds.txt
# 列出从 apk 中删除的代码
-printusage build/proguard/unused.txt
# 混淆前后的映射
-printmapping build/proguard/mapping.txt
#
#
## 避免混淆泛型
#-keepattributes Signature
## 抛出异常时保留代码行号,保持源文件以及行号
#-keepattributes SourceFile,LineNumberTable
#
-keep class moniter.tyw.com.moniterlibrary.** {*;}
##-----------------------------6.默认保留区-----------------------
## 保持 native 方法不被混淆
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-keepclassmembers public class * extends android.view.View {
# public <init>(android.content.Context);
# public <init>(android.content.Context, android.util.AttributeSet);
# public <init>(android.content.Context, android.util.AttributeSet, int);
# public void set*(***);
#}
#
##保持 Serializable 不被混淆
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    !static !transient <fields>;
#    !private <fields>;
#    !private <methods>;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
## 保持自定义控件类不被混淆
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context,android.util.AttributeSet);
#}
## 保持自定义控件类不被混淆
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context,android.util.AttributeSet,int);
#}
## 保持自定义控件类不被混淆
#-keepclassmembers class * extends android.app.Activity {
#    public void *(android.view.View);
#}
#
## 保持枚举 enum 类不被混淆
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## 保持 Parcelable 不被混淆
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
#
## 不混淆R文件中的所有静态字段，我们都知道R文件是通过字段来记录每个资源的id的，字段名要是被混淆了，id也就找不着了。
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
### 保持哪些类不被混淆
#-keep public class * extends android.app.Appliction
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Fragment
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.preference.Preference
##
##-keep class com.zhy.http.okhttp.**{*;}
##-keep class com.wiwide.util.** {*;}

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**

# This class is deprecated, but remains for backward compatibility.
-dontwarn android.util.FloatMath

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep class androidx.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# These classes are duplicated between android.jar and org.apache.http.legacy.jar.
-dontnote org.apache.http.**
-dontnote android.net.http.**

# These classes are duplicated between android.jar and core-lambda-stubs.jar.
-dontnote java.lang.invoke.**


# ============忽略警告，否则打包可能会不成功=============
#-ignorewarnings