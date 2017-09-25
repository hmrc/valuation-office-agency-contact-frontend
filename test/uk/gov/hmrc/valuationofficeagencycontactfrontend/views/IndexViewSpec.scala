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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.index

class IndexViewSpec extends ViewBehaviours {

  def view = () => index(frontendAppConfig)(fakeRequest, messages)

  "Index view" must {

    behave like normalPage(view, "index", "general-information", "contact-guidance", "council-title", "business-title",
      "housing-title", "lettings-title", "righttobuy-title", "dvs-title", "questions-part1",
      "questions-part2", "questions-part3", "council-url", "start-button", "agents-part1", "agents-part2", "agents-part3",
      "voamaps-url", "scotlandNI-title", "scotlandNI-information", "scotland-url", "scotland-assessors.title", "scotland-assessors.end",
      "ni-url", "ni-part1", "ni-part2")
  }
}
