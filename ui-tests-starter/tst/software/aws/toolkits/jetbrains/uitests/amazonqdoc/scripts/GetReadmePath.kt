// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

import java.io.File
import java.net.URI
import java.nio.file.Paths

fun getReadmePath(delete: Boolean = false): URI {
    val readmePath = Paths.get("tstData", "Hello", "README.md").toUri()
    if (delete) {
        File(readmePath).takeIf { it.exists() }?.delete()
    }
    return readmePath
}
