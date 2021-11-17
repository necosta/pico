package com.necosta.pico

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

class HelloWorldSpec extends Properties("String") {

  property("startsWith") = forAll { (a: String, b: String) =>
    (a + b).startsWith(a)
  }
}
