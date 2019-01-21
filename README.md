# Alpaca Scala

A scala library that interfaces to [alpaca.markets](https://alpaca.markets)

![Scala](https://img.shields.io/badge/scala-made--with-red.svg?logo=scala&style=for-the-badge)

## Setup

The library requires configuration that consists of these 3 propereties
* `accountKey` - The account key from the alpaca account.
* `accountSecret` - The account secret from the alpaca account.
* `isPaper` - Determines whether an account is a paper trading account.

The order the configuration properties are read are :
 * **Class Instantiation** - When the `Alpacca` class is instantiated, it can have the arguments of `accountKey`, `accountSecret` and `isPaper`.
 * **Config File** - This library will automatically pick up from an `application.conf` for example:
 ```yaml
alpaccaauth {
	accountKey : 'blah',
	accountSecret : 'asdfv',
	isPaper : 'true'
}
```
* **Env Variables** - You can also pass in system environment variables and it will pick those up.

## Usage

Here is an example of getting account information from Alpaca.

```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
val account : Future[Account] = alpaca.getAccount.unsafeToFuture()

```


## Todo

- [ ] Streaming API
- [ ] Unit Test with API (Need another paper account key?)
