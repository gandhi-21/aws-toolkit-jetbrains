// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

// language=TS
val specificUpdatesMakeChangesScript = """
    
    const puppeteer = require('puppeteer');
    
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

                    await findAndClickButton(page, 'Make changes', true, 90000)
                    console.log('clicked make changes')

                    // ? check describe changes text

                }
            }


        } finally {
            await browser.close();
        }
    }

    testNavigation().catch(console.error);
    
""".trimIndent()

val specificUpdatesMakeChangesTestScript = specificUpdatesMakeChangesScript.plus(findAndClickButton)
