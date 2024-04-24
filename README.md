# proto-gl

## Overview
**proto-gl** is a versatile, multiplatform library designed for rendering mesh animations using OpenGL (LWJGL bindings). 

It provides developers tools to load, manage, and display animations loaded a binary format for high-performance graphics applications.

## Features
- Efficient animation handling: Streamlines the process of mesh animation using `.pmod` and `.panim` files.
- Detailed control over animations and models: Manage every aspect of the animation state and model rendering.

## File Structure Overview

### `.pmod` Model File (Binary Format)
The `.pmod` file format is used to describe 3D models in a structured binary form that includes mesh data, skeletal information, and animation nodes. Below is a detailed breakdown of its structure:

[Link to the Blender Addon (io_pmod_blender_addon)](https://github.com/ModularMods/proto-gl/tree/main/io_pmod_blender_addon)

```plaintext
BYTE[4] "PMOD"              // Magic Number (ASCII)
BYTE    major               // Version (Major)
BYTE    minor               // Version (Minor)
BYTE    flags               // Model flags, uuuuuuAN (u = unused, N = has nodes, A = has armature)

BYTE    numJoints           // Number of joints in the armature
BYTE    numNodes            // Number of nodes in the model

// Conditional: If the model has an armature
IF hasArmature
    BYTE[]  skeletonName   // Name of the skeleton
    // For each joint in the skeleton
    FOR each joint in skeleton
        BYTE[]  jointName          // Name of the joint
        BYTE[]  parentJointName    // Name of the parent joint
        FLOAT[16] invBindMatrix    // Inverse Bind Matrix of the joint
END IF

// Loop over each node in the model
FOR each node in model
    BYTE[]  nodeName            // Name of the node
    BYTE[]  parentNodeName      // Name of the parent node, or "root" if no parent
    FLOAT[3] translation        // Translation vector of the node
    FLOAT[4] rotation           // Rotation quaternion of the node
    FLOAT[3] scale              // Scale vector of the node
    INT     numMeshes           // Number of meshes in the node

    // Loop over each mesh in the node
    FOR each mesh in node
        BYTE[]  meshName         // Name of the mesh
        INT     numIndices       // Number of indices in the mesh
        INT     numVertices      // Number of vertices in the mesh
        INT     numJoints        // Number of joints affecting the mesh (0 if none)
        INT     numWeights       // Number of weights per vertex (0 if none)

        INT[numIndices] indices               // Indices of the mesh
        FLOAT[numVertices * 3] vertices       // Vertices of the mesh (x, y, z per vertex)
        FLOAT[numVertices * 2] uvs            // UV coordinates of the mesh (u, v per vertex)
        FLOAT[numVertices * 3] normals        // Normal vectors of the mesh (x, y, z per normal)

        // Conditional: If there are joints
        IF numJoints > 0
            INT[numVertices * numJoints] joints    // Joint indices per vertex
        END IF

        // Conditional: If there are weights
        IF numWeights > 0
            FLOAT[numVertices * numWeights] weights   // Weights per vertex
        END IF
    END FOR
END FOR
```

### `.panim` Animation File:

The `.panim` file format is dedicated to storing animation data linked to `.pmod` model files. This format details the animation sequences and the nodes they affect:

```plaintext
BYTE[] "PANIM"               // Magic Number (ASCII)
BYTE major                  // Version (Major)
BYTE minor                  // Version (Minor)
BYTE flags                  // Model flags, uuuuuuAN (u = unused, N = has nodes, A = has armature)
BYTE numAnims               // Total animations contained

// Loop over each animation
FOR each animation in numAnims
    BYTE[] animationName        // Animation name (ASCII) 0x00 terminated
    BYTE numChannels            // Number of channels in the animation

    // Loop over each channel
    FOR each channel in numChannels
        BYTE channelType        // Type of channel (0 for translation, 1 for rotation, 2 for scale)
        BYTE[] nodeName         // Name of the node this channel affects
        INT numKeyframes        // Number of keyframes in this channel

        // Loop over each keyframe
        FOR each keyframe in numKeyframes
            FLOAT keyframeTime  // Time of this keyframe
            FLOAT[] keyValue    // Value at this keyframe (depends on channel type)
```

## Getting Started
To start using **proto-gl** in your projects, refer to the installation and setup guidelines provided in the documentation. Ensure your development environment is set up with the necessary OpenGL libraries and compilers suitable for your platform.
