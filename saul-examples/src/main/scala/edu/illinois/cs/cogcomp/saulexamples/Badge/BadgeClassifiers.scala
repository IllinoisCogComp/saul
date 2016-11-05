/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.Badge

import edu.illinois.cs.cogcomp.lbjava.learn.{SparseNetworkLearner, SparsePerceptron}
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._
/** Created by Parisa on 9/13/16.
  */

object BadgeClassifiers {

  object BadgeClassifier extends Learnable[String](badge) {
    def label = BadgeLabel
    override lazy val classifier = new SparsePerceptron()
    override def feature = using(BadgeFeature1)
  }
  object BadgeOppositClassifier extends Learnable[String](badge) {
    def label = BadgeOppositLabel
    override lazy val classifier = new SparsePerceptron()
    override def feature = using(BadgeFeature1)
  }

  object BadgeClassifierMulti extends Learnable[String](badge) {
    def label = BadgeLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(BadgeFeature1)
  }

  object BadgeOppositClassifierMulti extends Learnable[String](badge) {
    def label = BadgeOppositLabel
    override lazy val classifier = new SparseNetworkLearner()
    override def feature = using(BadgeFeature1)
  }
}