/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend.views

import play.api.data.Form
import uk.gov.hmrc.vo.contact.frontend.forms.EnquiryCategoryForm
import uk.gov.hmrc.vo.contact.frontend.models.NormalMode
import uk.gov.hmrc.vo.contact.frontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.vo.contact.frontend.views.html.{enquiryCategory => enquiry_category}
import play.twirl.api.HtmlFormat

class EnquiryCategoryViewSpec extends ViewBehaviours {

  def enquiryCategory: html.enquiryCategory = app.injector.instanceOf[enquiry_category]

  val messageKeyPrefix = "enquiry.category"

  def createView: () => HtmlFormat.Appendable =
    () => enquiryCategory(EnquiryCategoryForm.form, NormalMode)(using fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    form => enquiryCategory(form, NormalMode)(using fakeRequest, messages)

  "EnquiryCategory view" must {
    "display the correct browser title" in {
      val doc = asDocument(createView())
      assertEqualsValue(doc, "title", messages(s"$messageKeyPrefix.label") + " - Valuation Office contact form - GOV.UK")
    }

    "display the correct page h1 header" in {
      val doc = asDocument(createView())
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.label")
    }
  }

  "EnquiryCategory view" when {
    "rendered" must {

      "contain continue button with the value Continue" in {
        val doc            = asDocument(createViewUsingForm(EnquiryCategoryForm.form))
        val continueButton = doc.getElementsByClass("govuk-button").first().text()
        assert(continueButton == messages("button.continue.label"))
      }
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(EnquiryCategoryForm.form))
        for ((value, idx) <- EnquiryCategoryForm.values.zipWithIndex) {
          val id = "category" + (if idx == 0 then "" else s"-${idx + 1}")
          assertContainsRadioButton(doc, id, "category", value, false)
        }
      }

      "has a radio button with id 'category' and the label for it" in {
        val id  = "category"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.council_tax.label"))
      }

      "has a radio button with id 'category-2' and the label for it" in {
        val id  = "category-2"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.business_rates.label"))
      }

      "has a radio button with id 'category-3' and the label for it" in {
        val id  = "category-3"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.housing_benefit.label"))
      }

      "has a radio button with id 'category-4' and the label for it" in {
        val id  = "category-4"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.fair_rent.label"))
      }

      "has a radio button with id 'category-5' and the label for it" in {
        val id  = "category-5"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.providing_lettings.label"))

      }

      "has a radio button with id 'category-6' and the label for it" in {
        val id  = "category-6"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.valuations_for_tax.label"))
      }

      "has a radio button with id 'category-7' and the label for it" in {
        val id  = "category-7"
        val doc = asDocument(createView())
        assert(doc.getElementById(id).attr("name") == "category")
        assert(doc.select(s"label[for=$id]").size == 1)
        assertContainsText(doc, messages("enquiry.category.valuation_for_public_body.label"))
      }
    }

    for ((value, idx) <- EnquiryCategoryForm.values.zipWithIndex)
      val id = "category" + (if idx == 0 then "" else s"-${idx + 1}")

      s"rendered with a value of '$value'" must {
        s"have the '$value' radio button selected" in {
          val doc = asDocument(createViewUsingForm(EnquiryCategoryForm.form.bind(Map("category" -> value))))
          assertContainsRadioButton(doc, id, "category", value, true)

          for ((unselectedValue, index) <- EnquiryCategoryForm.values.zipWithIndex.filterNot(_._1 == value))
            val unselectedId = "category" + (if index == 0 then "" else s"-${index + 1}")
            assertContainsRadioButton(doc, unselectedId, "category", unselectedValue, false)
        }
      }
  }
}
