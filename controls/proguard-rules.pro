-repackageclasses 'com.aol.mobile.sdk.controls'
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepparameternames
-renamesourcefileattribute SourceFile

-dontnote com.google.android.gms.**
-dontnote android.net.http.**
-dontnote android.support.**
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-keepattributes Exceptions

-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class **.BuildConfig* {
    public static <fields>;
}

-keep public class com.aol.mobile.sdk.controls.* {
    public protected *;
}

-keep public class com.aol.mobile.sdk.controls.viewmodel.* {
    public protected *;
}

-keep public class com.aol.mobile.sdk.controls.view.* {
    public protected *;
}

-keep public class com.aol.mobile.sdk.controls.utils.ViewUtils {
    public protected *;
}
