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
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.error.error_template

class ErrorTemplateViewspec extends ViewBehaviours {

  def errorTemplate = app.injector.instanceOf[error_template]

    def view = () => errorTemplate(
      messages("global.error.badRequest400.title"),
      messages("global.error.badRequest400.heading"),
      messages("global.error.badRequest400.message"))(fakeRequest, messages)

    "error template view" must {
      behave like normalPage(view, "global.error.badRequest400",
        "title",
        "heading",
        "message"
      )

  }
}
