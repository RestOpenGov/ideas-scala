package test.matchers

import org.specs2.mutable._

import org.specs2.matcher.{Matcher, MatchResult, Expectable}
import org.specs2.execute.{Result, Failure}

  trait ErrorSpec extends Specification {

    def haveError[T] = new ErrorMatcher[T]

  implicit def errorMatcherToErrorListMatcher[T](m : ErrorMatcher[T]): ErrorListMatcher[T] =
    new ErrorListMatcher[T](m)

  class ErrorListMatcher[T](m: Matcher[Either[List[T], _]]) {
    def containing(t: T) =
    // the 'contain' matcher is adapted to take in an 
    // Either[List[T], _] and work on its left part
    m and contain(t) ^^ ((e: Either[List[T], _]) => e.left.toOption.get)
  }

  class ErrorMatcher[T] extends Matcher[Either[List[T], _]] {
    def apply[S <: Either[List[T], _]](value: Expectable[S]) = {
      result(value.value.left.toOption.isDefined,
             value.description + " is Left",
             value.description + " is not Left",
             value)
    }

    def like[U](f: PartialFunction[T, MatchResult[U]]) = {
      this and partialMatcher(f)
    }
    private def partialMatcher[U](f: PartialFunction[T, MatchResult[U]]) = 
      new Matcher[Either[List[T], _]] {

      def apply[S <: Either[List[T], _]](value: Expectable[S]) = {
        // get should always work here because it comes after the "and"
        val errors = value.value.left.toOption.get
        val res = atLeastOnceWhen[T, U](errors)(f)
        result(res.isSuccess,
               value.description+" is Left[T] and "+res.message,
               value.description+" is Left[T] but "+res.message,
               value)
      }
    }
  }

}

