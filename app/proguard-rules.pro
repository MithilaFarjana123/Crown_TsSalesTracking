-keep public class com.google.* {}

-keepattributes Annotation, Signature, Exception

-keep class Model** { *; }


# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions



#ok http

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.ConscryptHostnameVerifier

-keep class com.squareup.okhttp.** { *; }

-keep interface com.squareup.okhttp.** { *; }


#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
# // * rewind();
#}


-keep class android.view.** { *; }

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions



# Add this global rule
-keepattributes Signature

-keep class com.crowncement.TsSalesTracking.data.Model** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**
-keep class com.squareup.picasso.** { *; }
-keepclassmembers class com.squareup.picasso.** { *; }

-keepclassmembers class com.logicbeanzs.uberpolylineanimation.* {
  <fields>;
  <init>();
  <methods>;
}



-dontwarn java.lang.reflect.AnnotatedType
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE



 # Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single