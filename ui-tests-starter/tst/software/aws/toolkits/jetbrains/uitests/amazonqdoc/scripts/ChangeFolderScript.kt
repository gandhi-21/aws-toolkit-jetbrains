// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

// language=TS
private val changeFolder = """
    const puppeteer = require('puppeteer');

    async function testNavigation() {
        const browser = await puppeteer.connect({
            browserURL: 'http://localhost:9222'
        })
     
        try {
            const pages = await browser.pages()

            for(const page of pages) {

                const element = await page.${'$'}('.mynah-chat-prompt-input')
                if(element) {     
                    await page.type('.mynah-chat-prompt-input', '/doc')
                    await page.keyboard.press('Enter')
     
                    await findAndClickButton(page, 'Create a README', true, 10000)

                    await findAndClickButton(page, 'Change folder', true, 10000)
                }
            }
        } finally {
            await browser.close();
        }
    }
     
    testNavigation().catch(console.error);
""".trimIndent()

val changeFolderScript = changeFolder.plus(findAndClickButton)
