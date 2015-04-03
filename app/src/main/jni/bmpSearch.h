/**
 * @file
 * Contains the declaration of the graphSearch function.
 * By Yun Wu [yunwu3] @ University of Illinois at Urbana Champaign, March 2015
 */

#ifndef BMP_SEARCH_H
#define BMP_SEARCH_H
#include <android/bitmap.h>

JNIEXPORT jstring Java_cs241_simrn_MapPhaseActivity_graphSearch(JNIEnv
                                                             * env, jobject  obj, jobject graph,jobject subGraph);
#endif
