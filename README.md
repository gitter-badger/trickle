# Trickle

[![Build Status](https://travis-ci.org/benoitlouy/trickle.svg)](https://travis-ci.org/benoitlouy/trickle)
[![codecov.io](http://codecov.io/github/benoitlouy/trickle/coverage.svg?branch=master)](http://codecov.io/github/benoitlouy/trickle?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.benoitlouy/trickle/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.benoitlouy/trickle)

## Overview

Trickle is a Scala library for creating and executing workflows.

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
val integer = string |> { _.mMap(_.toInt) }
```

Let's increment the Int.

```scala
val flow = integer |> { _.mMap(_ + 1) }
```

We then execute the flow defined above with input "41". Executing a flow returns the result of the flow execution and the final execution state which contain the result of all the steps the flow is made of.

```scala
val (result, state) = flow.execute(string -> "41")
```

The result of the flow execution is ```Success(Some(43))``` which is of type ```StepIO[Int]```

You can retrieve the result of an intermediary step by looking it up in the state returned by the execution.

```scala
state.get(integer)
```

The above statement returns ```Some(StepResult(Success(Some(41)))```

A same flow can be executed many times with different input. You can even execute the same flow concurrently as the flow in itself doesn't keep any information about the execution.

In the execution below the input String cannot be converted to an Int

```scala
val (errorResult, errorState) = flow.execute(string -> "foo")
```

The integer step failed, the failure is propagated all the way to the result of the final step.

errorResult will have the value ```Failure(NonEmptyList(java.lang.NumberFormatException))```