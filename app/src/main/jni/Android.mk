LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#opencv library
#민지 경로
#OPENCVROOT:= C:\SDKProjectAnd\ImageManageApp1\opencv

#윤하 경로
OPENCVROOT:= C:\SDKCloud\ImageManageApp\opencv

#주영 경로
#OPENCVROOT:= C:\AndroidWorkspace3\ImageManageApp\opencv

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}\native\jni\OpenCV.mk


LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := main.cpp
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)