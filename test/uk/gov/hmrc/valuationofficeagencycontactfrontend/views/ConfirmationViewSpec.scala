/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views

import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.SatisfactionSurveyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.DateFormatter
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.{AnswerRow, AnswerSection}

class ConfirmationViewSpec extends ViewBehaviours {

  val contactDetails = ContactDetails("c1", "c3", "c5")
  val councilTax = "council_tax"
  val address = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
  val alternativeAddress = PropertyAddress("a", None, "c", None, "f")
  val cSub = "council_tax_home_business"
  val tellUs = TellUsMore("Hello")
  val contact = Contact(contactDetails, address, councilTax, cSub, tellUs.message)
  val alternativeContact = Contact(contactDetails, alternativeAddress, councilTax, cSub, tellUs.message)
  val date = DateFormatter.todaysDate()
  val whatHappensNew = Seq("confirmation.new.p1")
  val answerSectionNew = AnswerSection(None, List(
    AnswerRow("enquiryCategory.checkYourAnswersLabel", "enquiryCategory.council_tax", true, ""),
    AnswerRow("contactDetails.heading", "c1<br>c3<br>c5", false, ""),
    AnswerRow("propertyAddress.heading", "a<br>b<br>c<br>d<br>f", false, ""),
    AnswerRow("tellUsMore.checkYourAnswersLabel", "some message", false, "")))

  def confirmation = app.injector.instanceOf[Confirmation]

  def view = () => confirmation(frontendAppConfig, contact, answerSectionNew, whatHappensNew, SatisfactionSurveyForm.apply())(fakeRequest, messages)

  def alternativeView = () => confirmation(frontendAppConfig, alternativeContact, answerSectionNew, whatHappensNew, SatisfactionSurveyForm.apply())(fakeRequest, messages)

  "Confirmation view" must {

    behave like normalPage(view, "confirmation",
      "para1",
      "enquirySummary",
      "whatHappensnext",
      "title",
      "heading",
      "enquirySummary",
      "para1",
      "new.p1",
      "feedback.subheading",
      "feedback.improve",
      "feedback.warning"
    )

    "Given a property address with address line 2 and county as None it should contain a formatted address string with <br/> interstitial" in {
      val doc = asDocument(alternativeView())
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>f"))
    }

    "Given a property address it should contain a formatted address string with <br/> interstitial" in {
      val doc = asDocument(view())
      assert(doc.toString.contains("<br>b"))
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>d"))
      assert(doc.toString.contains("<br>f"))
    }

    "Given a Contact Details it should contain a formatted Contact Details string with <br/> interstitial" in {
      val doc = asDocument(view())
      assert(doc.toString.contains("c1"))
      assert(doc.toString.contains("<br>c3"))
      assert(doc.toString.contains("<br>c5"))
    }

  }
}
