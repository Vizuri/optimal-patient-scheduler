ng-prettyjson [![NPM version](https://badge.fury.io/js/ng-prettyjson.png)](http://badge.fury.io/js/ng-prettyjson) [![Build Status](https://travis-ci.org/darul75/ng-prettyjson.png?branch=master)](https://travis-ci.org/darul75/ng-prettyjson) [![Total views](https://sourcegraph.com/api/repos/github.com/darul75/ng-prettyjson/counters/views.png)](https://sourcegraph.com/github.com/darul75/ng-prettyjson) [![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/darul75/ng-prettyjson/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
=====================

Angular directive for JSON pretty display output, indent and colorized.

Idea was given by the need to display some configuration JSON format files in a back office.

Inspired by this from stackoverflow
[pretty json javascript](http://stackoverflow.com/questions/4810841/json-pretty-print-using-javascript)

Demo
------------
http://darul75.github.io/ng-prettyjson/


Screenshot
------------
![pretty json screenshot](http://darul75.github.io/ng-prettyjson/images/capture.png "pretty json screenshot")

Installation
------------

Using [Bower](http://bower.io):

```
bower install ng-prettyjson
```

How to use it
-------------

You should already have script required for Angular

```html
<script type="text/javascript" src="angular.min.js"></script>
```

to the list above, you should add:

```html
<link rel="stylesheet" type="text/css" href="ng-prettyjson.min.css">
```

```html
<script type="text/javascript" src="ng-prettyjson.min.js"></script>
```

Then, require `ngPrettyJson` in your application module:

```javascript
angular.module('myApp', ['ngPrettyJson']);
```

and then just add a `pre` with `pretty-json` directive:

```html
<pre pretty-json="jsonObj" />
```

`jsonObj` is a variable on the scope to be output as JSON:

```javascript
$scope.jsonObj = {a:1, 'b':'foo', c:[false,null, {d:{e:1.3e5}}]};
```

### Tag Usage

Alternatively, you can use a `<pretty-json>` tag.  This tag will be replaced with a `<pre>`:

```html
<pretty-json json="jsonObj"></pretty-json>
```

### Build

You can run the tests by running

```
npm install
```
and
```
npm test
```

assuming you already have `grunt` installed, otherwise you also need to do:

```
npm install -g grunt-cli
```

## License

The MIT License (MIT)

Copyright (c) 2013 Julien Valéry

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.




