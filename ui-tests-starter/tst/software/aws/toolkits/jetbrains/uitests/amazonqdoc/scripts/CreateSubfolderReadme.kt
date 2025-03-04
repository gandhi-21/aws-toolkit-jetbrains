// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

// language=TS
private val createSubfolderReadme = """     
    const puppeteer = require('puppeteer');
          
    async function testNavigation() {
        const browser = await puppeteer.connect({
            browserURL: 'http://localhost:9222'
        })
     
        try {
            const pages = await browser.pages()

            for(const page of pages) {
                await findAndClickButton(page, 'Yes', true, 10000)
                
                await new Promise(resolve => setTimeout(resolve, 45000));
                
                await findAndClickButton(page, 'Accept', true)
            }
        } finally {
            await browser.close();
        }
    }
     
    testNavigation().catch(console.error);
""".trimIndent()

val createSubfolderReadmeScript = createSubfolderReadme.plus(findAndClickButton)
