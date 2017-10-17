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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.confirmationCouncilTax
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models._
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.{DateFormatter}

class ConfirmationCouncilTaxViewSpec extends ViewBehaviours {

    val cd = ContactDetails("c1", "c2", "c3", "c4", "c5")
    val confirmCd = ConfirmedContactDetails(cd)
    val ec = "council_tax"
    val ct = Some(CouncilTaxAddress("a", "b", "c", "d", "f"))
    val cSub = "council_tax_home_business"
    val tellUs = TellUsMore("Hello")
    var contact = Contact(confirmCd, ct, None, ec, cSub, tellUs.message)
    val date = DateFormatter.todaysDate()

    def view = () => confirmationCouncilTax(frontendAppConfig, contact, date)(fakeRequest, messages)

   "ConfirmationCouncilTax view" must {

    behave like normalPage(view, "confirmation", "reference.ct", "enquirySummary", "whatHappensnext",  "para1", "para2", "para3", "section.enquiryType", "section.yourDetails", "section.propertyAddress" , "section.yourMessage")

    "contain a print button " in {
      val doc = asDocument(view())
      val printButton = doc.getElementById("print-button").text()
      val href = doc.getElementById("print-button").attr("href")
      assert(printButton == messages("site.print.button"))
      assert(href == "javascript:window.print()")
    }

     "Given a council tax address it should contain a formatted address string" in {
       val doc = asDocument(view())
       assert(doc.toString.contains("a, b, c, d, f"))
     }

     "Given a council tax address it should contain a formatted address string with <br/> interstitial" in {
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

     "contain start again button " in {
       val doc = asDocument(view())
       val startAgainButton = doc.getElementById("start-again").text()
       assert(startAgainButton == messages("site.start-again"))
     }

     "The Start again button links to the Index Controller onPageLoadWithNewSession method" in {
       val doc = asDocument(view())
       val href = doc.getElementById("start-again").attr("href")
       assert(href == uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.IndexController.onPageLoadWithNewSession().url.toString)
     }
  }
}

