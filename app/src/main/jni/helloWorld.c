#include <stdio.h>
#include <jni.h>
#include <string.h>

jstring Java_cs241_simrn_NativeClass_helloNdkString(JNIEnv* env, jobject this)
{
    return (*env)->NewStringUTF(env,"Hello NDK!!");
}