package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}

class FormattedNumberTest extends FlatSpec with Matchers {

  "FormattedNumber" should "have toString to be formatted according to its decimal point" in {
    FormattedNumber(98.051, 2).toString shouldEqual "98.05"
  }

  it should "have hashCode generated from toString" in {
    FormattedNumber(98.051, 2).hashCode shouldEqual "98.05".hashCode
  }

  it should "be equal if toString() is equal" in {
    //98.05 = 98.05
    FormattedNumber(98.051, 2) shouldEqual FormattedNumber(98.052, 2)
    //98.05 != 98.06
    FormattedNumber(98.051, 2) should not equal (FormattedNumber(98.055, 2))
  }

  it should "be equal for zeros (special equality on zeros)" in {
    //0 == 0.0
    FormattedNumber(0, 0) shouldEqual FormattedNumber(0, 1)
    //0.000 == - 0.00000
    FormattedNumber(0, 3) shouldEqual FormattedNumber(-0.000001, 5)
  }

  it should "not be equal if decimal points are different" in {
    FormattedNumber(98.05, 2) should not equal (FormattedNumber(98.05, 3))
  }

  it should "take a negative number in the 1st argument" in {
    FormattedNumber(-1, 5).toString shouldEqual "-1.00000"
  }

  it should "display 0.00.. by toString method if number is zero" in {
    FormattedNumber(-0.001, 2).toString shouldEqual "0.00"
  }

  it should "perform + operation" in {
    //50.05 + 0.00 = 50.05
    FormattedNumber(50.051, 2) + FormattedNumber(0, 2) shouldEqual FormattedNumber(50.05, 2)
    //50.05 + 50.05 = 100.10
    FormattedNumber(50.051, 2) + FormattedNumber(50.052, 2) shouldEqual FormattedNumber(100.10, 2)
    //50.05 + 50.05 + 50.05 = 150.15
    FormattedNumber(50.054, 2) + FormattedNumber(50.054, 2) + FormattedNumber(50.054, 2) shouldEqual FormattedNumber(150.15, 2)
  }

  it should "perform + operation, which takes the max decimal point of the two" in {
    //10.051 + 100.05 = 110.101
    FormattedNumber(10.051, 3) + FormattedNumber(100.052, 2) shouldEqual FormattedNumber(110.101, 3)
    //10.05 + 100.052 = 110.102
    FormattedNumber(10.051, 2) + FormattedNumber(100.052, 3) shouldEqual FormattedNumber(110.102, 3)
  }

  it should "perform + operation on negative numbers too" in {
    //50.05 + 0.00 = 50.05
    FormattedNumber(50.051, 2) + FormattedNumber(-0, 2) shouldEqual FormattedNumber(50.05, 2)
    //10.05 - 5.05 = -5.00
    FormattedNumber(10.051, 2) + FormattedNumber(-5.054, 2) shouldEqual FormattedNumber(5, 2)
    //50.05 - 50.05 = 0.00
    FormattedNumber(50.051, 2) + FormattedNumber(-50.052, 2) shouldEqual FormattedNumber(0, 2)
    //50.05 - 50.051 = -0.01
    FormattedNumber(50.051, 2) + FormattedNumber(-50.051, 3) shouldEqual FormattedNumber(-0.001, 3)
  }

  it should "compare two FormattedNumbers" in {
    //0.1 < 0.2
    FormattedNumber(0.1, 1) should be < FormattedNumber(0.2, 1)
    //0.1 > -0.2
    FormattedNumber(0.1, 1) should be > FormattedNumber(-0.2, 1)
    //0.1 < 0.13, tricky case!
    FormattedNumber(0.14, 1) should be < FormattedNumber(0.13, 2)
    //0.1 == 0.1
    FormattedNumber(0.1, 1) should not be > (FormattedNumber(0.1, 1))
    FormattedNumber(0.1, 1) should not be < (FormattedNumber(0.1, 1))
    FormattedNumber(0.1, 1) should be >= (FormattedNumber(0.1, 1))
    FormattedNumber(0.1, 1) should be <= (FormattedNumber(0.1, 1))
    //0.10 == 0.1
    FormattedNumber(0.1, 2) should not be > (FormattedNumber(0.1, 1))
    FormattedNumber(0.1, 2) should not be < (FormattedNumber(0.1, 1))
    FormattedNumber(0.1, 2) should be >= (FormattedNumber(0.1, 1))
    FormattedNumber(0.1, 2) should be <= (FormattedNumber(0.1, 1))
  }

}
