package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}
/**
 * Created by nishyu on 2014/06/14.
 */
class FormattedNumberTest extends FlatSpec with Matchers{

  "FormattedNumber" should "have toString to be formatted according to its decimal point" in {
    FormattedNumber(98.051, 2).toString shouldEqual "98.05"
  }

  "FormattedNumber" should "have hashCode generated from toString" in {
    FormattedNumber(98.051, 2).hashCode shouldEqual "98.05".hashCode
  }

  "FormattedNumber" should "be equal if toString() is equal" in {
    val a = FormattedNumber( 98.051, 2 );
    val b = FormattedNumber( 98.052, 2 );
    val c = FormattedNumber( 98.055, 2 );
    a shouldEqual b
    a shouldNot equal (c)
  }
}
