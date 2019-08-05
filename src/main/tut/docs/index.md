---
layout: docs
title: Getting Started
---

## Setup

Add the dependency:

`libraryDependencies += "com.cynance" %% "alpaca-scala" % "3.0.0"`

The library requires configuration that consists of these 3 properties
* `accountKey` - The account key from the alpaca account.
* `accountSecret` - The account secret from the alpaca account.
* `isPaper` - Determines whether an account is a paper trading account.

The order the configuration properties are read are :
 * **Class Instantiation** - When the `Alpaca` class is instantiated, it can have the arguments of `accountKey`, `accountSecret` and `isPaper`.
 * **Config File** - This library will automatically pick up from an `application.conf` for example:
 ```yaml
alpaccaauth {
	accountKey : 'blah',
	accountSecret : 'asdfv',
	isPaper : 'true'
}
```
* **Env Variables** - You can also pass in system environment variables and it will pick those up.