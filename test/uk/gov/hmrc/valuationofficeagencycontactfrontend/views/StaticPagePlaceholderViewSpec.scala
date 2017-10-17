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

import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.NormalMode
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.html.staticPagePlaceholder


class StaticPagePlaceholderViewSpec extends ViewBehaviours {

  def view = () => staticPagePlaceholder(frontendAppConfig)(fakeRequest, messages)

  "StaticPagePlaceholder view" must {

    behave like normalPage(view, "staticPagePlaceholder", "title", "heading", "paragraph", "royalmail-url", "royalmail-url.title",
      "nopostcode-url", "continuebutton", "back-title")
  }

  "has a link marked with site.back leading to the enquiry category page" in {
    val doc = asDocument(view())
    val backlinkText = doc.select("a[class=link-back]").text()
    backlinkText mustBe messages("site.back")
    val backlinkUrl = doc.select("a[class=link-back]").attr("href")
    backlinkUrl mustBe uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.routes.EnquiryCategoryController.onPageLoad(NormalMode).url
  }
}
