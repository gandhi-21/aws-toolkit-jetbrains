// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests

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
import software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts.executeScript
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

class AmazonQChatTest {

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
    fun `can open up IDE`() {
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
                    ADD TOKEN
                """.trimIndent()
            )

        Paths.get(System.getProperty("user.home"), ".aws", "sso", "cache", "d3b447f809607422aac1470dd17fbb32e358cdb3.json")
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

            copyExistingConfig(Paths.get("tstData", "config"))
            updateGeneralSettings()
        }.runIdeWithDriver()
            .useDriverAndCloseIde {
                waitForProjectOpen()
                Thread.sleep(30000)

                val result = executeScript(scr, testResourcesPath)
//                assertTrue(result.contains("/doc"))
            }
    }
}

// language=TS
private val scr = """
// updates readme

const puppeteer = require('puppeteer');

async function findAndClickButton(page, buttonText, clickButton = false, timeout = 5000) {
  try {
      // Wait for any matching buttons to be present
      await page.waitForSelector('button.mynah-button', {
          visible: true,
          timeout
      });

      // Find and verify the specific button
      const buttonHandle = await page.evaluateHandle((text) => {
          const buttons = Array.from(document.querySelectorAll('button.mynah-button'));
          return buttons.find(button => {
              const label = button.querySelector('.mynah-button-label');
              return label && label.textContent.trim() === text;
          });
      }, buttonText);

      // Check if button was found
      const button = buttonHandle.asElement();
      if (!button) {
          console.log(buttonText)
          throw new Error(`Button with text not found`);
      }

      // Verify button is visible and enabled
      const isVisible = await page.evaluate(el => {
          const style = window.getComputedStyle(el);
          return style.display !== 'none' &&
                 style.visibility !== 'hidden' &&
                 style.opacity !== '0';
      }, button);

      if (!isVisible) {
          console.log(buttonText)
          throw new Error(`Button with text is not visible`);
      }

      if (clickButton) {
        // Click the button
        await button.click();

        // Optional wait after click
        await new Promise(resolve => setTimeout(resolve, 1000));

        console.log(`Successfully clicked button with text`);
        console.log(buttonText)
      } else {
        return button;
      }


  } catch (error) {
      console.error(`Error interacting with button:`, buttonText, error);
      throw error;
  }
}


async function testNavigation() {
    const browser = await puppeteer.connect({
        browserURL: 'http://localhost:9222'
    })

    try {

        const pages = await browser.pages()
        //console.log(pages)
        for(const page of pages) {
            const contents = await page.evaluate(el => el.innerHTML, await page.${'$'}(':root'));
            //console.log(contents)
            const element = await page.${'$'}('.mynah-chat-prompt-input')
            if(element) {
                console.log('found')

                await page.type('.mynah-chat-prompt-input', '/doc')
                await page.keyboard.press('Enter')

                console.log('entered /doc')
                console.log('found commands')

                await findAndClickButton(page, 'Update an existing README', true, 10000)
                console.log('clicked update readme')
                await findAndClickButton(page, 'Make a specific change', true, 10000)
                console.log('clicked make a specific change')
                await findAndClickButton(page, 'Yes', true, 10000)
                console.log('clicked yes')

                await page.type('.mynah-chat-prompt-input', 'Add new section titled Programming Languages which describes the programming languages and version of programming language used in this project.')
                await page.keyboard.press('Enter')

                await new Promise(resolve => setTimeout(resolve, 90000));

                await findAndClickButton(page, 'Accept', true, 10000)
                console.log('clicked Accept')

            }
        }


    } finally {
        await browser.close();
    }
}

testNavigation().catch(console.error);

""".trimIndent()
