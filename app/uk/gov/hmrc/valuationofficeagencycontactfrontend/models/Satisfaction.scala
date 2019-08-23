/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

//import models.Satisfaction

//class Satisfaction {
//
//}

sealed trait Satisfaction extends NamedEnum {
  val key = "satisfactionSurveyType"
  val rating: Int
}

trait NamedEnum {
  def name:String
  def key:String
  def msgKey: String = s"$key.$name"
}

trait NamedEnumSupport[E <: NamedEnum]{

  def all:List[E]

  def fromName(name: String): Option[E] = {
    all.find { _.name.equalsIgnoreCase(name) }
  }
}

object VerySatisfied extends Satisfaction {
  val name = "verySatisfied"
  val rating = 5
}

object Satisfied extends Satisfaction {
  val name = "satisfied"
  val rating = 4
}

object Neither extends Satisfaction {
  val name = "neither"
  val rating = 3
}

object Dissatisfied extends Satisfaction {
  val name = "dissatisfied"
  val rating = 2
}

object VeryDissatisfied extends Satisfaction {
  val name = "veryDissatisfied"
  val rating = 1
}

object SatisfactionTypes extends NamedEnumSupport[Satisfaction] {
  val all = List(VerySatisfied, Satisfied, Neither, Dissatisfied, VeryDissatisfied)
}
