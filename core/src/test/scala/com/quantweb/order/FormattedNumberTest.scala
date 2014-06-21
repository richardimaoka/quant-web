package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}

class FormattedNumberTest extends FlatSpec with Matchers {

  "FormattedNumber" should "have toString to be formatted according to its decimal point" in {
    FormattedNumber(98.051, 2).toString shouldEqual "98.05"
  }

  "FormattedNumber" should "have hashCode generated from toString" in {
    FormattedNumber(98.051, 2).hashCode shouldEqual "98.05".hashCode
  }

  "FormattedNumber" should "be equal if toString() is equal" in {
    //98.05 = 98.05
    FormattedNumber(98.051, 2) shouldEqual FormattedNumber(98.052, 2);
    //98.05 != 98.06
    FormattedNumber(98.051, 2) should not equal (FormattedNumber(98.055, 2));
  }

  "FormattedNumber" should "not be equal if decimal points are different" in {
    FormattedNumber(98.05, 2) should not equal (FormattedNumber(98.05, 3));
  }

  "FormattedNumber" should "perform + operation" in {
    //50.05 + 50.05 = 100.10
    FormattedNumber(50.051, 2) + FormattedNumber(50.052, 2) shouldEqual FormattedNumber(100.10, 2);
    //50.05 + 50.05 + 50.05 = 150.15
    FormattedNumber(50.054, 2) + FormattedNumber(50.054, 2) + FormattedNumber(50.054, 2) shouldEqual FormattedNumber(150.15, 2);
  }

  "FormattedNumber" should "take the max decimal point of the two" in {
    //10.051 + 100.05 = 110.101
    FormattedNumber(10.051, 3) + FormattedNumber(100.052, 2) shouldEqual FormattedNumber(110.101, 3);
    //10.05 + 100.052 = 110.102
    FormattedNumber(10.051, 2) + FormattedNumber(100.052, 3) shouldEqual FormattedNumber(110.102, 3);
  }
}
