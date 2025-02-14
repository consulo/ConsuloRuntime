/*
 * Copyright (c) 2011, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "jni.h"

#import <pthread.h>
#import <assert.h>

#import <Cocoa/Cocoa.h>
#import <JavaNativeFoundation/JavaNativeFoundation.h>
#import <Carbon/Carbon.h>

//#define DEBUG 1

// number of mouse buttons supported
extern int gNumberOfButtons;

// InputEvent mask array
extern jint* gButtonDownMasks;

@interface AWTToolkit : NSObject { }
+ (long) getEventCount;
+ (void) eventCountPlusPlus;
+ (jint) scrollStateWithEvent: (NSEvent*) event;
+ (NSEvent*) latestPerformKeyEquivalentEvent;
+ (void) setLatestPerformKeyEquivalentEvent:(NSEvent *)val;
@end

/*
 * Utility Macros
 */

/** Macro to cast a jlong to an Objective-C object (id). Casts to long on 32-bit systems to quiesce the compiler. */
#define OBJC(jl) ((id)jlong_to_ptr(jl))

#ifndef kCFCoreFoundationVersionNumber10_13_Max
#define kCFCoreFoundationVersionNumber10_13_Max 1499
#endif

#ifndef kCFCoreFoundationVersionNumber10_14_Max
#define kCFCoreFoundationVersionNumber10_14_Max 1599
#endif

#ifndef IS_OSX_GT10_13
#define IS_OSX_GT10_13 (floor(kCFCoreFoundationVersionNumber) > \
    kCFCoreFoundationVersionNumber10_13_Max)
#endif

#ifndef IS_OSX_GT10_14
#define IS_OSX_GT10_14 (floor(kCFCoreFoundationVersionNumber) > \
kCFCoreFoundationVersionNumber10_14_Max)
#endif

