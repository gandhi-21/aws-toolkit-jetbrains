// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc


import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.junit5.hyphenateWithClass
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import software.aws.toolkits.jetbrains.uitests.TestCIServer
import software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts.specificUpdateAcceptChangesTestScript
import software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts.specificUpdatesMakeChangesTestScript
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

class AmazonQDocUpdateSpecificChangesFlowTest {
    init {
        di = DI {
            extend(di)
            bindSingleton<CIServer>(overrides = true) { TestCIServer }
        }
    }

    private val testResourcesPath = "src/test/tstData"

    @BeforeEach
    fun setUp() {
        // Setup test environment
        print("Setting up environment")
        setupTestEnvironment()
    }

    private fun setupTestEnvironment() {
        // Ensure Puppeteer is installed
        print("\n Installing Puppeteer...")
        val npmInstall = ProcessBuilder()
            .command("npm", "install", "puppeteer")
            .inheritIO()
            .start()
            .waitFor()

        assertEquals(0, npmInstall, "Failed to install Puppeteer")
        print("Puppeteer installed successfully")
    }
    @Test
    fun `Make changes button brings you to UPDATE w specific changes flow`() {
        val testCase = TestCase(
            IdeProductProvider.IC,
            LocalProjectInfo(
                Paths.get("tstData", "Hello")
            )
        ).useRelease("2024.3")

        Paths.get(System.getProperty("user.home"), ".aws", "sso", "cache", "ee1d2538cb8d358377d7661466c866af747a8a3f.json")
            .createParentDirectories()
            .writeText(
                """
                """.trimIndent()
            )

        Paths.get(System.getProperty("user.home"), ".aws", "sso", "cache", "d3b447f809607422aac1470dd17fbb32e358cdb3.json")
            .writeText(
                """
                """.trimIndent()
            )

        Starter.newContext(CurrentTestMethod.hyphenateWithClass(), testCase).apply {
            System.getProperty("ui.test.plugins").split(File.pathSeparator).forEach { path ->
                pluginConfigurator.installPluginFromPath(
                    Path.of(path)
                )
            }

            copyExistingConfig(Paths.get("tstData", "config"))
            updateGeneralSettings()
        }.runIdeWithDriver()
            .useDriverAndCloseIde {
                waitForProjectOpen()
                Thread.sleep(30000)

                val result = executeScript(specificUpdatesMakeChangesTestScript)
            }
    }

    @Test
    fun `Update README with specific changes`() {
        val testCase = TestCase(
            IdeProductProvider.IC,
            LocalProjectInfo(
                Paths.get("tstData", "Hello")
            )
        ).useRelease("2024.3")

        Paths.get(System.getProperty("user.home"), ".aws", "sso", "cache", "ee1d2538cb8d358377d7661466c866af747a8a3f.json")
            .createParentDirectories()
            .writeText(
                """
                """.trimIndent()
            )

        Paths.get(System.getProperty("user.home"), ".aws", "sso", "cache", "d3b447f809607422aac1470dd17fbb32e358cdb3.json")
            .writeText(
                """
                """.trimIndent()
            )

        Starter.newContext(CurrentTestMethod.hyphenateWithClass(), testCase).apply {
            System.getProperty("ui.test.plugins").split(File.pathSeparator).forEach { path ->
                pluginConfigurator.installPluginFromPath(
                    Path.of(path)
                )
            }

            copyExistingConfig(Paths.get("tstData", "config"))
            updateGeneralSettings()
        }.runIdeWithDriver()
            .useDriverAndCloseIde {
                waitForProjectOpen()
                Thread.sleep(30000)

                val result = executeScript(specificUpdateAcceptChangesTestScript)
            }
    }

    private fun executeScript(scriptContent: String): String {
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
}

