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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views.includes.sidelinks

import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.SidelinkBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.includes.sidelinks.councilTax



class CouncilTaxViewSpec extends SidelinkBehaviours {

  def view = () => councilTax()(messages)

  "Council Tax view" must {

    behave like normalPage(view, "councilTax", "title", "information", "taxband-url", "taxband-title", "taxband-information",
      "taxband-changes.url", "taxband-changes.title", "taxband-changes.information", "taxband-assessment.url",
      "taxband-assessment.title", "taxband-assessment.information", "pay-url", "pay-title", "pay-information"
    )
  }

}
