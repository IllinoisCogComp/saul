package edu.illinois.cs.cogcomp.saul.classifier

/** Utility functions for various operations (e.g. training, testing, saving, etc) on multiple classifiers.
  */
object ClassifierUtils {
  val evalSeparator = "==============================================="

  object TrainClassifiers {
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner, trainInstances) =>
          println(evalSeparator)
          println("Training " + learner.getClassSimpleNameForClassifier)
          learner.learn(10, trainInstances)
      }
      println(evalSeparator)
    }

    def apply[T <: AnyRef](iter: Integer, c: (Learnable[T], Iterable[T])*) = {
      c.foreach {
        case (learner, trainInstances) =>
          println(evalSeparator)
          println("Training " + learner.getClassSimpleNameForClassifier)
          learner.learn(iter, trainInstances)
      }
      println(evalSeparator)
    }

    def apply[T <: AnyRef](iter: Integer, trainInstances: Iterable[T], c: (Learnable[T])*) = {
      c.foreach { learner =>
        println(evalSeparator)
        println("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter, trainInstances)
      }
      println(evalSeparator)
    }

    def apply(iter: Integer, c: (Learnable[_])*)(implicit d1: DummyImplicit, d2: DummyImplicit) = {
      c.foreach { learner =>
        println(evalSeparator)
        println("Training " + learner.getClassSimpleNameForClassifier)
        learner.learn(iter)
      }
      println(evalSeparator)
    }
  }

  // TODO: simplify the output type of test
  object TestClassifiers {
    def apply[T <: AnyRef](c: (Learnable[T], Iterable[T])*): Seq[Results] = {
      val testResults = c.map {
        case (learner, testInstances) =>
          println(evalSeparator)
          println("Evaluating " + learner.getClassSimpleNameForClassifier)
          learner.test(testInstances)
      }
      println(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](testInstances: Iterable[T], c: Learnable[T]*): Seq[Results] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test(testInstances)
      }
      println(evalSeparator)
      testResults
    }

    def apply(c: Learnable[_]*)(implicit d1: DummyImplicit, d2: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      println(evalSeparator)
      testResults
    }

    def apply(c: ConstrainedClassifier[_, _]*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test()
      }
      println(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](testInstances: Iterable[T], c: ConstrainedClassifier[T, _]*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): Seq[Results] = {
      val testResults = c.map { learner =>
        println(evalSeparator)
        println("Evaluating " + learner.getClassSimpleNameForClassifier)
        learner.test(testInstances)
      }
      println(evalSeparator)
      testResults
    }

    def apply[T <: AnyRef](instanceClassifierPairs: (Iterable[T], ConstrainedClassifier[T, _])*)(implicit d1: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit, d4: DummyImplicit): Seq[Results] = {
      val testResults = instanceClassifierPairs.map {
        case (testInstances, learner) =>
          println(evalSeparator)
          println("Evaluating " + learner.getClassSimpleNameForClassifier)
          learner.test(testInstances)
      }
      println(evalSeparator)
      testResults
    }
  }

  object ForgetAll {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach((x: Learnable[_]) => x.forget())
    }
  }

  object SaveClassifiers {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach((x: Learnable[_]) => x.save())
    }
  }

  object LoadClassifier {
    def apply(c: Learnable[_]*): Unit = {
      c.foreach { x =>
        val prefix = x.getClassNameForClassifier
        x.load(prefix + ".lc", prefix + ".lex")
      }
    }

    def apply(modelPath: String, c: Learnable[_]*)(implicit d1: DummyImplicit): Unit = {
      c.foreach { x =>
        val prefix = modelPath + x.getClassNameForClassifier
        x.load(prefix + ".lc", prefix + ".lex")
      }
    }
  }

  /** some utility functions for playing arounds results of classifiers */
  private def resultToList(someResult: AbsractResult): List[Double] = {
    List(someResult.f1, someResult.precision, someResult.recall)
  }

  def getAverageResults(perLabelResults: Seq[ResultPerLabel]): AverageResult = {
    val avgResultList = perLabelResults.toList.map(resultToList).transpose.map(a => a.sum / perLabelResults.length)
    AverageResult(avgResultList(0), avgResultList(1), avgResultList(2))
  }
}

/** basic data structure to keep the results */
abstract class AbsractResult() {
  def f1: Double
  def precision: Double
  def recall: Double
}
case class ResultPerLabel(label: String, val f1: Double, val precision: Double, val recall: Double,
  val allClasses: Array[String], val labeledSize: Int, val predictedSize: Int, val correctSize: Int) extends AbsractResult
case class OverallResult(val f1: Double, val precision: Double, val recall: Double) extends AbsractResult
case class AverageResult(val f1: Double, val precision: Double, val recall: Double) extends AbsractResult

case class Results(perLabel: Seq[ResultPerLabel], average: AverageResult, overall: OverallResult)
