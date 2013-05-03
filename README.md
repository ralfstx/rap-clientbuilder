Client library builder for RWT WebClient
========================================

The JsCompressor is used for assembling the JavaScript library for the WebClient.
It reads JavaScript files from the project org.eclipse.rap.rwt, compresses and
concatenates them, and writes the resulting JavaScript file back into the project
as client.js.

As of RAP 2.1, this project has been moved into the [RAP repository](http://git.eclipse.org/c/rap/org.eclipse.rap.git/tree/releng).

For earlier versions, please check out the releated branch.

Requires
--------
Bundle org.mozilla.javascript from Orbit (Branch v1_6_6)
dev.eclipse.org:/cvsroot/tools/org.eclipse.orbit/org.mozilla.javascript
You can import this project using the team project set file JSCompressor.psf.

Includes
--------
Code from YUI Compressor version 2.4.2 from:
http://yuilibrary.com/downloads/#yuicompressor

License
-------
Code in package com.yahoo.platform.yui.compressor is licensed under the BSD
License: http://developer.yahoo.net/yui/license.txt

All other code in this project is licensed under the EPL:
http://www.eclipse.org/legal/epl-v10.html

[1] Rich Ajax Platform  http://www.eclipse.org/rap
