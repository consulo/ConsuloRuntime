/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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

#ifndef COMMON_H
#define COMMON_H

#include <simd/SIMD.h>

#define PGRAM_VERTEX_COUNT 6
#define QUAD_VERTEX_COUNT 4

enum VertexAttributes {
    VertexAttributePosition = 0,
    VertexAttributeTexPos = 1
};

enum BufferIndex  {
    MeshVertexBuffer = 0,
    FrameUniformBuffer = 1,
    MatrixBuffer = 2
};

struct FrameUniforms {
    vector_float4 color;
};

struct TransformMatrix {
    matrix_float4x4 transformMatrix;
};

struct GradFrameUniforms {
    vector_float3 params;
    vector_float4 color1;
    vector_float4 color2;
};

struct Vertex {
    float position[2];
};

struct TxtVertex {
    float position[2];
    float txtpos[2];
};

struct TxtFrameUniforms {
    vector_float4 color;
    int mode;
    int isSrcOpaque;
    int isDstOpaque;
};
#endif
