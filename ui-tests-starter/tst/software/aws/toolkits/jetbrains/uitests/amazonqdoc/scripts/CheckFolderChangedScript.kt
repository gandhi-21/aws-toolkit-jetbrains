// Copyright 2025 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.uitests.amazonqdoc.scripts

// language=TS
private val checkFolderChanged = """
    const puppeteer = require('puppeteer');

async function testNavigation() {
    const browser = await puppeteer.connect({
        browserURL: 'http://localhost:9222'
    })
 
    try {     
        const pages = await browser.pages();
        console.log(await pages[0].content());
    } finally {
        await browser.close();
    }
}
     
    testNavigation().catch(console.error);
""".trimIndent()

val checkFolderChangedScript = checkFolderChanged.plus(findAndClickButton)
