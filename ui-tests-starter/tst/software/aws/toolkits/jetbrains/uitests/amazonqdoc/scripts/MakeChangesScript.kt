// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

// language=TS
private val makeChanges = """
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
     
                    await findAndClickButton(page, 'Update an existing README', true)
                    
                    await findAndClickButton(page, 'Make a specific change', true, 10000)

                    await findAndClickButton(page, 'Yes', true)

                    await page.type('.mynah-chat-prompt-input', 'remove the repository structure section')
                    await page.keyboard.press('Enter')
     
                    await new Promise(resolve => setTimeout(resolve, 90000));
     
                    await findAndClickButton(page, 'Accept', true)
                }
            }
        } finally {
            await browser.close();
        }
    }
     
    testNavigation().catch(console.error);
""".trimIndent()

val makeChangesScript = makeChanges.plus(findAndClickButton)
