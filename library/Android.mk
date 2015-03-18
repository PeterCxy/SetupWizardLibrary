LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SDK_VERSION := current
LOCAL_MANIFEST_FILE := main/AndroidManifest.xml
LOCAL_MODULE := setup-wizard-lib
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/main/res
LOCAL_SRC_FILES := $(call all-java-files-under, main/src)

include $(BUILD_STATIC_JAVA_LIBRARY)
