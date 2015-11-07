# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\...\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
#Basic
-keep class android.support.v7.app** { *; }
-keep class android.support.v4.app** { *; }
-useuniqueclassmembernames

-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic

-optimizationpasses 5
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field

-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

#Keep all classes in package model: BookLab and Book
-keep class com.simpleastudio.recommendbookapp.model.**{
    public protected private *;
}

-keep public class * extends android.view.View {
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
   public void set*(...);
}

-keepclassmembers class * extends android.content.Context {
   void *(android.view.View);
   void *(android.view.MenuItem);
}

#Get rid of logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

###############
# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
#For butterknife @Bind
-keep class * {
    @butterknife.* <fields>;
    @butterknife.* <methods>;
}

################
#Volley
-keep class com.android.volley.** {*;}

#http://stackoverflow.com/questions/32248414/android-volley-signed-apk-issue
#Since android API 23 Apache HTTP Client is removed from Android:
#Volley still uses the HTTP client
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**



##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------##
