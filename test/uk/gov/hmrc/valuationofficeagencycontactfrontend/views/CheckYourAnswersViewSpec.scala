/*
 * Copyright 2017 HM Revenue & Customs
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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.CheckYourAnswersHelper
import uk.gov.hmrc.valuationofficeagencycontactfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.check_your_answers

class CheckYourAnswersViewSpec extends ViewBehaviours {

  val tellUs = TellUsMore("Hello")
  val cd = ContactDetails("c1", "c2", "c3", "c4", "c5")
  val propertyAddress1 = PropertyAddress("a", None, "c", None, "f")
  val userAnswers1 = new FakeUserAnswers(cd, "council_tax", "council_tax_band", "", propertyAddress1, tellUs)
  val checkYourAnswersHelper1 = new CheckYourAnswersHelper(userAnswers1)

  val propertyAddress2 = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
  val userAnswers2= new FakeUserAnswers(cd, "business_rates", "", "business_rates_rateable_value", propertyAddress2, tellUs)
  val checkYourAnswersHelper2 = new CheckYourAnswersHelper(userAnswers2)

  def view1 = () => check_your_answers(frontendAppConfig, Seq(AnswerSection(None, Seq(checkYourAnswersHelper1.enquiryCategory,
    checkYourAnswersHelper1.councilTaxSubcategory, checkYourAnswersHelper1.contactDetails, checkYourAnswersHelper1.propertyAddress,
    checkYourAnswersHelper1.tellUsMore).flatten)))(fakeRequest, messages)

  def view2 = () => check_your_answers(frontendAppConfig, Seq(AnswerSection(None, Seq(checkYourAnswersHelper2.enquiryCategory,
    checkYourAnswersHelper2.businessRatesSubcategory, checkYourAnswersHelper2.contactDetails, checkYourAnswersHelper2.propertyAddress,
    checkYourAnswersHelper2.tellUsMore).flatten)))(fakeRequest, messages)

  "Check Your Answers view" must {

    behave like normalPage(view1, "checkYourAnswers", "guidance", "subheading")

    "contain a submit button with the value Submit" in {
      val doc = asDocument(view1())
      val submitButton = doc.getElementById("submit").text()
      assert(submitButton == messages("site.submit"))
    }

    "has a link marked with site.back leading to the Contact Details Page" in {
      val doc = asDocument(view1())
      val backlinkText = doc.select("a[class=link-back]").text()
      backlinkText mustBe messages("site.back")
      val backlinkUrl = doc.select("a[class=link-back]").attr("href")
      backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactDetailsController.onPageLoad(NormalMode).url
    }

    "contain Enquiry Type label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("enquiryCategory.checkYourAnswersLabel")))
    }

    "contain Enquiry Details label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("councilTaxSubcategory.checkYourAnswersLabel")))
    }

    "contain Contact Details label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("contactDetails.checkYourAnswersLabel")))
    }

    "contain Property Details label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("propertyAddress.checkYourAnswersLabel")))
    }

    "contain Enquiry label" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains(messages("tellUsMore.checkYourAnswersLabel")))
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
      val doc = asDocument(view1())
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>f"))
    }

    "Given a Contact Details it should contain a formatted Contact Details string with <br/> interstitial" in {
      val doc = asDocument(view1())
      assert(doc.toString.contains("c1 c2"))
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
      val doc = asDocument(view2())
      assert(doc.toString.contains("<br>b"))
      assert(doc.toString.contains("<br>c"))
      assert(doc.toString.contains("<br>d"))
      assert(doc.toString.contains("<br>f"))
    }

    "has a link marked with site.edit for changing the council tax subcategory option" in {
      val doc = asDocument(view1())
      val subcategoryLink = doc.getElementById("change-link-1").attr("href")
      subcategoryLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.CouncilTaxSubcategoryController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the contact details" in {
      val doc = asDocument(view1())
      val contactDetailsLink = doc.getElementById("change-link-2").attr("href")
      contactDetailsLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.ContactDetailsController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the property details" in {
      val doc = asDocument(view2())
      val contactDetailsLink = doc.getElementById("change-link-3").attr("href")
      contactDetailsLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.PropertyAddressController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the enquiry message" in {
      val doc = asDocument(view2())
      val contactDetailsLink = doc.getElementById("change-link-4").attr("href")
      contactDetailsLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.TellUsMoreController.onPageLoad(CheckMode).url.toString
    }

    "has a link marked with site.edit for changing the business rates subcategory option" in {
      val doc = asDocument(view2())
      val subcategoryLink = doc.getElementById("change-link-1").attr("href")
      subcategoryLink mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.BusinessRatesSubcategoryController.onPageLoad(CheckMode).url.toString
    }

    "it shouldn't have a href element with id change-link-0 so the user cannot change the enquiry type" in {
      val doc = asDocument(view1())
      assertNotRenderedById(doc, "change-link-0")
    }
  }
}
