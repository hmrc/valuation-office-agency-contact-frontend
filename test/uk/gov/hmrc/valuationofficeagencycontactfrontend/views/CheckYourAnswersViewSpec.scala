/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.i18n.{Lang, Messages}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{CheckYourAnswersHelper, DateUtil}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.check_your_answers

import java.util.Locale

class CheckYourAnswersViewSpec extends ViewBehaviours {

  private val outputSettings = new OutputSettings().prettyPrint(false)

  implicit val messagesEnglish: Messages = messagesApi.preferred(Seq(Lang(Locale.UK)))
  implicit val dateUtil: DateUtil = injector.instanceOf[DateUtil]

  val tellUs = TellUsMore("Hello")
  val cd = ContactDetails("c1", "c3", "c5")
  val propertyAddress1 = PropertyAddress("a", None, "c", None, "f")
  val userAnswers1 = new FakeUserAnswers(cd, "council_tax", "council_tax_band", "",  "", propertyAddress1, tellUs)
  val checkYourAnswersHelper1 = new CheckYourAnswersHelper(userAnswers1)

  val propertyAddress2 = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
  val userAnswers2= new FakeUserAnswers(cd, "business_rates", "", "business_rates_rateable_value",  "", propertyAddress2, tellUs)
  val checkYourAnswersHelper2 = new CheckYourAnswersHelper(userAnswers2)

  val anythingElse = Some("AnythingElse")
  val userAnswers3 = new FakeUserAnswers(cd, "council_tax", "council_tax_band", "",  "", propertyAddress1, tellUs, anythingElse)
  val checkYourAnswersHelper3 = new CheckYourAnswersHelper(userAnswers3)

  val backlinkUrl = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.TellUsMoreController.onPageLoad(NormalMode).url
  val backlinkUrlAE = uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.AnythingElseTellUsController.onPageLoad().url
  def checkYourAnswers = app.injector.instanceOf[check_your_answers]

  def view1 = () => checkYourAnswers(frontendAppConfig, Seq(AnswerSection(None, Seq(checkYourAnswersHelper1.enquiryCategory,
    checkYourAnswersHelper1.councilTaxSubcategory, checkYourAnswersHelper1.contactDetails, checkYourAnswersHelper1.propertyAddress,
    checkYourAnswersHelper1.tellUsMore()).flatten)), backlinkUrl)(fakeRequest, messages)

  def view2 = () => checkYourAnswers(frontendAppConfig, Seq(AnswerSection(None, Seq(checkYourAnswersHelper2.enquiryCategory,
    checkYourAnswersHelper2.businessRatesSubcategory, checkYourAnswersHelper2.contactDetails, checkYourAnswersHelper2.propertyAddress,
    checkYourAnswersHelper2.tellUsMore()).flatten)), backlinkUrl)(fakeRequest, messages)

  def view3 = () => checkYourAnswers(frontendAppConfig, Seq(AnswerSection(None, Seq(checkYourAnswersHelper3.enquiryCategory,
    checkYourAnswersHelper3.existingEnquiryCategory, checkYourAnswersHelper3.refNumber, checkYourAnswersHelper3.contactDetails,
    checkYourAnswersHelper3.propertyAddress, checkYourAnswersHelper3.anythingElse).flatten)), backlinkUrlAE)(fakeRequest, messages)

  "Check Your Answers view" must {

    behave like normalPage(view1, "checkYourAnswers")

    "contain a submit button with the value Submit" in {
      val doc = asDocument(view1())
      val submitButton = doc.getElementById("submit").text()
      assert(submitButton == messages("site.submit"))
    }

    "has a link marked with site.back leading to the Tell Us More Page" in {
      val doc = asDocument(view1())
      val backlinkText = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=govuk-back-link govuk-!-margin-top-0 govuk-!-margin-bottom-0]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.TellUsMoreController.onPageLoad(NormalMode).url
    }

    "contain Enquiry Type heading" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("enquiryCategory.heading")))
    }

    "contain Enquiry Details heading" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("councilTaxSubcategory.heading")))
    }

    "contain Contact Details label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("contactDetails.heading")))
    }

    "contain Property Details label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("propertyAddress.heading")))
    }

    "contain Enquiry heading" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("tellUsMore.heading")))
    }

    "contain Council Tax string if the council_tax enquiry category has been selected" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("enquiryCategory.council_tax")))
    }

    "contain councilTaxSubcategory.council_tax_band subcategory string" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("councilTaxSubcategory.council_tax_band")))
    }

    "contain enquiry message Hello" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains("Hello"))
    }

    "Given a property address with address line 2 and county None it should contain a formatted address string with <br/> interstitial" in {
      val doc = asDocument(view1()).outputSettings(outputSettings)
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>f"))
    }

    "Given a Contact Details it should contain a formatted Contact Details string with <br/> interstitial" in {
      val doc = asDocument(view1()).outputSettings(outputSettings)
      assert(doc.toString.contains("c1"))
      assert(doc.toString.contains("<br>c3"))
      assert(doc.toString.contains("<br>c5"))
    }

    "contain Business Rates string if the business_rates enquiry category has been selected" in {
      val doc = asDocument(view2())
      assert(doc.toString.contains(messages("enquiryCategory.business_rates")))
    }

    "contain businessRatesSubcategory.business_rates_rateable_value subcategory string" in {
      val doc = asDocument(view2())
      assert(doc.toString.contains(messages("businessRatesSubcategory.business_rates_rateable_value")))
    }

    "Given a property address it should contain a formatted address string with <br/> interstitial" in {
      val doc = asDocument(view2()).outputSettings(outputSettings)
      assert(doc.toString.contains("<br>b"))
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>d"))
      assert(doc.toString.contains("<br>f"))
    }

    "has a link marked with site.edit for changing the council tax subcategory option" in {
      val doc = asDocument(view1())
      val subcategoryLink = doc.getElementsByClass("change-link-1").first().attr("href")
      subcategoryLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode).url.toString
    }

    "has a link marked with site.edit for changing the contact details" in {
      val doc = asDocument(view1())
      val contactDetailsLink = doc.getElementsByClass("change-link-2").first().attr("href")
      contactDetailsLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactDetailsController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the property details" in {
      val doc = asDocument(view2())
      val contactDetailsLink = doc.getElementsByClass("change-link-3").first().attr("href")
      contactDetailsLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyAddressController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the enquiry message" in {
      val doc = asDocument(view2())
      val contactDetailsLink = doc.getElementsByClass("change-link-4").first().attr("href")
      contactDetailsLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.TellUsMoreController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the business rates subcategory option" in {
      val doc = asDocument(view2())
      val subcategoryLink = doc.getElementsByClass("change-link-1").first().attr("href")
      subcategoryLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode).url.toString
    }

    "it shouldn't have a href element with id change-link-0 so the user cannot change the enquiry type" in {
      val doc = asDocument(view1())
      assertNotRenderedById(doc, "change-link-0")
    }
  }
}
