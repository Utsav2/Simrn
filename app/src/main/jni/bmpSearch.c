/**
 * @file
 * Contains the implementation of the graphSearch function.
 * By Yun Wu [yunwu3] @ University of Illinois at Urbana Champaign, March 2015
 */


#include <stdio.h>
#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "bmpSearch.h"
//includes "bmp.h" by header file "bmpSearch.h"

#define  LOG_TAG    "MapNDK"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct
{
    uint8_t alpha;
    uint8_t red;
    uint8_t green;
    uint8_t blue;
} argb;

/* Considering returning a int * to send back result (not implemented yet, follow "====>" in comments)
 * Return NULL if subGraph does not matches any part of graph
 * Return an int pointer "posit" when found the first match:
 * posit[0] is x value
 * posit[1] is y value
 * Upper left pixel of Graph is where (x,y) = (0,0), the one to its right is (1,0)
 */
JNIEXPORT jstring Java_cs241_simrn_MapPhaseActivity_graphSearch(JNIEnv
                                                             * env, jobject  obj, jobject graph,jobject subGraph) {

        AndroidBitmapInfo  infograph;
        void*              pixelsgraph;
        AndroidBitmapInfo  infosubgraph;
        void*              pixelssubgraph;
        char result_buffer[1000];
        int ret;

        LOGI("graphSearch");
        if ((ret = AndroidBitmap_getInfo(env, graph, &infograph)) < 0) {
            LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
            return;
        }


        if ((ret = AndroidBitmap_getInfo(env, subGraph, &infosubgraph)) < 0) {
            LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
            return;
        }

        if ((ret = AndroidBitmap_lockPixels(env, graph, &pixelsgraph)) < 0) {
            LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        }

        if ((ret = AndroidBitmap_lockPixels(env, subGraph, &pixelssubgraph)) < 0) {
            LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        }

    int graphwidth = infograph.width;
    int graphheight = infograph.height;
    int subgraphwidth = infosubgraph.width;
    int subgraphheight = infosubgraph.height;

    argb pg;//pixel of graph
    argb ps;//pixel of subgraph
    
    //x,y is the current position on graph
    //no need for a position record on subgraph: checking always starts with (0,0)
    int x = 0;
    int y = 0;
    
    while(y <= graphheight - subgraphheight){

        argb * current_pixels_graph =  (argb *)((char *) pixelsgraph + infograph.stride * y);
        pg = current_pixels_graph[x];
        argb * current_pixels_subgraph =  (argb *)(pixelssubgraph);
        ps = current_pixels_subgraph[0];

        //If the first pixel matches, check the other pixels, return if subgraph found, continue if some pixel not matched.
        if (subgraphwidth <= (graphwidth - x) && pg.red == ps.red && pg.green == ps.green && pg.blue == ps.blue) {

            int a = x;
            int b = y;
            int c = 0;
            int d = 0;

            argb spg;//pixel of graph
            argb sps;//pixel of subgraph
            
            while(d < subgraphheight){//loop for checking every pixel

                argb * current_pixels_line =  (argb * )((char *) pixelsgraph + infograph.stride * b);
                spg = current_pixels_line[a];
                argb * current_subpixels_line =  (argb *)((char *) pixelsgraph + infosubgraph.stride * d);
                sps = current_subpixels_line[c];

                if(spg.red == sps.red && spg.green == sps.green && spg.blue == sps.blue){//This pair of pixels matches
                    a = a + 1;
                    c = c + 1;
                }else{//A pair of pixels that do not matches breaks while loop.
                    break;
                    d = subgraphheight + 1;
                }
                
                if(c == subgraphwidth){//A whole row done, go to the beginning of next row
                    a = x;
                    b = b + 1;
                    c = 0;
                    d = d + 1;
                }

                if(d == subgraphheight){//Every pixel matches and no more pixel to check
                    /* int* posit = new int[2];
                    posit[0] = x;
                    posit[1] = y; */
                    //======>To send back the result:
                    //======>return posit;
                    //======>REMEMBER: free posit

                    sprintf(result_buffer, "{ \"result\" : [%d, %d], \"status\" : true}", x, y);
                    return (*env)->NewStringUTF(env, result_buffer);

                }
                
            }
        }
        
        //Goes to the next pixel on graph
        x = x + 1;
        if(x == graphwidth){x = 0; y++;}
    }
    
    //=========> Consider return NULL when failed
    sprintf(result_buffer, "{\"status\" : false}");
    return (*env)->NewStringUTF(env, result_buffer);
}
