
# http4s

### Functional HTTP client and Server

---

## Content

- cats.effect
- EitherT
- http4s server
- ~~circe encoder / decoder~~
- ~~http4s client~~

---

## cats.effect
<style type="text/css">
  .reveal p {
    text-align: left;
  }
  .reveal ul {
    display: block;
  }
  .reveal ol {
    display: block;
  }
</style>

Something different than:
- Javascript Promises
- Scala's future

Something similar to:
- fs2.task
- scalaz's ioeffect

Lazy and asynchronous execution (Monad ➡ `flatMap`!)

---

## cats.effect

```
def armMissile(/* ... */): IO[ArmedMissile] = {
  // ... check launch code
  // ... notify POTUS
  // ... change missile state
}

def launch(missile: ArmedMissile): IO[Unit] = {
  // ... ignite the missile
}

armMissile("missileA").flatMap(launch)

```

---

## cats.effect

Creation
```
IO.pure(42) // => : IO[Int]
IO.pure(println("fooooo")) // don't do this
IO(println("foooo"))
```

---

## cats.effect

Example

```
def add(t: (Int, Int)) = IO(t._1 + t._2)

val inputs = NonEmptyList.of((1, 2), (3, 4))
val listOfIO = inputs.map(t => add(t)) // => : NonEmptyList[IO[Int]]
val singleIO = list.parSequence // => : IO[NonEmptyList[Int]]

val result = singleTask.unsafeRunSync() // => : NonEmptyList[Int]
```

---

## cats.effect

```
import cats.effect._
import cats.syntax.all._
import scala.concurrent.duration._

def retryWithBackoff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)
  (implicit timer: Timer[IO]): IO[A] = {

  ioa.handleErrorWith { error =>
    if (maxRetries > 0)
      IO.sleep(initialDelay) *> retryWithBackoff(ioa, initialDelay * 2, maxRetries - 1)
    else
      IO.raiseError(error)
  }
}
```


---

## http4s server

```
import cats.effect._, org.http4s._, org.http4s.dsl.io._, 
import scala.concurrent.ExecutionContext.Implicits.global

val helloWorldService = HttpService[IO] {
  case GET -> Root / "hello" / name =>
    Ok(s"Hello, $name.")
}

```

---

## Learn more

- [RevealJS Demo/Manual](http://lab.hakim.se/reveal-js)
- [RevealJS Project/README](https://github.com/hakimel/reveal.js)
- [GitHub Flavored Markdown](https://help.github.com/articles/github-flavored-markdown)
