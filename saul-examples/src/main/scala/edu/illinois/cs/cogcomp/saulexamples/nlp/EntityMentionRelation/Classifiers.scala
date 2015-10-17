package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saul.classifier.{ ConstrainedClassifier, Learnable }
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.Attribute
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.{ ConllRawSentence, ConllRawToken, ConllRelation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.constrains._

/** Created by haowu on 1/27/15.
  */
object classifiers {

  import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.entityRelationDataModel._
  // TODO : Write the type conversion
  //  val orgFeature = List(pos,entityType)

  object orgClassifier extends Learnable[ConllRawToken](entityRelationDataModel) {

    def label: Attribute[ConllRawToken] = entityType is "Org"
    //TODO add test units with explicit feature definition and remove these lines.
    //override def feature = using(
    //word,phrase,containsSubPhraseMent,containsSubPhraseIng,
    // containsInPersonList,wordLen,containsInCityList
    // )
  }

  object PersonClassifier extends Learnable[ConllRawToken](entityRelationDataModel) {

    def label: Attribute[ConllRawToken] = entityType is "Peop"
    override def feature = using(
      windowWithIn[ConllRawSentence](-2, 2, List(
        pos
      )), word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList
    )

  }

  object LocClassifier extends Learnable[ConllRawToken](entityRelationDataModel) {

    def label: Attribute[ConllRawToken] = entityType is "Loc"
    override def feature = using(
      windowWithIn[ConllRawSentence](-2, 2, List(
        pos
      )), word, phrase, containsSubPhraseMent, containsSubPhraseIng,
      containsInPersonList, wordLen, containsInCityList
    )

  }

  val ePipe = discreteAttributesGeneratorOf[ConllRelation]('e1pipe) {
    rel =>
      {
        "e1-org: " + orgClassifier.discreteValue(rel.e1) ::
          "e1-per: " + PersonClassifier.discreteValue(rel.e1) ::
          "e1-loc: " + LocClassifier.discreteValue(rel.e1) ::
          "e2-org: " + orgClassifier.discreteValue(rel.e1) ::
          "e2-per: " + PersonClassifier.discreteValue(rel.e1) ::
          "e2-loc: " + LocClassifier.discreteValue(rel.e1) ::
          Nil
      }
  }

  object workForClassifier extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Work_For"

    override def feature = using(
      relFeature, relPos //,ePipe
    )
  }

  object workForClassifierPipe extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Work_For"

    override def feature = using(
      relFeature, relPos, ePipe
    )
  }

  object LivesInClassifier extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Live_In"
    override def feature = using(
      relFeature, relPos
    )
  }

  object LivesInClassifierPipe extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Live_In"
    override def feature = using(
      relFeature, relPos, ePipe
    )
  }

  object org_baseClassifier extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "OrgBased_In"
  }
  object locatedInClassifier extends Learnable[ConllRelation](entityRelationDataModel) {
    override def label: Attribute[ConllRelation] = relationType is "Located_In"
  }

  object orgConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](entityRelationDataModel, orgClassifier) {
    def subjectTo = Per_Org
    override val pathToHead = Some('containE2)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId1
  }

  object PerConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](entityRelationDataModel, PersonClassifier) {

    def subjectTo = Per_Org
    override val pathToHead = Some('containE1)
    override def filter(t: ConllRawToken, h: ConllRelation): Boolean = t.wordId == h.wordId2
  }

  object LocConstraintClassifier extends ConstrainedClassifier[ConllRawToken, ConllRelation](entityRelationDataModel, LocClassifier) {

    def subjectTo = Per_Org
    override val pathToHead = Some('containE2)
    //TODO add test unit for this filter
    //    override def filter(t: ConllRawToken,h:ConllRelation): Boolean = t.wordId==h.wordId2
  }

  object P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](entityRelationDataModel, workForClassifier) {
    def subjectTo = Per_Org
  }

  object LiveIn_P_O_relationClassifier extends ConstrainedClassifier[ConllRelation, ConllRelation](entityRelationDataModel, LivesInClassifier) {
    def subjectTo = Per_Org
  }
}

