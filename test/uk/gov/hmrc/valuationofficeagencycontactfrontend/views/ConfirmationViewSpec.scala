/*
 * Copyright 2018 HM Revenue & Customs
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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.confirmation
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateFormatter}

class ConfirmationViewSpec extends ViewBehaviours {

  val cd = ContactDetails("c1", "c2", "c3", "c4", "c5")
  val confirmCd = ConfirmedContactDetails(cd)
  val ec = "council_tax"
  val address = PropertyAddress("a", Some("b"), "c", Some("d"), "f")
  val alternativeAddress = PropertyAddress("a", None, "c", None, "f")
  val cSub = "council_tax_home_business"
  val tellUs = TellUsMore("Hello")
  val contact = Contact(confirmCd, address, ec, cSub, tellUs.message)
  val alternativeContact = Contact(confirmCd, alternativeAddress, ec, cSub, tellUs.message)
  val date = DateFormatter.todaysDate()

  def view = () => confirmation(frontendAppConfig, contact, date, "councilTaxSubcategory")(fakeRequest, messages)

  def alternativeView = () => confirmation(frontendAppConfig, alternativeContact, date, "councilTaxSubcategory")(fakeRequest, messages)

  "Confirmation view" must {

    behave like normalPage(view, "confirmation",
      "para1",
      "enquirySummary",
      "whatHappensnext",
      "para2",
      "section.enquiryType",
      "section.yourDetails",
      "section.propertyAddress",
      "section.yourMessage",
      "section.date")

    "contain a print button " in {
      val doc = asDocument(view())
      val printButton = doc.getElementById("print-button").text()
      val href = doc.getElementById("print-button").attr("href")
      assert(printButton == messages("site.print.button"))
      assert(href == "javascript:window.print()")
    }

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
      assert(doc.toString.contains("c1 c2"))
      assert(doc.toString.contains("<br>c3"))
      assert(doc.toString.contains("<br>c5"))
    }

    "contain Back to GOVUK link " in {
      val doc = asDocument(view())
      val startAgainButton = doc.getElementById("backToGovUk").text()
      assert(startAgainButton == messages("site.govuk"))
      val govukUrl = doc.select("a[id=backToGovUk]").attr("href")
      govukUrl mustBe "http://www.gov.uk"
    }

  }
}
