#
# Include this make file to build your application against this module.
#
# Make sure to include it after you've set all your desired LOCAL variables.
# Note that you must explicitly set your LOCAL_RESOURCE_DIR before including this file.
#
# For example:
#
#   LOCAL_RESOURCE_DIR := \
#        $(LOCAL_PATH)/res
#
#   include frameworks/opt/setupwizard/library/common-eclair-mr1.mk
#

LOCAL_RESOURCE_DIR += \
    $(call my-dir)/main/res \
    $(call my-dir)/eclair-mr1/res
LOCAL_AAPT_FLAGS += --auto-add-overlay \
    --extra-packages com.android.setupwizardlib \
    --extra-packages android.support.v7.appcompat
LOCAL_STATIC_JAVA_LIBRARIES += setup-wizard-lib-eclair-mr1-compat
