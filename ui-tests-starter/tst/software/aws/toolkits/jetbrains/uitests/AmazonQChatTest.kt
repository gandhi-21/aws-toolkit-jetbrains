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
import org.junit.jupiter.api.Assertions.assertTrue
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
                    {"clientId":"dlDxa3ikg77D_nNkExE7QXVzLWVhc3QtMQ","clientSecret":"eyJraWQiOiJrZXktMTU2NDAyODA5OSIsImFsZyI6IkhTMzg0In0.eyJzZXJpYWxpemVkIjoie1wiY2xpZW50SWRcIjp7XCJ2YWx1ZVwiOlwiZGxEeGEzaWtnNzdEX25Oa0V4RTdRWFZ6TFdWaGMzUXRNUVwifSxcImlkZW1wb3RlbnRLZXlcIjpudWxsLFwidGVuYW50SWRcIjpudWxsLFwiY2xpZW50TmFtZVwiOlwiQVdTIElERSBQbHVnaW5zIGZvciBKZXRCcmFpbnNcIixcImJhY2tmaWxsVmVyc2lvblwiOm51bGwsXCJjbGllbnRUeXBlXCI6XCJQVUJMSUNcIixcInRlbXBsYXRlQXJuXCI6bnVsbCxcInRlbXBsYXRlQ29udGV4dFwiOm51bGwsXCJleHBpcmF0aW9uVGltZXN0YW1wXCI6MTc0ODM2Mjc3OC4zMDMwNjYwNDksXCJjcmVhdGVkVGltZXN0YW1wXCI6MTc0MDU4Njc3OC4zMDMwNjYwNDksXCJ1cGRhdGVkVGltZXN0YW1wXCI6MTc0MDU4Njc3OC4zMDMwNjYwNDksXCJjcmVhdGVkQnlcIjpudWxsLFwidXBkYXRlZEJ5XCI6bnVsbCxcInN0YXR1c1wiOm51bGwsXCJpbml0aWF0ZUxvZ2luVXJpXCI6XCJodHRwczpcL1wvdmlldy5hd3NhcHBzLmNvbVwvc3RhcnRcL1wiLFwiZW50aXRsZWRSZXNvdXJjZUlkXCI6bnVsbCxcImVudGl0bGVkUmVzb3VyY2VDb250YWluZXJJZFwiOm51bGwsXCJleHRlcm5hbElkXCI6bnVsbCxcInNvZnR3YXJlSWRcIjpudWxsLFwic2NvcGVzXCI6W3tcImZ1bGxTY29wZVwiOlwiY29kZXdoaXNwZXJlcjpjb252ZXJzYXRpb25zXCIsXCJzdGF0dXNcIjpcIklOSVRJQUxcIixcImFwcGxpY2F0aW9uQXJuXCI6bnVsbCxcImZyaWVuZGx5SWRcIjpcImNvZGV3aGlzcGVyZXJcIixcInVzZUNhc2VBY3Rpb25cIjpcImNvbnZlcnNhdGlvbnNcIixcInR5cGVcIjpcIkltbXV0YWJsZUFjY2Vzc1Njb3BlXCIsXCJzY29wZVR5cGVcIjpcIkFDQ0VTU19TQ09QRVwifSx7XCJmdWxsU2NvcGVcIjpcImNvZGV3aGlzcGVyZXI6dHJhbnNmb3JtYXRpb25zXCIsXCJzdGF0dXNcIjpcIklOSVRJQUxcIixcImFwcGxpY2F0aW9uQXJuXCI6bnVsbCxcImZyaWVuZGx5SWRcIjpcImNvZGV3aGlzcGVyZXJcIixcInVzZUNhc2VBY3Rpb25cIjpcInRyYW5zZm9ybWF0aW9uc1wiLFwidHlwZVwiOlwiSW1tdXRhYmxlQWNjZXNzU2NvcGVcIixcInNjb3BlVHlwZVwiOlwiQUNDRVNTX1NDT1BFXCJ9LHtcImZ1bGxTY29wZVwiOlwiY29kZXdoaXNwZXJlcjp0YXNrYXNzaXN0XCIsXCJzdGF0dXNcIjpcIklOSVRJQUxcIixcImFwcGxpY2F0aW9uQXJuXCI6bnVsbCxcImZyaWVuZGx5SWRcIjpcImNvZGV3aGlzcGVyZXJcIixcInVzZUNhc2VBY3Rpb25cIjpcInRhc2thc3Npc3RcIixcInR5cGVcIjpcIkltbXV0YWJsZUFjY2Vzc1Njb3BlXCIsXCJzY29wZVR5cGVcIjpcIkFDQ0VTU19TQ09QRVwifSx7XCJmdWxsU2NvcGVcIjpcImNvZGV3aGlzcGVyZXI6Y29tcGxldGlvbnNcIixcInN0YXR1c1wiOlwiSU5JVElBTFwiLFwiYXBwbGljYXRpb25Bcm5cIjpudWxsLFwiZnJpZW5kbHlJZFwiOlwiY29kZXdoaXNwZXJlclwiLFwidXNlQ2FzZUFjdGlvblwiOlwiY29tcGxldGlvbnNcIixcInR5cGVcIjpcIkltbXV0YWJsZUFjY2Vzc1Njb3BlXCIsXCJzY29wZVR5cGVcIjpcIkFDQ0VTU19TQ09QRVwifSx7XCJmdWxsU2NvcGVcIjpcImNvZGV3aGlzcGVyZXI6YW5hbHlzaXNcIixcInN0YXR1c1wiOlwiSU5JVElBTFwiLFwiYXBwbGljYXRpb25Bcm5cIjpudWxsLFwiZnJpZW5kbHlJZFwiOlwiY29kZXdoaXNwZXJlclwiLFwidXNlQ2FzZUFjdGlvblwiOlwiYW5hbHlzaXNcIixcInR5cGVcIjpcIkltbXV0YWJsZUFjY2Vzc1Njb3BlXCIsXCJzY29wZVR5cGVcIjpcIkFDQ0VTU19TQ09QRVwifV0sXCJhdXRoZW50aWNhdGlvbkNvbmZpZ3VyYXRpb25cIjpudWxsLFwiZW5hYmxlZEdyYW50c1wiOntcIkFVVEhfQ09ERVwiOntcInR5cGVcIjpcIkltbXV0YWJsZUF1dGhvcml6YXRpb25Db2RlR3JhbnRPcHRpb25zXCIsXCJyZWRpcmVjdFVyaXNcIjpbXCJodHRwOlwvXC8xMjcuMC4wLjFcL29hdXRoXC9jYWxsYmFja1wiXX0sXCJSRUZSRVNIX1RPS0VOXCI6e1widHlwZVwiOlwiSW1tdXRhYmxlUmVmcmVzaFRva2VuR3JhbnRPcHRpb25zXCJ9fSxcImVuZm9yY2VBdXRoTkNvbmZpZ3VyYXRpb25cIjpudWxsLFwib3duZXJBY2NvdW50SWRcIjpudWxsLFwic3NvSW5zdGFuY2VBY2NvdW50SWRcIjpudWxsLFwidXNlckNvbnNlbnRcIjpudWxsLFwibm9uSW50ZXJhY3RpdmVTZXNzaW9uc0VuYWJsZWRcIjpudWxsLFwic2hvdWxkR2V0VmFsdWVGcm9tVGVtcGxhdGVcIjpmYWxzZSxcImhhc0luaXRpYWxTY29wZXNcIjp0cnVlLFwiaGFzUmVxdWVzdGVkU2NvcGVzXCI6ZmFsc2UsXCJhcmVBbGxTY29wZXNDb25zZW50ZWRUb1wiOmZhbHNlLFwiZ3JvdXBTY29wZXNCeUZyaWVuZGx5SWRcIjp7XCJjb2Rld2hpc3BlcmVyXCI6W3tcImZ1bGxTY29wZVwiOlwiY29kZXdoaXNwZXJlcjp0cmFuc2Zvcm1hdGlvbnNcIixcInN0YXR1c1wiOlwiSU5JVElBTFwiLFwiYXBwbGljYXRpb25Bcm5cIjpudWxsLFwiZnJpZW5kbHlJZFwiOlwiY29kZXdoaXNwZXJlclwiLFwidXNlQ2FzZUFjdGlvblwiOlwidHJhbnNmb3JtYXRpb25zXCIsXCJ0eXBlXCI6XCJJbW11dGFibGVBY2Nlc3NTY29wZVwiLFwic2NvcGVUeXBlXCI6XCJBQ0NFU1NfU0NPUEVcIn0se1wiZnVsbFNjb3BlXCI6XCJjb2Rld2hpc3BlcmVyOnRhc2thc3Npc3RcIixcInN0YXR1c1wiOlwiSU5JVElBTFwiLFwiYXBwbGljYXRpb25Bcm5cIjpudWxsLFwiZnJpZW5kbHlJZFwiOlwiY29kZXdoaXNwZXJlclwiLFwidXNlQ2FzZUFjdGlvblwiOlwidGFza2Fzc2lzdFwiLFwidHlwZVwiOlwiSW1tdXRhYmxlQWNjZXNzU2NvcGVcIixcInNjb3BlVHlwZVwiOlwiQUNDRVNTX1NDT1BFXCJ9LHtcImZ1bGxTY29wZVwiOlwiY29kZXdoaXNwZXJlcjpjb21wbGV0aW9uc1wiLFwic3RhdHVzXCI6XCJJTklUSUFMXCIsXCJhcHBsaWNhdGlvbkFyblwiOm51bGwsXCJmcmllbmRseUlkXCI6XCJjb2Rld2hpc3BlcmVyXCIsXCJ1c2VDYXNlQWN0aW9uXCI6XCJjb21wbGV0aW9uc1wiLFwidHlwZVwiOlwiSW1tdXRhYmxlQWNjZXNzU2NvcGVcIixcInNjb3BlVHlwZVwiOlwiQUNDRVNTX1NDT1BFXCJ9LHtcImZ1bGxTY29wZVwiOlwiY29kZXdoaXNwZXJlcjpjb252ZXJzYXRpb25zXCIsXCJzdGF0dXNcIjpcIklOSVRJQUxcIixcImFwcGxpY2F0aW9uQXJuXCI6bnVsbCxcImZyaWVuZGx5SWRcIjpcImNvZGV3aGlzcGVyZXJcIixcInVzZUNhc2VBY3Rpb25cIjpcImNvbnZlcnNhdGlvbnNcIixcInR5cGVcIjpcIkltbXV0YWJsZUFjY2Vzc1Njb3BlXCIsXCJzY29wZVR5cGVcIjpcIkFDQ0VTU19TQ09QRVwifSx7XCJmdWxsU2NvcGVcIjpcImNvZGV3aGlzcGVyZXI6YW5hbHlzaXNcIixcInN0YXR1c1wiOlwiSU5JVElBTFwiLFwiYXBwbGljYXRpb25Bcm5cIjpudWxsLFwiZnJpZW5kbHlJZFwiOlwiY29kZXdoaXNwZXJlclwiLFwidXNlQ2FzZUFjdGlvblwiOlwiYW5hbHlzaXNcIixcInR5cGVcIjpcIkltbXV0YWJsZUFjY2Vzc1Njb3BlXCIsXCJzY29wZVR5cGVcIjpcIkFDQ0VTU19TQ09QRVwifV19LFwiY29udGFpbnNPbmx5U3NvU2NvcGVzXCI6ZmFsc2UsXCJzc29TY29wZXNcIjpbXSxcImlzQmFja2ZpbGxlZFwiOmZhbHNlLFwiaXNFeHBpcmVkXCI6ZmFsc2UsXCJpc1YxQmFja2ZpbGxlZFwiOmZhbHNlLFwiaXNWMkJhY2tmaWxsZWRcIjpmYWxzZSxcImlzVjNCYWNrZmlsbGVkXCI6ZmFsc2V9In0.WMAiK6xb6qALDyKTHhv9aqEo5pKMynqxN9T1YmgtCwzeqkqDqU1rJrp1lQcmsO2p","expiresAt":"2025-05-27T16:19:38Z","scopes":["codewhisperer:conversations","codewhisperer:transformations","codewhisperer:taskassist","codewhisperer:completions","codewhisperer:analysis"],"issuerUrl":"https://view.awsapps.com/start","region":"us-east-1","clientType":"public","grantTypes":["authorization_code","refresh_token"],"redirectUris":["http://127.0.0.1/oauth/callback"]}
                """.trimIndent()
            )

        Paths.get(System.getProperty("user.home"), ".aws", "sso", "cache", "d3b447f809607422aac1470dd17fbb32e358cdb3.json")
            .writeText(
                """
                    {"issuerUrl":"https://view.awsapps.com/start","region":"us-east-1","accessToken":"aoaAAAAAGe_TS4wMRGP1EuZs1Y66jslZq8pyQKlcqy91NEGjTBUcV6UFnTKSEyzbcztDFdQ1fd5UHw6ZxyvhgJawYBkc0:MGQCMA1zWXJ7eCuoNMyP69sQXYEWgukNzuoe7Fcd0sYewksFj9btzr3kxFl17F7jfGldjgIwEjz7gEhaefg/e3t/ZOtuB60jD0VZHHJuQ/PRzFw3mdMXmr7zPiKQEb7yMoOOEErE","refreshToken":"aorAAAAAGgr4yAYcYQGOc5l2fG1voB_X_FDbqngj3-WZ3TExruwFuw2Xb6sTbxlOi5kfQtEU0SQPu1krw3x6PPrOYBkc0:MGUCMFUv/Hr/gGEg46/RSFjwcI6A/FXaTOyz2ohJkThtHdD+QibM4AdZJeN6i69xTU80WgIxAN/Zw471rHsSHWm1WHhRMDgDUb6h0kAwBv7zD1XS7fUiIKN1X3XsWD9SNzXR9ScVGQ","expiresAt":"2025-02-26T17:19:42.629625Z","createdAt":"2025-02-26T16:19:42.629630Z"}
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

                val result = executeScript(scr)
//                assertTrue(result.contains("/doc"))
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
