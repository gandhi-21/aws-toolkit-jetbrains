// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

fun executeScript(scriptContent: String, testResourcesPath: String = "src/test/tstData"): String {
    val scriptFile = File("$testResourcesPath/temp-script.js")
    scriptFile.parentFile.mkdirs()
    scriptFile.writeText(scriptContent)

    val process = ProcessBuilder()
        .command("node", scriptFile.absolutePath)
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().use { it.readText() }
    print(output)
    val exitCode = process.waitFor()

    scriptFile.delete()

    assertEquals(0, exitCode, "Script execution failed with output: $output")
    return output
}
