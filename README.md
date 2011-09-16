Client library builder for RWT
==============================

Summary
-------
The JsCompressor is used for creating the client library for RAP [1].  It
reads JavaScript files from the project org.eclipse.rap.rwt, compresses and
concatenates them, and writes the resulting JavaScript file back into the
project as client.js.  The list of files to be included is taken from the
class QooxdooResourcesUtil.

Usage
-----
Place this project into the same workspace as the org.eclipse.rap.rwt
project.  Use the included launch configuration JSCompressor.launch to create
a new client.js. This launch configuration calls JsCompressor as a plain
application, passing the root directory of the rwt project as parameter.
Remember to refresh (F5) the rwt project when the compressor is done.

_Important:_ For RAP 1.4, checkout the 1.4-maintenance branch.

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
