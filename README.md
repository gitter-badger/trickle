# Trickle

[![Build Status](https://travis-ci.org/benoitlouy/trickle.svg)](https://travis-ci.org/benoitlouy/trickle)
[![codecov.io](http://codecov.io/github/benoitlouy/trickle/coverage.svg?branch=master)](http://codecov.io/github/benoitlouy/trickle?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.benoitlouy/trickle/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.benoitlouy/trickle)

## Overview

Trickle is a Scala library for creating and executing workflows.

trickle-core depends on

* scalaz-core
* scalaz-concurrent
* shapeless
* commons-pool2

## Code samples

### Linear workflow

Let's look at how we can define a workflow and execute it.

In the example below we are going to create a workflow which takes a String as input, convert it to a Int and then increment it.


The input of the flow is a String.

```scala
val input = source[String]
```

We convert the String to an Int.
The input (and output) of a step is a ```StepIO[X]``` which is nothing more than a type alias for ```Validation[NonEmptyList[Exception], Option[X]]```. This mean than the input of step can be:

* a failure in which case we will have a list of at least one exception
* a success without a value: ```None```
* a success with a value: ```Some(e)```

For convenience, trickle provide the mMap operator which unwrap the Validation and the Option

```scala
val integer = input |> { _.mMap(_.toInt) }
```

Let's increment the Int.

```scala
val flow = integer |> { _.mMap(_ + 1) }
```

We then execute the flow defined above with input "41". Executing a flow returns the result of the flow execution and the final execution state which contain the result of all the steps the flow is made of.

```scala
val (result, state) = flow.execute(input -> "41")
```

The result of the flow execution is ```Success(Some(43))``` which is of type ```StepIO[Int]```.

You can retrieve the result of an intermediary step by looking it up in the state returned by the execution.

```scala
state.get(integer)
```

The above statement returns ```Some(StepResult(Success(Some(41)))```

A same flow can be executed many times with a different input. You can even execute the same flow concurrently as the flow in itself doesn't keep any information about the execution.

In the execution below the input String cannot be converted to an Int and the flow will fail.

```scala
val (errorResult, errorState) = flow.execute(input -> "foo")
```

The integer step failed, the failure is propagated all the way to the result of the final step.

errorResult will have the value ```Failure(NonEmptyList(java.lang.NumberFormatException))```

### Parallel worflow and concurrency

Let's create 2 flows:

* ```branch1``` which takes a ```String``` as input and append " rocks " to it.
* ```branch2``` which takes a ```String``` representing an integer, converts it to an actual ```Int`` and then multiplies it by 2.

```scala
val inputString = source[String]
val inputIntAsString = source[String]

val branch1 = inputString |> { _.mMap(_ + " rocks ") }
val branch2 = inputIntAsString |> { _.mMap(_.toInt) } |> { _.mMap(_ * 2) }
```

What we want to achieve is combine the results of these two flows into one to obtain a string made of n times the result of ```branch1```, where n is the result of ```branch2```.

To do so, we put the two branches together into a tuple and pipe it into a function taking two ```StepIO``` as input. In this case a function taking a ```StepIO[String]``` and a ```StepIO[Int]``` as input.

```scala
val flow = (branch1, branch2) |> { (s, i) => (s |@| i) { case (os, oi) => Some(os.get * oi.get) } }
```

Since ```StepIO``` is a ```scalaz.Validation``` we can use the ```|@|``` operator to check that `s` and `i` are both successes.

We can now execute the flow by doing the following:

```scala
val (result, state) = flow.execute(inputString -> "trickle", inputIntAsString -> "2")
```
This will execute `branch1` then execute `branch2` and then combine the two.

We can also execute branches at the same time by executing the flow this way:

```scala
val (result, state) = flow.executeParallel(inputString -> "trickle", inputIntAsString -> "2")
```

Trickle will automatically spawn threads to execute the branches concurrently. It will collect the results and call the function to combine them.

You can combine the results of up to 22 branches, in which case Trickle will execute the 22 branches in parallel.

The result of the above workflow executions is `Success(Some("trickle rocks trickle rocks trickle rocks trickle rocks "))`


