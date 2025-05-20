/*
 * Copyright 2025 HM Revenue & Customs
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

import org.jsoup.nodes.Document.OutputSettings
import uk.gov.hmrc.valuationofficeagencycontactfrontend.forms.SatisfactionSurveyForm
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.{confirmation => Confirmation}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.{AnswerRow, AnswerSection}
import play.twirl.api.HtmlFormat

class ConfirmationViewSpec extends ViewBehaviours {

  private val outputSettings = new OutputSettings().prettyPrint(false)

  val contactDetails: ContactDetails      = ContactDetails("c1", "c3", "c5")
  val councilTax                          = "council_tax"
  val address: PropertyAddress            = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
  val alternativeAddress: PropertyAddress = PropertyAddress("a", None, "c", None, "f")
  val cSub                                = "council_tax_home_business"
  val tellUs: TellUsMore                  = TellUsMore("Hello")
  val contact: Contact                    = Contact(contactDetails, address, councilTax, cSub, tellUs.message)
  val alternativeContact: Contact         = Contact(contactDetails, alternativeAddress, councilTax, cSub, tellUs.message)
  val whatHappensNew: Seq[String]         = Seq("confirmation.new.p1")

  val answerSectionNew: AnswerSection = AnswerSection(
    None,
    List(
      AnswerRow("enquiryCategory.checkYourAnswersLabel", "enquiryCategory.council_tax", true, ""),
      AnswerRow("contactDetails.heading", "c1<br>c3<br>c5", false, ""),
      AnswerRow("propertyAddress.heading", "a<br>b<br>c<br>d<br>f", false, ""),
      AnswerRow("tellUsMore.checkYourAnswersLabel", "some message", false, "")
    )
  )

  def confirmation: html.confirmation = app.injector.instanceOf[Confirmation]

  def view: () => HtmlFormat.Appendable =
    () => confirmation(frontendAppConfig, contact, answerSectionNew, whatHappensNew, SatisfactionSurveyForm.apply())(using fakeRequest, messages)

  def alternativeView: () => HtmlFormat.Appendable =
    () => confirmation(frontendAppConfig, alternativeContact, answerSectionNew, whatHappensNew, SatisfactionSurveyForm.apply())(using fakeRequest, messages)

  "Confirmation view" must {

    behave like normalPage(
      view,
      "confirmation",
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
      val doc = asDocument(alternativeView()).outputSettings(outputSettings)
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>f"))
    }

    "Given a property address it should contain a formatted address string with <br/> interstitial" in {
      val doc = asDocument(view()).outputSettings(outputSettings)
      assert(doc.toString.contains("<br>b"))
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>d"))
      assert(doc.toString.contains("<br>f"))
    }

    "Given a Contact Details it should contain a formatted Contact Details string with <br/> interstitial" in {
      val doc = asDocument(view()).outputSettings(outputSettings)
      assert(doc.toString.contains("c1"))
      assert(doc.toString.contains("<br>c3"))
      assert(doc.toString.contains("<br>c5"))
    }

  }
}
