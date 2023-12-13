/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.JsString
import play.api.mvc.Request
import play.api.test.FakeRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.EnquiryCategoryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
 * @author Yuriy Tumakha
 */
class DataRequestUtilSpec extends AnyFlatSpec with should.Matchers {

  val categories: Seq[String] = Seq("housing_benefit", "fair_rent")

  "isEnquiryCategoryOneOf" should "return true for category 'housing_benefit'" in {
    val userAnswers: UserAnswers = new UserAnswers(CacheMap("",
      Map(EnquiryCategoryId.toString -> JsString("housing_benefit"))))
    implicit val dataRequest: DataRequest[_] = DataRequest(FakeRequest(), "sessionId", userAnswers)

    DataRequestUtil.isEnquiryCategoryOneOf(categories: _*) shouldBe true
  }

  "isEnquiryCategoryOneOf" should "return false for category 'business_rates'" in {
    val userAnswers: UserAnswers = new UserAnswers(CacheMap("",
      Map(EnquiryCategoryId.toString -> JsString("business_rates"))))
    implicit val dataRequest: DataRequest[_] = DataRequest(FakeRequest(), "sessionId", userAnswers)

    DataRequestUtil.isEnquiryCategoryOneOf(categories: _*) shouldBe false
  }

  "isEnquiryCategoryOneOf" should "return false for basic request" in {
    implicit val dataRequest: Request[_] = FakeRequest()

    DataRequestUtil.isEnquiryCategoryOneOf(categories: _*) shouldBe false
  }

}
