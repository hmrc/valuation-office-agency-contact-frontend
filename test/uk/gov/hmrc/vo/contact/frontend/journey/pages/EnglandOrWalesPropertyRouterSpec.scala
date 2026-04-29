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

package uk.gov.hmrc.vo.contact.frontend.journey.pages

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsString, JsValue}
import uk.gov.hmrc.vo.contact.frontend.controllers.routes
import uk.gov.hmrc.vo.contact.frontend.identifiers.BusinessRatesSubcategoryId
import uk.gov.hmrc.vo.contact.frontend.models.{CacheMap, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers

/**
  * @author Yuriy Tumakha
  */
class EnglandOrWalesPropertyRouterSpec extends AnyWordSpec with should.Matchers:

  private val emptyUserAnswers = userAnswers(Map())

  private def userAnswers(data: Map[String, JsValue]) =
    UserAnswers(CacheMap("cacheId", data))

  "EnglandOrWalesPropertyRouter page" should {
    "have correct previous link" in {
      EnglandOrWalesPropertyRouter.previousPage(emptyUserAnswers) shouldBe routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
    }

    "return correct next page for England jurisdiction" in {
      val demolishedPropertyAnswer = userAnswers(Map(
        BusinessRatesSubcategoryId.toString -> JsString("business_rates_change_valuation"),
        EnglandOrWalesPropertyRouter.key    -> JsString("england")
      ))
      EnglandOrWalesPropertyRouter.nextPage(demolishedPropertyAnswer) shouldBe routes.JourneyController.onPageLoad("valuation-online-in-England")
    }

    "return correct next page for Wales jurisdiction" in {
      val propertyAreaChangesInWalesAnswers = userAnswers(Map(
        BusinessRatesSubcategoryId.toString -> JsString("business_rates_changes"),
        EnglandOrWalesPropertyRouter.key    -> JsString("wales")
      ))
      EnglandOrWalesPropertyRouter.nextPage(propertyAreaChangesInWalesAnswers) shouldBe routes.JourneyController.onPageLoad("property-or-area-changed-in-Wales")
      EnglandOrWalesPropertyRouter.nextLang(propertyAreaChangesInWalesAnswers) shouldBe None
    }

    "return start page for empty UserAnswers" in {
      EnglandOrWalesPropertyRouter.nextPage(emptyUserAnswers) shouldBe routes.EnquiryCategoryController.onPageLoad(NormalMode)
    }

    "apply empty page suffix for empty jurisdiction" in {
      val demolishedPropertyAnswer = userAnswers(Map(
        BusinessRatesSubcategoryId.toString -> JsString("business_rates_demolished")
      ))
      EnglandOrWalesPropertyRouter.nextPage(demolishedPropertyAnswer) shouldBe routes.JourneyController.onPageLoad("property-demolished")
    }

    "apply empty page suffix for wrong jurisdiction" in {
      val demolishedPropertyAnswer = userAnswers(Map(
        BusinessRatesSubcategoryId.toString -> JsString("business_rates_demolished"),
        EnglandOrWalesPropertyRouter.key    -> JsString("wrong_jurisdiction")
      ))
      EnglandOrWalesPropertyRouter.nextPage(demolishedPropertyAnswer) shouldBe routes.JourneyController.onPageLoad("property-demolished")
    }

  }
