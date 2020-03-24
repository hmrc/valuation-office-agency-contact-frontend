/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.internalServerError

class InternalServerErrorViewSpec extends ViewBehaviours {

  def view = () => internalServerError(frontendAppConfig)(fakeRequest, messages)

  "Internal Server Error view" must {

    behave like normalPage(view, "error500", "tryagain.para", "contact.para", "phone-title", "england", "england.phone-number",
      "wales", "wales.phone-number", "opening-times.title", "opening-times.paragraph", "call-charges.title", "call-charges.url",
      "england.phone-number.url", "wales.phone-number.url")
  }
}
