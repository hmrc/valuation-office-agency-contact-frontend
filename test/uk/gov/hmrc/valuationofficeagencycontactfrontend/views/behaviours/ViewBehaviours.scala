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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.views.behaviours

import java.util.Locale

import org.scalatest.exceptions.TestFailedException
import play.api.i18n.Lang
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.valuationofficeagencycontactfrontend.views.ViewSpecBase

import scala.util.Try

trait ViewBehaviours extends ViewSpecBase {

  def labelDefinedAndUsedOnce(option: String, prefix: String, view: () => HtmlFormat.Appendable) = {
    val doc = asDocument(view())
    assert(messages.isDefinedAt(s"$prefix.$option"))
    val label = doc.select(s"label[for=$prefix.$option]")
    assert(label.size() == 1)
  }

  def normalPage(view: () => HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 expectedGuidanceKeys: String*) = {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view())
          val nav = Option {
            doc.getElementById("proposition-menu")
          }.getOrElse(
            doc.getElementsByAttributeValue("class", "govuk-header__link govuk-header__link--service-name").first().parent()
          )
          val span = nav.children.first
          span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsMessage(doc, "title", s"$messageKeyPrefix.title")
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view())
          doc.getElementById("cymraeg-switch") != null || !doc.getElementsByAttributeValue("href", "/valuation-office-agency-contact-frontend/language/cymraeg").isEmpty
        }
      }
    }
  }
}
