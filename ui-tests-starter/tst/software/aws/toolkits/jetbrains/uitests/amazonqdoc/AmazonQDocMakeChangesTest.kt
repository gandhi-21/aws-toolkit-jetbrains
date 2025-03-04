// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc

import com.intellij.driver.sdk.waitForIndicators
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.config.ConfigurationStorage
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.junit5.hyphenateWithClass
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import software.aws.toolkits.jetbrains.uitests.TestCIServer
import software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts.makeChangesScript
import software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts.executeScript
import software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts.getReadmePath
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.seconds

class AmazonQDocMakeChangesTest {
    init {
        di = DI {
            extend(di)
            bindSingleton<CIServer>(overrides = true) { TestCIServer }
            val defaults = ConfigurationStorage.instance().defaults.toMutableMap().apply {
                put("LOG_ENVIRONMENT_VARIABLES", (!System.getenv("CI").toBoolean()).toString())
            }

            bindSingleton<ConfigurationStorage>(overrides = true) {
                ConfigurationStorage(this, defaults)
            }
        }
    }

    companion object {
        @JvmStatic
        @AfterAll
        fun cleanup() {
            // Delete any README.md files created by the test
            getReadmePath(delete = true)
        }
    }

    @BeforeEach
    fun setUp() {
        setupTestEnvironment()

        // copy the file "README_TO_COPY.md" in ui-tests-starter/tstData to ui-tests-starter/tstData/Hello and rename it README.md
        val readmeToCopy = Paths.get("tstData", "Hello", "README_TO_COPY.md")
        val readme = Paths.get("tstData", "Hello", "README.md") // Fix path to include Hello subdirectory
        readmeToCopy.toFile().readText().let { contents ->
            readme.createParentDirectories() // Create parent directories if they don't exist
            readme.toFile().writeText(contents)
        }
    }

    private fun setupTestEnvironment() {
        // Ensure Puppeteer is installed
        val npmInstall = ProcessBuilder()
            .command("npm", "install", "puppeteer")
            .inheritIO()
            .start()
            .waitFor()

        Assertions.assertEquals(0, npmInstall, "Failed to install Puppeteer")
    }

    @Test
    fun `can create README in doc`() {
        val testCase = TestCase(
            IdeProductProvider.IC,
            LocalProjectInfo(
                Paths.get("tstData", "Hello")
            )
        ).useRelease("2024.3")

        // KB
        Paths.get(
            System.getProperty("user.home"),
            ".aws",
            "sso",
            "cache",
            "ee1d2538cb8d358377d7661466c866af747a8a3f.json"
        )
            .createParentDirectories()
            .writeText(
                """
                ADD TOKEN
                """.trimIndent()
            )

        // bytes COPY CREDS!!!!
        Paths.get(
            System.getProperty("user.home"),
            ".aws",
            "sso",
            "cache",
            "d3b447f809607422aac1470dd17fbb32e358cdb3.json"
        )
            .writeText(
                """
               ADD TOKEN
                """.trimIndent()
            )

        Starter.newContext(CurrentTestMethod.hyphenateWithClass(), testCase).apply {
            System.getProperty("ui.test.plugins").split(File.pathSeparator).forEach { path ->
                pluginConfigurator.installPluginFromPath(
                    Path.of(path)
                )
            }

            copyExistingConfig(Paths.get("tstData", "configAmazonQTests"))
            updateGeneralSettings()
        }.runIdeWithDriver()
            .useDriverAndCloseIde {
                waitForProjectOpen()
                waitForIndicators(60.seconds)
                val readmePath = getReadmePath()
                Thread.sleep(2000)

                executeScript(makeChangesScript)

                val readmeFile = File(readmePath)
                assert(readmeFile.exists()) { "README.md file should exist" }
                assert(readmeFile.length() > 0) { "README.md file should have content" }
            }
    }
}
